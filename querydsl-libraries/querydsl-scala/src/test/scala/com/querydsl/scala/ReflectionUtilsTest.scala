package com.querydsl.scala

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api._

class ReflectionUtilsTest {

  @Test
  def getSuperClasses_of_String: Unit = {
    assertEquals(List(classOf[String],classOf[AnyRef]), ReflectionUtils.getSuperClasses(classOf[String]))
  }

  @Test
  def getImplementedInterfaces: Unit = {
    var setToTest = ReflectionUtils.getImplementedInterfaces(classOf[String])
    assertTrue(setToTest.contains(classOf[java.io.Serializable]))
    assertTrue(setToTest.contains(classOf[Comparable[_]]))
    assertTrue(setToTest.contains(classOf[CharSequence]))
  }

  @Test
  def getFields: Unit = {
    assertTrue( ReflectionUtils.getFields(classOf[String]).size > 0)
  }



}