/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import org.junit.Test;

public class QPersonTest {

  public static class Person {

    private int id;

    private String firstname, securedid;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getFirstname() {
      return firstname;
    }

    public void setFirstname(String firstname) {
      this.firstname = firstname;
    }

    public String getSecuredid() {
      return securedid;
    }

    public void setSecuredid(String securedid) {
      this.securedid = securedid;
    }
  }

  @Test
  public void populate() {
    QPerson person = QPerson.person;
    QBean<Person> personProjection =
        Projections.bean(Person.class, person.id, person.firstname, person.securedid);
    Person p = personProjection.newInstance(3, "X", "Y");
    assertEquals(3, p.getId());
    assertEquals("X", p.getFirstname());
    assertEquals("Y", p.getSecuredid());
  }
}
