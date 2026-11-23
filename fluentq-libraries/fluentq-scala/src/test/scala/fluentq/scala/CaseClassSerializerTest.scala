package fluentq.scala

import java.io.StringWriter

import fluentq.codegen.utils._
import fluentq.codegen._
import org.junit.jupiter.api._

class CaseClassSerializerTest {

  val typeMappings = ScalaTypeMappings.create

  var entityType = EntityTypes.entityType

  var writer = new StringWriter()

  @Test
  def Print: Unit = {
    val serializer = new CaseClassSerializer(typeMappings)
    typeMappings.register(entityType, new QueryTypeFactoryImpl("Q", "", "").create(entityType))
    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new ScalaWriter(writer))

    //println(writer.toString)
  }

  @Test
  def Compile: Unit = {
    val serializer = new CaseClassSerializer(typeMappings)
    serializer.createCompanionObject = false
    typeMappings.register(entityType, new QueryTypeFactoryImpl("Q", "", "").create(entityType))
    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new ScalaWriter(writer))
    val str = writer.toString

    CompileTestUtils.assertCompileSuccess(str)
  }

}
