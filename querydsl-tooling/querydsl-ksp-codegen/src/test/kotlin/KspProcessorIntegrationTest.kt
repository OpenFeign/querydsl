import com.querydsl.ksp.codegen.QueryDslProcessorProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalCompilerApi::class)
class KspProcessorIntegrationTest {

    @Test
    fun kotlinEntity_generatesQClassUnderKsp2() {
        val source = SourceFile.kotlin(
            "User.kt",
            """
            package test

            import jakarta.persistence.Entity
            import jakarta.persistence.Id

            @Entity
            class User {
                @Id
                var id: Long = 0
                var name: String = ""
                var active: Boolean = false
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(source)

        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).doesNotContain("KaInvalidLifetimeOwnerAccessException")

        val qUser = generatedDir.findGenerated("QUser.kt")
        assertThat(qUser.readText())
            .contains("class QUser")
            .contains("createNumber(\"id\"")
            .contains("createString(\"name\")")
            .contains("createBoolean(\"active\")")
    }

    @Test
    fun javaEntity_generatesQClass() {
        val source = SourceFile.java(
            "User.java",
            """
            package test;

            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            import jakarta.persistence.Transient;

            @Entity
            public class User {
                @Id
                public Long id;
                public String name;
                public boolean active;
                @Transient
                public String tempField;
                public static final String CONSTANT = "x";
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(source)

        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qUser = generatedDir.findGenerated("QUser.kt").readText()
        assertThat(qUser)
            .contains("class QUser")
            .contains("createNumber(\"id\"")
            .contains("createString(\"name\")")
            .contains("createBoolean(\"active\")")
            // @Transient field excluded
            .doesNotContain("\"tempField\"")
            // static final excluded
            .doesNotContain("\"CONSTANT\"")
    }

    @Test
    fun objectReferences_areLazyAndNonNull_otherFieldsAreJvmField() {
        // Hybrid rendering:
        //  * scalar paths, _super, and inherited properties → eager @JvmField
        //    (Java consumers can do qFoo.actived as field-chained access)
        //  * @ManyToOne / object refs → `by lazy`, non-null type
        //    (handles self-references without StackOverflow at construction;
        //    Kotlin queries get non-null `qFoo.evaluation` without `?.`)
        val parent = SourceFile.java(
            "Quality.java",
            """
            package test;
            import jakarta.persistence.MappedSuperclass;
            @MappedSuperclass
            public class Quality {
                public boolean actived;
            }
            """.trimIndent()
        )
        val child = SourceFile.java(
            "Branch.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            import jakarta.persistence.ManyToOne;
            @Entity
            public class Branch extends Quality {
                @Id public Long id;
                public String name;
                @ManyToOne private Branch parent;  // self-reference
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(parent, child)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qBranch = generatedDir.findGenerated("QBranch.kt").readText()
        assertThat(qBranch)
            // _super eager @JvmField
            .contains("@JvmField")
            .contains("public val _super: QQuality = test.QQuality(this)")
            // Inherited property eager @JvmField
            .contains("public val actived: BooleanPath = _super.actived")
            // Scalar path eager @JvmField
            .contains("public val name: StringPath = createString(\"name\")")
            // Self-reference lazy + non-null (no `?` on the type, no
            // construction-time recursion)
            .contains("public val parent: QBranch by lazy")
            .doesNotContain("public val parent: QBranch?")
            // The synthesised JVM getter is renamed to drop the `get` prefix
            // so Java callers can write `qBranch.parent()` instead of
            // `qBranch.getParent()`.
            .contains("@get:JvmName(\"parent\")")
    }

    @Test
    fun javaEntity_extendsJavaMappedSuperclass() {
        val parent = SourceFile.java(
            "Animal.java",
            """
            package test;
            import jakarta.persistence.MappedSuperclass;

            @MappedSuperclass
            public class Animal {
                public Long age;
            }
            """.trimIndent()
        )
        val child = SourceFile.java(
            "Cat.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;

            @Entity
            public class Cat extends Animal {
                @Id
                public Long id;
                public String name;
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(parent, child)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qCat = generatedDir.findGenerated("QCat.kt").readText()
        assertThat(qCat)
            .contains("class QCat")
            .contains("createString(\"name\")")
            .contains("_super: QAnimal")
        assertThat(generatedDir.findGenerated("QAnimal.kt").readText())
            .contains("createNumber(\"age\"")
    }

    @Test
    fun entityWithCollectionProperty_doesNotCrashOnParameterizedSupertype() {
        // Regression: TypeExtractor.fallbackType used to call KSType.toClassName()
        // on every supertype returned by getAllSuperTypes(). For an entity with a
        // Set<String> / List<X> property whose element type falls through to
        // fallbackType (e.g. an enum-like or custom wrapper), getAllSuperTypes()
        // includes parameterized Collection<E> / Iterable<E>, and KotlinPoet rejects
        // parameterized KSType with IllegalStateException("KSType '...' has type
        // arguments"). See OpenFeign/querydsl#1688.
        val source = SourceFile.kotlin(
            "Foo.kt",
            """
            package test

            import jakarta.persistence.ElementCollection
            import jakarta.persistence.Entity
            import jakarta.persistence.Id

            @Entity
            class Foo {
                @Id
                var id: Long = 0
                @ElementCollection
                var tags: MutableSet<String> = mutableSetOf()
                @ElementCollection
                var labels: MutableList<String> = mutableListOf()
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(source)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages)
            .doesNotContain("has type arguments, which are not supported for ClassName conversion")

        val qFoo = generatedDir.findGenerated("QFoo.kt").readText()
        assertThat(qFoo)
            .contains("class QFoo")
            .contains("createSet(\"tags\"")
            .contains("createList(\"labels\"")
    }

    @Test
    fun javaEntityWithCollectionField_emitsCollectionPath() {
        // Java entities frequently declare @OneToMany fields as Collection<X> or
        // Iterable<X> rather than Set<X>/List<X>. collectionType() previously only
        // recognised Set/List/Map and fell through to fallbackType for plain
        // Collection — which then emitted SimplePath<MutableCollection> (no type
        // arg → invalid Kotlin).
        val degree = SourceFile.java(
            "Degree.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            @Entity
            public class Degree {
                @Id public Long id;
                public String name;
            }
            """.trimIndent()
        )
        val learner = SourceFile.java(
            "Learner.java",
            """
            package test;

            import java.util.Collection;
            import java.util.HashSet;
            import jakarta.persistence.CascadeType;
            import jakarta.persistence.Entity;
            import jakarta.persistence.FetchType;
            import jakarta.persistence.Id;
            import jakarta.persistence.OneToMany;

            @Entity
            public class Learner {
                @Id
                public Long id;

                @OneToMany(mappedBy = "learner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
                public Collection<Degree> degrees = new HashSet<>();
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(degree, learner)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qLearner = generatedDir.findGenerated("QLearner.kt").readText()
        assertThat(qLearner)
            .contains("CollectionPath<Degree, QDegree>")
            .contains("createCollection(\"degrees\", test.Degree::class.java, test.QDegree::class.java, null)")
            // No raw MutableCollection without a type argument
            .doesNotContain("SimplePath<MutableCollection>")
    }

    @Test(timeout = 30_000)
    fun bidirectionalEntities_loadConcurrentlyWithoutDeadlock() {
        // Regression: two JPA entities with mutual @ManyToOne refs (Foo has
        // a Bar, Bar has a Foo) are the canonical case where APT-generated
        // Q-classes can deadlock when their static initialisers fire
        // concurrently — each <clinit> eagerly constructs the peer Q-class,
        // and the two class-init locks are taken in opposite orders. See
        // OpenFeign/querydsl#1739 for the APT-side compile-time detector.
        // The KSP rendering emits object-reference fields as `by lazy`, so
        // neither Q-class's <clinit> touches the other transitively. This
        // test pins that property — both statically (the generated source
        // uses `by lazy`) and dynamically (force concurrent first access
        // and assert neither thread hangs).
        val foo = SourceFile.java(
            "Foo.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            import jakarta.persistence.ManyToOne;
            @Entity
            public class Foo {
                @Id public Long id;
                @ManyToOne public Bar bar;
            }
            """.trimIndent()
        )
        val bar = SourceFile.java(
            "Bar.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            import jakarta.persistence.ManyToOne;
            @Entity
            public class Bar {
                @Id public Long id;
                @ManyToOne public Foo foo;
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(foo, bar)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        // Static assertion: both peer references render lazy in the source.
        assertThat(generatedDir.findGenerated("QFoo.kt").readText())
            .contains("public val bar: QBar by lazy")
        assertThat(generatedDir.findGenerated("QBar.kt").readText())
            .contains("public val foo: QFoo by lazy")

        // Dynamic assertion: load the Class objects without initialising
        // them (so <clinit> hasn't run yet), then race two threads to touch
        // QFoo.foo and QBar.bar at the same time. If either <clinit> tried
        // to construct the peer Q-class, the two threads would deadlock on
        // the opposing class-init locks and the JUnit timeout would fire.
        val cl = result.classLoader
        val qFooClass = Class.forName("test.QFoo", false, cl)
        val qBarClass = Class.forName("test.QBar", false, cl)

        val startLatch = CountDownLatch(1)
        val fooResult = AtomicReference<Any?>(null)
        val barResult = AtomicReference<Any?>(null)
        val errors = ConcurrentLinkedQueue<Throwable>()

        val threadFoo = Thread({
            try {
                startLatch.await()
                fooResult.set(qFooClass.getField("foo").get(null))
            } catch (t: Throwable) {
                errors.add(t)
            }
        }, "init-QFoo")
        val threadBar = Thread({
            try {
                startLatch.await()
                barResult.set(qBarClass.getField("bar").get(null))
            } catch (t: Throwable) {
                errors.add(t)
            }
        }, "init-QBar")

        threadFoo.start()
        threadBar.start()
        startLatch.countDown()
        threadFoo.join(10_000)
        threadBar.join(10_000)

        assertThat(threadFoo.isAlive)
            .withFailMessage("init-QFoo never completed — likely <clinit> deadlock with QBar")
            .isFalse()
        assertThat(threadBar.isAlive)
            .withFailMessage("init-QBar never completed — likely <clinit> deadlock with QFoo")
            .isFalse()
        assertThat(errors).isEmpty()
        assertThat(fooResult.get()).isNotNull
        assertThat(barResult.get()).isNotNull
    }

    @Test
    fun inheritedObjectReference_doesNotRecurseAtConstruction() {
        // Regression: an `@MappedSuperclass` that `@ManyToOne`s back to a
        // concrete entity which itself extends that superclass — e.g. an
        // Auditable base with `createdBy : User` applied to `User` — used to
        // stack-overflow at QUser.user construction. The eager
        // `@JvmField val createdBy = _super.createdBy` on the child forced
        // the parent's per-instance `by lazy` to evaluate during the child's
        // ctor, which allocated another QUser, which allocated another
        // QAuditable, ad infinitum. Inherited object references are now
        // rendered as `by lazy` themselves, so first construction terminates
        // and only user-driven navigation creates further levels.
        val auditable = SourceFile.java(
            "Auditable.java",
            """
            package test;
            import jakarta.persistence.ManyToOne;
            import jakarta.persistence.MappedSuperclass;
            @MappedSuperclass
            public abstract class Auditable {
                @ManyToOne public User createdBy;
            }
            """.trimIndent()
        )
        val user = SourceFile.java(
            "User.java",
            """
            package test;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;
            @Entity
            public class User extends Auditable {
                @Id public Long id;
                public String name;
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(auditable, user)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qUser = generatedDir.findGenerated("QUser.kt").readText()
        // The inherited createdBy must be a lazy delegate, not an eager
        // @JvmField — otherwise QUser.user would StackOverflow on first access.
        assertThat(qUser)
            .contains("public val createdBy: QUser by lazy")
            .contains("@get:JvmName(\"createdBy\")")
            .doesNotContain("public val createdBy: QUser = _super.createdBy")

        // And — the real proof — load QUser.user from the compiled output
        // and verify the static init does not StackOverflowError.
        val qUserClass = result.classLoader.loadClass("test.QUser")
        val userInstance = qUserClass.getField("user").get(null)
        assertThat(userInstance).isNotNull
    }

    @Test
    fun convertAnnotatedField_doesNotCrashUnderKsp2() {
        // Regression: TypeExtractor.userType used to call
        // `it.annotationType.resolve().toClassName()` on every property's
        // annotations, going through kotlinpoet-ksp's isError()/declaration
        // path that traverses KSP2's analysis-API lifetime tokens. Any field
        // carrying a JPA @Convert (or hibernate @Type / @JdbcTypeCode)
        // annotation crashed kspKotlin with KaInvalidLifetimeOwnerAccessException.
        val converter = SourceFile.kotlin(
            "Converter.kt",
            """
            package test
            import jakarta.persistence.AttributeConverter

            class StringPair(val first: String, val second: String)

            class StringPairConverter : AttributeConverter<StringPair, String> {
                override fun convertToDatabaseColumn(p: StringPair) = "${'$'}{p.first}|${'$'}{p.second}"
                override fun convertToEntityAttribute(s: String): StringPair {
                    val parts = s.split("|")
                    return StringPair(parts[0], parts[1])
                }
            }
            """.trimIndent()
        )
        val entity = SourceFile.kotlin(
            "Bag.kt",
            """
            package test
            import jakarta.persistence.Convert
            import jakarta.persistence.Entity
            import jakarta.persistence.Id

            @Entity
            class Bag {
                @Id var id: Long = 0
                @Convert(converter = StringPairConverter::class)
                var pair: StringPair = StringPair("", "")
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(converter, entity)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages)
            .doesNotContain("KaInvalidLifetimeOwnerAccessException")

        val qBag = generatedDir.findGenerated("QBag.kt").readText()
        // @Convert-annotated field is rendered as a SimplePath<UserType>.
        assertThat(qBag)
            .contains("class QBag")
            .contains("createSimple(\"pair\"")
            .contains("StringPair")
    }

    @Test
    fun referencedClassWithSameSimpleNameAnnotation_isNotMistakenForJpaEntity() {
        // Regression: QueryModelType.autodetect used to compare KSAnnotation by
        // simpleName only. That's safe for *discovered* symbols (KSP already
        // FQN-filters via getSymbolsWithAnnotation), but it's also called on
        // *referenced* classes during property type resolution. A user class
        // annotated with their own @com.example.Entity (NOT JPA) referenced
        // from a JPA entity would be misclassified as a Querydsl entity and
        // emit a phantom QFooClass(...) reference that doesn't compile.
        val customAnnotation = SourceFile.kotlin(
            "CustomEntity.kt",
            """
            package custom
            // A user-defined annotation that happens to share the JPA simple name.
            annotation class Entity
            """.trimIndent()
        )
        val referenced = SourceFile.kotlin(
            "Helper.kt",
            """
            package test
            @custom.Entity
            class Helper(val label: String)
            """.trimIndent()
        )
        val entity = SourceFile.kotlin(
            "Holder.kt",
            """
            package test
            import jakarta.persistence.Entity
            import jakarta.persistence.Id
            import jakarta.persistence.Transient

            @Entity
            class Holder {
                @Id var id: Long = 0
                // Not persisted; we just want it as a Kotlin property type so
                // that referenceType() examines its class.
                @Transient var helper: Helper = Helper("x")
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(customAnnotation, referenced, entity)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qHolder = generatedDir.findGenerated("QHolder.kt").readText()
        // Helper must NOT be treated as a Querydsl entity (no QHelper reference).
        assertThat(qHolder).doesNotContain("QHelper")
        // No phantom Q-class generated for the non-JPA-annotated class either.
        val generatedNames = generatedDir.walkTopDown().filter { it.isFile }.map { it.name }.toList()
        assertThat(generatedNames).doesNotContain("QHelper.kt")
    }

    @Test
    fun customComparableWrapper_emitsComparablePath() {
        // A custom Comparable<X> wrapper (typed-value-querydsl-style) — declaration
        // implements a parameterized Comparable<X> in its supertype chain. Must still
        // be detected as Comparable and produce ComparablePath rather than SimplePath,
        // even though the supertype is parameterized.
        val source = SourceFile.kotlin(
            "Wrapped.kt",
            """
            package test

            import jakarta.persistence.Entity
            import jakarta.persistence.Id

            class TypedId(val value: Long) : Comparable<TypedId> {
                override fun compareTo(other: TypedId) = value.compareTo(other.value)
            }

            @Entity
            class Wrapped {
                @Id
                var id: TypedId = TypedId(0)
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(source)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        val qWrapped = generatedDir.findGenerated("QWrapped.kt").readText()
        assertThat(qWrapped)
            .contains("class QWrapped")
            .contains("createComparable(\"id\"")
    }

    @Test
    fun mixedSources_kotlinEntityReferencesJavaEmbeddable() {
        val javaEmbeddable = SourceFile.java(
            "Address.java",
            """
            package test;
            import jakarta.persistence.Embeddable;

            @Embeddable
            public class Address {
                public String street;
                public String city;
            }
            """.trimIndent()
        )
        val kotlinEntity = SourceFile.kotlin(
            "Person.kt",
            """
            package test

            import jakarta.persistence.Embedded
            import jakarta.persistence.Entity
            import jakarta.persistence.Id

            @Entity
            class Person {
                @Id
                var id: Long = 0
                var name: String = ""
                @Embedded
                var address: Address = Address()
            }
            """.trimIndent()
        )

        val (result, generatedDir) = compile(javaEmbeddable, kotlinEntity)
        assertThat(result.exitCode)
            .withFailMessage(result.messages)
            .isEqualTo(KotlinCompilation.ExitCode.OK)

        assertThat(generatedDir.findGenerated("QPerson.kt").readText())
            .contains("class QPerson")
            .contains("createString(\"name\")")
        assertThat(generatedDir.findGenerated("QAddress.kt").readText())
            .contains("createString(\"street\")")
            .contains("createString(\"city\")")
    }

    private fun compile(vararg sources: SourceFile): Pair<JvmCompilationResult, File> {
        val compilation = KotlinCompilation().apply {
            this.sources = sources.toList()
            inheritClassPath = true
            // kctfork 0.10+ runs KSP2 only; no useKsp2 toggle exists.
            configureKsp {
                symbolProcessorProviders += QueryDslProcessorProvider()
                incremental = false
            }
        }
        return compilation.compile() to compilation.kspSourcesDir
    }

    private fun File.findGenerated(name: String): File {
        val match = walkTopDown().firstOrNull { it.isFile && it.name == name }
        assertThat(match)
            .withFailMessage {
                val all = walkTopDown().filter { it.isFile }.joinToString("\n  ") { it.relativeTo(this).path }
                "Expected $name under $this; generated files were:\n  $all"
            }
            .isNotNull
        return match!!
    }
}
