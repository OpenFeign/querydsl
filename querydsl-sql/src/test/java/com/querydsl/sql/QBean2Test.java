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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class QBean2Test {

  //    @Table("PERSON")
  public static class QPerson extends RelationalPathBase<QPerson> {
    private static final long serialVersionUID = 609527362;
    public static final QPerson person = new QPerson("PERSON");
    public final StringPath firstName = createString("firstName");
    public final NumberPath<Integer> id = createNumber("id", Integer.class);
    public final StringPath lastName = createString("lastName");

    public QPerson(String variable) {
      super(QPerson.class, PathMetadataFactory.forVariable(variable), "", "PERSON");
      addMetadata();
    }

    public QPerson(BeanPath<? extends QPerson> entity) {
      super(entity.getType(), entity.getMetadata(), "", "PERSON");
      addMetadata();
    }

    public QPerson(PathMetadata metadata) {
      super(QPerson.class, metadata, "", "PERSON");
      addMetadata();
    }

    public void addMetadata() {
      addMetadata(firstName, ColumnMetadata.named("FIRST_NAME"));
      addMetadata(lastName, ColumnMetadata.named("LAST_NAME"));
      addMetadata(id, ColumnMetadata.named("ID"));
    }
  }

  public static class Person {
    private int id;
    private String firstName;
    private String lastName;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }

  @Test
  public void newInstance() {
    QPerson p = QPerson.person;
    QBean<Person> projection =
        Projections.bean(
            Person.class, p.id, p.firstName.as("firstName"), p.lastName.as("lastName"));

    Person person = projection.newInstance(3, "John", "Doe");
    assertThat(person.getId()).isEqualTo(3);
    assertThat(person.getFirstName()).isEqualTo("John");
    assertThat(person.getLastName()).isEqualTo("Doe");
  }
}
