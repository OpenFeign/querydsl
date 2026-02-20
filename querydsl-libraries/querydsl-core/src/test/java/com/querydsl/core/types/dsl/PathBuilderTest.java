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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.domain.Cat;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.util.BeanMap;
import java.sql.Time;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PathBuilderTest {

  @Test
  void getEnum() {
    var entityPath = new PathBuilder<>(User.class, "entity");
    EnumPath<Gender> enumPath = entityPath.getEnum("gender", Gender.class);
    assertThat(enumPath.ordinal()).isNotNull();
    assertThat(entityPath.get(enumPath)).isEqualTo(enumPath);
  }

  @Test
  void getByExample() {
    var user = new User();
    user.setFirstName("firstName");
    user.setLastName("lastName");
    var byExample = getByExample(user).toString();
    assertThat(byExample)
        .contains("entity.lastName = lastName")
        .contains("entity.firstName = firstName");
  }

  @Test
  void getArray() {
    var entityPath = new PathBuilder<>(User.class, "entity");
    ArrayPath<String[], String> array = entityPath.getArray("array", String[].class);
    assertThat(array.getType()).isEqualTo(String[].class);
    assertThat(array.getElementType()).isEqualTo(String.class);
  }

  @Test
  void getList() {
    var entityPath = new PathBuilder<>(User.class, "entity");
    entityPath.getList("list", String.class, StringPath.class).get(0).lower();
    entityPath.getList("list", String.class).get(0);
  }

  @Test
  void getMap() {
    var entityPath = new PathBuilder<>(User.class, "entity");
    entityPath.getMap("map", String.class, String.class, StringPath.class).get("").lower();
    entityPath.getMap("map", String.class, String.class).get("");
  }

  @SuppressWarnings("unchecked")
  private <T> BooleanBuilder getByExample(T entity) {
    var entityPath = new PathBuilder<>((Class<T>) entity.getClass(), "entity");
    var conditions = new BooleanBuilder();
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
  void get() {
    var entity = new PathBuilder<>(User.class, "entity");
    var intPath = new NumberPath<>(Integer.class, "int");
    var strPath = new StringPath("str");
    var booleanPath = new BooleanPath("boolean");

    assertThat(entity.get(intPath)).hasToString("entity.int");
    assertThat(entity.get(strPath)).hasToString("entity.str");
    assertThat(entity.get(booleanPath)).hasToString("entity.boolean");

    assertThat(entity.get(entity.get(intPath))).hasToString("entity.int");
  }

  @Test
  void various() {
    var entity = new PathBuilder<>(User.class, "entity");
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
  void calling_get_with_the_same_name_and_different_types_returns_correct_type() {
    var entity = new PathBuilder<>(User.class, "entity");
    var pathName = "some_path";
    assertThat(entity.get(pathName).getType()).isEqualTo(Object.class);
    assertThat(entity.get(pathName, Integer.class).getType()).isEqualTo(Integer.class);
    assertThat(entity.get(pathName, User.class).getType()).isEqualTo(User.class);
  }

  @Test
  void calling_get_with_the_same_name_and_different_types_returns_specific_type_when_validating() {
    var entity = new PathBuilder<>(User.class, "entity", PathBuilderValidator.FIELDS);
    var pathName = "username";
    assertThat(entity.get(pathName).getType()).isEqualTo(String.class);
    assertThat(entity.get(pathName, Comparable.class).getType()).isEqualTo(String.class);
    assertThat(entity.get(pathName, Object.class).getType()).isEqualTo(String.class);
  }

  @Test
  void order_HQL_injection() {
    var orderBy = "breed";
    var pathBuilder = new PathBuilder<Cat>(Cat.class, "entity");
    assertThatCode(() -> new OrderSpecifier(Order.ASC, pathBuilder.get(orderBy)))
        .doesNotThrowAnyException();
  }

  // CVE-2024-49203
  // https://github.com/OpenFeign/querydsl/security/advisories/GHSA-6q3q-6v5j-h6vg
  @Test
  void unsafe_order_HQL_injection() {
    var orderBy =
        "test.name INTERSECT SELECT t FROM Test t WHERE (SELECT cast(pg_sleep(10) AS text))='2' ORDER BY t.id";
    var pathBuilder = new PathBuilder<Cat>(Cat.class, "entity");
    var error =
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> new OrderSpecifier(Order.ASC, pathBuilder.get(orderBy)))
            .actual();
    assertThat(error).hasMessageContaining("CVE-2024-49203");
  }
}
