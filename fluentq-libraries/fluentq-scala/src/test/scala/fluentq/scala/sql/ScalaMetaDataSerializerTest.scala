package fluentq.scala.sql

import java.io.StringWriter

import fluentq.codegen.utils._
import fluentq.codegen.utils.model._
import fluentq.codegen._
import fluentq.scala._
import fluentq.sql._
import fluentq.sql.codegen._
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api._

class ScalaMetaDataSerializerTest {

  var entityType: EntityType = null

  val writer = new StringWriter()

  @BeforeEach
  def setUp(): Unit = {
    // type
    val typeModel = new SimpleType(TypeCategory.ENTITY,
        "fluentq.DomainClass", "fluentq", "DomainClass", false, false)
    entityType = new EntityType(typeModel)
    //entityType.addAnnotation(new TableImpl("DOMAIN_TYPE"))
    entityType.getData.put("table", "DOMAIN_TYPE")

    // properties
    List(classOf[java.lang.Boolean], classOf[Comparable[_]], classOf[Integer],
         classOf[java.util.Date], classOf[java.sql.Date], classOf[java.sql.Time])
      .foreach(cl => {
        val classType = new ClassType(TypeCategory.get(cl.getName), cl)
        val propertyName = StringUtils.uncapitalize(cl.getSimpleName)
        var property = new Property(entityType, propertyName, classType)
        property.getData.put("COLUMN", ColumnMetadata.named(propertyName.toUpperCase).ofType(0))
        entityType.addProperty(property)
      })
  }

  @Test
  def Print: Unit = {
    val typeMappings = ScalaTypeMappings.create
    val namingStrategy = new DefaultNamingStrategy()
    val serializer = new ScalaMetaDataSerializer(typeMappings, namingStrategy)
    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new ScalaWriter(writer))
    val str = writer.toString
    //System.err.println(str)
    assertTrue(str.indexOf("object") < str.indexOf("class"), "companion object isn't before class")
    //assertTrue("companion object isn't before annotations", str.indexOf("object") < str.indexOf("@Table"))
  }

}