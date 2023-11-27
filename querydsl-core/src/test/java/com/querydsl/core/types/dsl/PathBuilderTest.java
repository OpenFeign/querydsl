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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.util.BeanMap;
import java.sql.Time;
import java.util.Date;
import java.util.Map;
import org.junit.Test;

public class PathBuilderTest {

  @Test
  public void getEnum() {
    PathBuilder<User> entityPath = new PathBuilder<User>(User.class, "entity");
    EnumPath<Gender> enumPath = entityPath.getEnum("gender", Gender.class);
    assertThat(enumPath.ordinal()).isNotNull();
    assertThat(entityPath.get(enumPath)).isEqualTo(enumPath);
  }

  @Test
  public void getByExample() {
    User user = new User();
    user.setFirstName("firstName");
    user.setLastName("lastName");
    String byExample = getByExample(user).toString();
    assertThat(byExample).contains("entity.lastName = lastName");
    assertThat(byExample).contains("entity.firstName = firstName");
  }

  @Test
  public void getArray() {
    PathBuilder<User> entityPath = new PathBuilder<User>(User.class, "entity");
    ArrayPath<String[], String> array = entityPath.getArray("array", String[].class);
    assertThat(array.getType()).isEqualTo(String[].class);
    assertThat(array.getElementType()).isEqualTo(String.class);
  }

  @Test
  public void getList() {
    PathBuilder<User> entityPath = new PathBuilder<User>(User.class, "entity");
    entityPath.getList("list", String.class, StringPath.class).get(0).lower();
    entityPath.getList("list", String.class).get(0);
  }

  @Test
  public void getMap() {
    PathBuilder<User> entityPath = new PathBuilder<User>(User.class, "entity");
    entityPath.getMap("map", String.class, String.class, StringPath.class).get("").lower();
    entityPath.getMap("map", String.class, String.class).get("");
  }

  @SuppressWarnings("unchecked")
  private <T> BooleanBuilder getByExample(T entity) {
    PathBuilder<T> entityPath = new PathBuilder<T>((Class<T>) entity.getClass(), "entity");
    BooleanBuilder conditions = new BooleanBuilder();
    Map<String, Object> beanMap = new BeanMap(entity);
    for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
      if (!entry.getKey().equals("class")) {
        if (entry.getValue() != null) {
          conditions.and(entityPath.get(entry.getKey()).eq(entry.getValue()));
        }
      }
    }
    return conditions;
  }

  @Test
  public void get() {
    PathBuilder<User> entity = new PathBuilder<User>(User.class, "entity");
    NumberPath<Integer> intPath = new NumberPath<Integer>(Integer.class, "int");
    StringPath strPath = new StringPath("str");
    BooleanPath booleanPath = new BooleanPath("boolean");

    assertThat(entity.get(intPath).toString()).isEqualTo("entity.int");
    assertThat(entity.get(strPath).toString()).isEqualTo("entity.str");
    assertThat(entity.get(booleanPath).toString()).isEqualTo("entity.boolean");

    assertThat(entity.get(entity.get(intPath)).toString()).isEqualTo("entity.int");
  }

  @Test
  public void various() {
    PathBuilder<User> entity = new PathBuilder<User>(User.class, "entity");
    entity.getBoolean("boolean");
    entity.getCollection("col", User.class);
    entity.getComparable("comparable", Comparable.class);
    entity.getDate("date", Date.class);
    entity.getDateTime("dateTime", Date.class);
    entity.getList("list", User.class);
    entity.getMap("map", String.class, User.class);
    entity.getNumber("number", Integer.class);
    entity.getSet("set", User.class);
    entity.getSimple("simple", Object.class);
    entity.getString("string");
    entity.getTime("time", Time.class);
  }

  @Test
  public void calling_get_with_the_same_name_and_different_types_returns_correct_type() {
    PathBuilder<User> entity = new PathBuilder<User>(User.class, "entity");
    String pathName = "some_path";
    assertThat(entity.get(pathName).getType()).isEqualTo(Object.class);
    assertThat(entity.get(pathName, Integer.class).getType()).isEqualTo(Integer.class);
    assertThat(entity.get(pathName, User.class).getType()).isEqualTo(User.class);
  }

  @Test
  public void
      calling_get_with_the_same_name_and_different_types_returns_specific_type_when_validating() {
    PathBuilder<User> entity =
        new PathBuilder<User>(User.class, "entity", PathBuilderValidator.FIELDS);
    String pathName = "username";
    assertThat(entity.get(pathName).getType()).isEqualTo(String.class);
    assertThat(entity.get(pathName, Comparable.class).getType()).isEqualTo(String.class);
    assertThat(entity.get(pathName, Object.class).getType()).isEqualTo(String.class);
  }
}
