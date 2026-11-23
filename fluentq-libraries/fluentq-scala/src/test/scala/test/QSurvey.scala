package test

import fluentq.core.types.PathMetadataFactory._
import fluentq.core.types._
import fluentq.scala.sql.RelationalPathImpl
import fluentq.sql._

object QSurvey extends QSurvey("survey") {
  override def as(variable: String) = new QSurvey(variable)

}

class QSurvey(md: PathMetadata) extends RelationalPathImpl[Survey](md, "PUBLIC", "SURVEY") {
  def this(variable: String) = this(forVariable(variable))

  def this(parent: Path[_], variable: String) = this(forProperty(parent, variable))

  val id = createNumber[Integer]("id")

  val name = createString("name")

  val sysIdx54: PrimaryKey[Survey] = createPrimaryKey(id)

  addMetadata(id, ColumnMetadata.named("ID"))
  addMetadata(name, ColumnMetadata.named("NAME"))

}

