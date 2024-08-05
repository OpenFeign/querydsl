package com.querydsl.scala

import java.io.StringWriter

import com.querydsl.codegen.utils._
import com.querydsl.codegen.utils.model._
import com.querydsl.codegen._
import org.junit.Assert._
import org.junit.{Before, Test}

class ScalaEntitySerializerTest {

  var entityType: EntityType = null

  val writer = new StringWriter()

  @Before
  def setUp() {
    val typeModel = new ClassType(TypeCategory.ENTITY, classOf[Person])
    entityType = new EntityType(typeModel)
    for ( (name, t) <- List(
        ("scalaInt", Types.INT),
        ("javaInt", Types.INTEGER),
        ("javaDouble", Types.DOUBLE),
        ("scalaDouble", Types.DOUBLE_P),
        ("firstName", Types.STRING),
        ("lastName", Types.STRING),
        ("scalaList", new SimpleType(Types.LIST, Types.STRING)),
        ("scalaMap", new SimpleType(Types.MAP, Types.STRING, Types.STRING)),
        ("javaCollection", new SimpleType(Types.COLLECTION, Types.STRING)),
        ("javaSet", new SimpleType(Types.SET, Types.STRING)),
        ("javaList", new SimpleType(Types.LIST, Types.STRING)),
        ("javaMap", new SimpleType(Types.MAP, Types.STRING, Types.STRING)),
        ("listOfPersons", new SimpleType(Types.LIST, entityType)),
        ("array", new ClassType(TypeCategory.ARRAY, classOf[Array[String]])),
        ("other", entityType))) {
      entityType.addProperty(new Property(entityType, name, t))
    }


  }

  @Test
  def Print {
    val typeMappings = ScalaTypeMappings.create
    typeMappings.register(entityType, new QueryTypeFactoryImpl("Q", "", "").create(entityType))
    val serializer = new ScalaEntitySerializer(typeMappings)
    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new ScalaWriter(writer))
    val str = writer.toString
    //System.err.println(str)
    assertTrue(str.contains("class QPerson(cl: Class[_ <: Person], md: PathMetadata) " +
            "extends EntityPathImpl[Person](cl, md) {"))
    assertTrue(str.contains("def this(variable: String) = " +
            "this(classOf[Person], forVariable(variable))"))
    assertTrue(str.contains("def this(parent: Path[_], variable: String) = " +
            "this(classOf[Person], forProperty(parent, variable))"))
  }

  @Test
  def Compile {
    val typeMappings = ScalaTypeMappings.create
    typeMappings.register(entityType, new QueryTypeFactoryImpl("Q", "", "").create(entityType))
    val serializer = new ScalaEntitySerializer(typeMappings)
    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new ScalaWriter(writer))
    val str = writer.toString
    //System.err.println(str);
    CompileTestUtils.assertCompileSuccess(str)
  }
}