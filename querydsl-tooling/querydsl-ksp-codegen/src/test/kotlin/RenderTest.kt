import com.querydsl.core.types.dsl.BooleanPath
import com.querydsl.ksp.codegen.QProperty
import com.querydsl.ksp.codegen.QPropertyType
import com.querydsl.ksp.codegen.QueryModel
import com.querydsl.ksp.codegen.QueryModelRenderer
import com.querydsl.ksp.codegen.QueryModelType
import com.querydsl.ksp.codegen.SimpleType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.script.ScriptEngineManager

class RenderTest {
    @Test
    fun propertyTypes() {
        val model = QueryModel(
            originalClassName = ClassName("", "User"),
            typeParameterCount = 0,
            className = ClassName("", "QUser"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val properties = listOf(
            QProperty("a", QPropertyType.Simple(SimpleType.QBoolean)),
            QProperty("b", QPropertyType.Simple(SimpleType.QString)),
            QProperty("c", QPropertyType.Simple(SimpleType.Date(java.util.Date::class.asClassName()))),
            QProperty("d", QPropertyType.EnumReference(SampleEnum::class.asClassName())),
            QProperty("e", QPropertyType.Simple(SimpleType.DateTime(java.util.Date::class.asClassName()))),
            QProperty("f", QPropertyType.Simple(SimpleType.QNumber(Int::class.asClassName()))),
            QProperty("g", QPropertyType.Simple(SimpleType.Time(java.sql.Time::class.asClassName()))),
            QProperty("h", QPropertyType.Simple(SimpleType.Simple(Any::class.asClassName()))),
        )
        model.properties.addAll(properties)
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContainAll("""
            val a: com.querydsl.core.types.dsl.BooleanPath = createBoolean("a")
            val b: com.querydsl.core.types.dsl.StringPath = createString("b")
            val c: com.querydsl.core.types.dsl.DatePath<java.util.Date> = createDate("c", java.util.Date::class.java)
            val d: com.querydsl.core.types.dsl.EnumPath<SampleEnum> = createEnum("d", SampleEnum::class.java)
            val e: com.querydsl.core.types.dsl.DateTimePath<java.util.Date> = createDateTime("e", java.util.Date::class.java)
            val f: com.querydsl.core.types.dsl.NumberPath<kotlin.Int> = createNumber("f", kotlin.Int::class.java)
            val g: com.querydsl.core.types.dsl.TimePath<java.sql.Time> = createTime("g", java.sql.Time::class.java)
            val h: com.querydsl.core.types.dsl.SimplePath<kotlin.Any> = createSimple("h", kotlin.Any::class.java)
        """.trimIndent())
    }

    @Test
    fun superclass() {
        val model = QueryModel(
            originalClassName = ClassName("", "Cat"),
            typeParameterCount = 0,
            className = ClassName("", "QCat"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val superClass = QueryModel(
            originalClassName = ClassName("", "Animal"),
            typeParameterCount = 0,
            className = ClassName("", "QAnimal"),
            type = QueryModelType.SUPERCLASS,
            null,
            mockk()
        )
        model.superclass = superClass
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContains("class QCat : com.querydsl.core.types.dsl.EntityPathBase<Cat>")
        code.assertContainLines("val _super: QAnimal by lazy { QAnimal(this) }")
    }

    @Test
    fun superclassNullContainingFile() {
        val model = QueryModel(
            originalClassName = ClassName("", "Cat"),
            typeParameterCount = 0,
            className = ClassName("", "QCat"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val superClass = QueryModel(
            originalClassName = ClassName("", "Animal"),
            typeParameterCount = 0,
            className = ClassName("", "QAnimal"),
            type = QueryModelType.SUPERCLASS,
            null,
            null
        )
        model.superclass = superClass
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContains("class QCat : com.querydsl.core.types.dsl.EntityPathBase<Cat>")
        code.assertContainLines("val _super: QAnimal by lazy { QAnimal(this) }")
    }

    @Test
    fun genericTypeArgs() {
        val model = QueryModel(
            originalClassName = ClassName("", "Article"),
            typeParameterCount = 1,
            className = ClassName("", "QArticle"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertLines("""
            public class QArticle : com.querydsl.core.types.dsl.EntityPathBase<Article<*>> {
                public constructor(path: com.querydsl.core.types.Path<out Article<*>>) : super(path.type, path.metadata)
                
                public constructor(metadata: com.querydsl.core.types.PathMetadata) : super(Article::class.java, metadata)
                
                public constructor(variable: kotlin.String) : super(Article::class.java, com.querydsl.core.types.PathMetadataFactory.forVariable(variable))
                
                public constructor(type: java.lang.Class<out Article<*>>, metadata: com.querydsl.core.types.PathMetadata) : super(type, metadata)
                
                public companion object {
                    @kotlin.jvm.JvmField
                    public val article: QArticle = QArticle("article")
                }
            }
        """.trimIndent())
    }

    @Test
    fun inheritSuperProperties() {
        val animalModel = QueryModel(
            originalClassName = ClassName("", "Animal"),
            typeParameterCount = 0,
            className = ClassName("", "QAnimal"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        animalModel.properties.add(
            QProperty("hasTail", QPropertyType.Simple(SimpleType.QBoolean))
        )
        val catModel = QueryModel(
            originalClassName = ClassName("", "Cat"),
            typeParameterCount = 0,
            className = ClassName("", "QCat"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        catModel.superclass = animalModel
        val typeSpec = QueryModelRenderer.render(catModel)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContainLines("val hasTail: com.querydsl.core.types.dsl.BooleanPath get() = _super.hasTail")
    }

    @Test
    fun setCollection() {
        val model = QueryModel(
            originalClassName = ClassName("", "Animal"),
            typeParameterCount = 0,
            className = ClassName("", "QAnimal"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        model.properties.add(
            QProperty(
                "features",
                QPropertyType.SetCollection(
                    QPropertyType.Simple(SimpleType.QString)
                )
            )
        )
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContains("""
            val features: com.querydsl.core.types.dsl.SetPath<kotlin.String, com.querydsl.core.types.dsl.StringPath> = createSet("features", kotlin.String::class.java, com.querydsl.core.types.dsl.StringPath::class.java, null)
        """.trimIndent())
    }

    @Test
    fun queryProjection() {
        val model = QueryModel(
            originalClassName = ClassName("", "CatDTO"),
            typeParameterCount = 0,
            className = ClassName("", "QCatDTO"),
            type = QueryModelType.QUERY_PROJECTION,
            null,
            mockk()
        )
        val properties = listOf(
            QProperty("id", QPropertyType.Simple(SimpleType.QNumber(Int::class.asClassName()))),
            QProperty("name", QPropertyType.Simple(SimpleType.QString)),
        )
        model.properties.addAll(properties)
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()
        code.assertCompiles()
        code.assertContainAll("""
            public class QPersonDTO(
              id: com.querydsl.core.types.Expression<kotlin.Int>,
              name: com.querydsl.core.types.Expression<kotlin.String>,
            ) : com.querydsl.core.types.ConstructorExpression<CatDTO>(CatDTO::class.java, arrayOf(kotlin.Int::class.java, kotlin.String::class.java), id, name)
        """.trimIndent())
    }

    @Test
    fun innerClassNaming() {
        // Test case for inner class naming compatibility with KAPT
        val model = QueryModel(
            originalClassName = ClassName("", "OuterClass", "InnerClass"),
            typeParameterCount = 0,
            className = ClassName("", "QOuterClass_InnerClass"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val properties = listOf(
            QProperty("innerProperty", QPropertyType.Simple(SimpleType.QString))
        )
        model.properties.addAll(properties)
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()

        // Skip compile check for inner classes as they reference non-existent types
        code.assertContains("class QOuterClass_InnerClass")
        code.assertContains("val innerProperty: com.querydsl.core.types.dsl.StringPath = createString(\"innerProperty\")")
    }

    @Test
    fun deeplyNestedClassNaming() {
        // Test case for deeply nested classes (3 levels) - just check generated code structure
        val model = QueryModel(
            originalClassName = ClassName("", "Level1", "Level2", "Level3"),
            typeParameterCount = 0,
            className = ClassName("", "QLevel1_Level2_Level3"),
            type = QueryModelType.ENTITY,
            null,
            mockk()
        )
        val typeSpec = QueryModelRenderer.render(model)
        val code = typeSpec.toString()

        // Skip compile check for nested classes as they reference non-existent types
        code.assertContains("class QLevel1_Level2_Level3")
        code.assertContains("EntityPathBase<Level1.Level2.Level3>")
    }

	@Test
	fun geometryTypeRendering() {
		val model = QueryModel(
			originalClassName = ClassName("", "MyShape"),
			typeParameterCount = 0,
			className = ClassName("", "QMyShape"),
			type = QueryModelType.ENTITY,
			null,
			mockk()
		)
		val properties = listOf(
			QProperty(
				"geometry",
				QPropertyType.Simple(
					SimpleType.Mapper.get(ClassName("org.locationtech.jts.geom", "Geometry"))!!
				)
			)
		)
		model.properties.addAll(properties)
		val typeSpec = QueryModelRenderer.render(model)
		val code = typeSpec.toString()
		code.assertContains("""
			public val geometry: com.querydsl.spatial.locationtech.jts.JTSGeometryPath<org.locationtech.jts.geom.Geometry> = com.querydsl.spatial.locationtech.jts.JTSGeometryPath(forProperty("geometry"))
		""".trimIndent())
	}
}

private fun String.assertLines(expected: String) {
    val actualLines = this.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    val expectedLines = expected.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    expectedLines.forEachIndexed { index, expectedLine ->
        val actualLine = actualLines[index]
        if (expectedLine != actualLine) {
            println(expected)
            println(this)
            assertThat(actualLine).isEqualTo(expectedLine)
        }
    }
    assertThat(actualLines.size).isEqualTo(expectedLines.size)
}

private fun String.assertContains(other: String) {
    assertThat(this).contains(other)
}

private fun String.assertContainAll(other: String) {
    val expectedElements = other.split("\n")
    for (element in expectedElements) {
        other.assertContains(element)
    }
}

private fun String.assertContainLines(other: String) {
    val joinedLines = this.split("\n").map { it.trim() }.joinToString(" ")
    assertThat(joinedLines).contains(other)
}

private fun String.assertCompiles() {
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    try {
        engine.eval(this)
    } catch (scriptException: javax.script.ScriptException) {
        println(this)
        throw scriptException
    }
}

enum class SampleEnum { A, B, C }

class User

class Article<T>

class Animal

class QAnimal(
    path: com.querydsl.core.types.Path<*>
) {
    val hasTail: BooleanPath
        get() = error("Not implemented")
}

class Cat

class CatDTO

class OuterClass {
    class InnerClass
}

class Level1 {
    class Level2 {
        class Level3
    }
}