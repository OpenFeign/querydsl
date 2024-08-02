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
package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class QBeanFieldAccessTest {

  public static class Entity {

    String name;

    String name2;

    int age;

    boolean married;
  }

  private PathBuilder<Entity> entity;

  private StringPath name, name2;

  private NumberPath<Integer> age;

  private BooleanPath married;

  @Before
  public void setUp() {
    entity = new PathBuilderFactory().create(Entity.class);
    name = entity.getString("name");
    name2 = entity.getString("name2");
    age = entity.getNumber("age", Integer.class);
    married = entity.getBoolean("married");
  }

  @Test
  public void with_class_and_exprs_using_fields() {
    var beanProjection = new QBean<>(Entity.class, true, name, age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.name).isEqualTo("Fritz");
    assertThat(bean.age).isEqualTo(30);
    assertThat(bean.married).isTrue();
  }

  @Test
  public void with_path_and_exprs_using_fields() {
    QBean<Entity> beanProjection = Projections.fields(entity, name, age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.name).isEqualTo("Fritz");
    assertThat(bean.age).isEqualTo(30);
    assertThat(bean.married).isTrue();
  }

  @Test
  public void with_class_and_map_using_fields() {
    Map<String, Expression<?>> bindings = new LinkedHashMap<>();
    bindings.put("name", name);
    bindings.put("age", age);
    bindings.put("married", married);
    var beanProjection = new QBean<>(Entity.class, true, bindings);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.name).isEqualTo("Fritz");
    assertThat(bean.age).isEqualTo(30);
    assertThat(bean.married).isTrue();
  }

  @Test
  public void with_class_and_alias_using_fields() {
    var name2 = Expressions.stringPath("name2");
    var beanProjection = new QBean<>(Entity.class, true, name.as(name2), age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.name).isNull();
    assertThat(bean.name2).isEqualTo("Fritz");
    assertThat(bean.age).isEqualTo(30);
    assertThat(bean.married).isTrue();
  }

  @Test
  public void with_nested_factoryExpression() {
    Map<String, Expression<?>> bindings = new LinkedHashMap<>();
    bindings.put("age", age);
    bindings.put("name", new Concatenation(name, name2));
    var beanProjection = new QBean<>(Entity.class, true, bindings);
    FactoryExpression<Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
    Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
    assertThat(bean.name).isEqualTo("Fritz");
  }
}
