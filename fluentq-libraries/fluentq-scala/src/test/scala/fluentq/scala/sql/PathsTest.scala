package fluentq.scala.sql

import fluentq.core.types._
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api._
import test._

class PathsTest {

  @Test
  def Projection: Unit = {
    val projection = Employee.getProjection.asInstanceOf[FactoryExpression[_]]
    assertEquals(4, projection.getArgs.size)
  }

}