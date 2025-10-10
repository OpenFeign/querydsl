import com.querydsl.example.ksp.Bear
import com.querydsl.example.ksp.BearSpecies
import com.querydsl.example.ksp.Cat
import com.querydsl.example.ksp.CatType
import com.querydsl.example.ksp.Dog
import com.querydsl.example.ksp.Email
import com.querydsl.example.ksp.Person
import com.querydsl.example.ksp.QBear
import com.querydsl.example.ksp.QBearSimplifiedProjection
import com.querydsl.example.ksp.QCat
import com.querydsl.example.ksp.QDog
import com.querydsl.example.ksp.QMyShape
import com.querydsl.example.ksp.QPerson
import com.querydsl.example.ksp.QPersonClassDTO
import com.querydsl.example.ksp.QPersonClassConstructorDTO
import com.querydsl.example.ksp.Tag
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.hibernate.cfg.AvailableSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.hibernate.cfg.Configuration
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.Test

class Tests {
    @Test
    fun `select entity` () {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Person(424, "John Smith", Email("abc@domain.com")))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QPerson.person
            val person = queryFactory
                .selectFrom(q)
                .where(q.name.eq("John Smith"), q.email.eq("abc@domain.com"))
                .fetchOne()
            if (person == null) {
                fail<Any>("No person was returned")
            } else {
                assertThat(person.id).isEqualTo(424)
            }
            em.close()
        }
    }

    @Test
    fun `create cat with owner` () {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            val catOwner = Person(425, "Percy Bysshe Catownerly", Email("abc@domain.com"))
            em.persist(catOwner)
            em.persist(Cat(103, "Samuel Taylor Cattingridge", false, "Not so fluffy", catOwner, CatType.MAINE_COON))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QCat.cat
            val catWithOwner = queryFactory
                .selectFrom(q)
                .where(q.name.eq("Samuel Taylor Cattingridge"))
                .fetchOne()
            if (catWithOwner == null) {
                fail<Any>("No owned cat was returned")
            } else {
                assertThat(catWithOwner.id).isEqualTo(103)
                assertThat(catWithOwner.catType).isEqualTo(CatType.MAINE_COON)
                assertThat(catWithOwner.owner?.id).isEqualTo(425)
            }
            em.close()
        }
    }

    @Test
    fun `filter entities`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Cat(100, "Neville Furbottom", true, "Quite Fluffy"))
            em.persist(Cat(101, "Edgar Allan Paw", true, null))
            em.persist(Cat(102, "Admiral Meowington", false))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QCat.cat
            val animals = queryFactory
                .selectFrom(q)
                .where(q.isTailUpright.isTrue)
                .fetch()
                .map { it.name }
            assertThat(animals.size).isEqualTo(2)
            assertThat(animals).contains("Neville Furbottom")
            assertThat(animals).contains("Edgar Allan Paw")
            val quiteFluffyAnimals = queryFactory
                .selectFrom(q)
                .where(q.fluffiness.eq("Quite Fluffy"))
                .fetch()
                .map { it.name }
            assertThat(quiteFluffyAnimals.size).isEqualTo(1)
            assertThat(quiteFluffyAnimals).contains("Neville Furbottom")
            em.close()
        }
    }

    @Test
    fun `select dto`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Person(424, "John Smith", Email("abc@domain.com")))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QPerson.person
            val personDTO = queryFactory
                .select(QPersonClassConstructorDTO(q.id, q.name))
                .from(q)
                .where(q.name.eq("John Smith"))
                .fetchOne()
            if (personDTO == null) {
                fail<Any>("No personDTO was returned")
            } else {
                assertThat(personDTO.id).isEqualTo(424)
                assertThat(personDTO.name).isEqualTo("John Smith")
            }
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QPerson.person
            val personDTO = queryFactory
                .select(QPersonClassDTO(q.id, q.name))
                .from(q)
                .where(q.name.eq("John Smith"))
                .fetchOne()
            if (personDTO == null) {
                fail<Any>("No personDTO was returned")
            } else {
                assertThat(personDTO.id).isEqualTo(424)
                assertThat(personDTO.name).isEqualTo("John Smith")
            }
            em.close()
        }
    }

    @Test
    fun `query projection with excluded property`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Bear(424, "Winnie", "The forest", null))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QBear.bear
            val bearProjection = queryFactory
                .select(QBearSimplifiedProjection(q.id, q.name))
                .from(q)
                .where(q.name.eq("Winnie"))
                .fetchOne()
            if (bearProjection == null) {
                fail<Any>("No bear was returned")
            } else {
                assertThat(bearProjection.id).isEqualTo(424)
                assertThat(bearProjection.name).isEqualTo("Winnie")
            }
            em.close()
        }
    }

    @Test
    fun `query projection exclude property from constructor`() {
        assertThat(Bear::class.declaredMemberProperties.count()).isEqualTo(4)
        assertThat(QBearSimplifiedProjection::class.primaryConstructor!!.parameters.count()).isEqualTo(2)
    }

    @Test
    fun `enum with convert annotation generates EnumPath`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Bear(501, "Baloo", "Jungle", null))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QBear.bear

            val bears = queryFactory
                .select(q.species.coalesce(BearSpecies.BLACK_BEAR))
                .from(q)
                .orderBy(q.id.asc())
                .fetch()

            assertThat(bears.size).isEqualTo(1)
            assertThat(bears[0]).isEqualTo(BearSpecies.BLACK_BEAR)

            em.close()
        }
    }

    @Test
    fun `create and query dog with jsonb tag`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()

            val dogTag = Tag(
                id = 10,
                name = "Playful"
            )
            em.persist(Dog(300, "Buddy", dogTag))

            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QDog.dog

            val dog = queryFactory
                .selectFrom(q)
                .where(q.name.eq("Buddy"))
                .fetchOne()

            if (dog == null) {
                fail<Any>("No dog was returned")
            } else {
                assertThat(dog.id).isEqualTo(300)
                assertThat(dog.name).isEqualTo("Buddy")
                assertThat(dog.tag.id).isEqualTo(10)
                assertThat(dog.tag.name).isEqualTo("Playful")
            }

            em.close()
        }
    }

	@Test
	fun ensureCorrectGeoType() {
		val departureProperty = QMyShape::class.memberProperties.single { it.name == "departureGeo" }
		assertThat(departureProperty.returnType.jvmErasure.qualifiedName!!).isEqualTo("com.querydsl.spatial.locationtech.jts.JTSGeometryPath")
		assertThat(departureProperty.returnType.arguments.single().type!!.jvmErasure.qualifiedName!!).isEqualTo("org.locationtech.jts.geom.Geometry")
	}

    private fun initialize(): EntityManagerFactory {
        val configuration = Configuration()
            .setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, org.h2.Driver::class.qualifiedName!!)
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:my-database;")
            .setProperty(AvailableSettings.HBM2DDL_AUTO, "create-drop")
            .setProperty(AvailableSettings.SHOW_SQL, "true")
            .addAnnotatedClass(Person::class.java)
            .addAnnotatedClass(Cat::class.java)
            .addAnnotatedClass(Dog::class.java)
            .addAnnotatedClass(Bear::class.java)

        return configuration
            .buildSessionFactory()
            .unwrap(EntityManagerFactory::class.java)
    }
}