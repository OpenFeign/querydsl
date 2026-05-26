package fluentq.scala

import fluentq.codegen.utils.model._
import fluentq.codegen._
import fluentq.core.util.StringUtils.uncapitalize

import scala.jdk.CollectionConverters._

object EntityTypes {

    val typeModel = new SimpleType(TypeCategory.ENTITY,
        "fluentq.DomainClass", "fluentq", "DomainClass", false, false)
    val entityType = new EntityType(typeModel)

    // properties
    for ( (name, clazz) <- List(
        ("entityField",entityType),
        ("collection", new SimpleType(Types.COLLECTION, typeModel)),
        ("listField",new SimpleType(Types.LIST, typeModel)), ("setField",new SimpleType(Types.SET, typeModel)),
        ("arrayField", new ClassType(TypeCategory.ARRAY, classOf[Array[String]])),
        ("mapField", new SimpleType(Types.MAP, typeModel, typeModel)))) {
      entityType.addProperty(new Property(entityType, name, clazz))
    }

    for ( clazz <- List(
        classOf[java.lang.Boolean], classOf[Boolean], classOf[Array[Byte]],
        classOf[Integer], classOf[java.util.Date], classOf[java.sql.Date], classOf[java.sql.Time])) {
      val name = uncapitalize(clazz.getSimpleName +
          (if (clazz.isPrimitive) "_p" else "")).replace("[","").replace("]","")
      entityType.addProperty(new Property(entityType, name,
          new ClassType(TypeCategory.get(clazz.getName), clazz)))
    }

    // constructor
    val firstName = new Parameter("firstName", Types.STRING)
    val lastName = new Parameter("lastName", Types.STRING)

    entityType.addConstructor(new Constructor(List(firstName, lastName).asJava))

}