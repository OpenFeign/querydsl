package com.querydsl.scala

import org.junit.{Assert, Test}

class QPersonTest {

  val person = QPerson as "person"

  @Test
  def EntityPath: Unit = {
    assertEquals("person.other.firstName", person.other.firstName)
  }

  @Test
  def Collection_Any: Unit = {
    assertEquals("any(person.javaCollection) = Bob", person.javaCollection.any === "Bob")
  }

  @Test
  def List_Get: Unit = {
    assertEquals("person.javaList.get(0) = Bob", person.javaList(0) === "Bob")
  }

  @Test
  def List_Get_EntityPath: Unit = {
    assertEquals("person.listOfPersons.get(0).firstName is not null",
        person.listOfPersons(0).firstName isNotNull)
  }

  def assertEquals(expected: String, actual: Any): Unit = {
    Assert.assertEquals(expected, actual.toString)
  }

}