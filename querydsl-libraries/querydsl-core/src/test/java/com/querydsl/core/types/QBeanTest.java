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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QBeanTest {

  public static class Entity {

    private String name;

    private String name2;

    private int age;

    private boolean married;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public boolean isMarried() {
      return married;
    }

    public void setMarried(boolean married) {
      this.married = married;
    }

    public String getName2() {
      return name2;
    }

    public void setName2(String name2) {
      this.name2 = name2;
    }
  }

  public static class SubEntity extends Entity {}

  private PathBuilder<Entity> entity;

  private StringPath name, name2;

  private NumberPath<Integer> age;

  private BooleanPath married;

  @BeforeEach
  void setUp() {
    entity = new PathBuilderFactory().create(Entity.class);
    name = entity.getString("name");
    name2 = entity.getString("name2");
    age = entity.getNumber("age", Integer.class);
    married = entity.getBoolean("married");
  }

  @Test
  void with_class_and_exprs() {
    var beanProjection = new QBean<>(Entity.class, name, age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
    assertThat(bean.isMarried()).isTrue();
  }

  @Test
  void with_path_and_exprs() {
    QBean<Entity> beanProjection = Projections.bean(entity, name, age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
    assertThat(bean.isMarried()).isTrue();
  }

  @Test
  void with_unknown_properties() {
    QBean<Entity> beanProjection =
        Projections.bean(entity, name, age, Expressions.booleanPath("unknown"));
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
  }

  @Test
  void with_class_and_map() {
    Map<String, Expression<?>> bindings = new LinkedHashMap<>();
    bindings.put("name", name);
    bindings.put("age", age);
    bindings.put("married", married);
    var beanProjection = new QBean<>(Entity.class, bindings);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
    assertThat(bean.isMarried()).isTrue();
  }

  @Test
  void with_class_and_alias() {
    var name2 = Expressions.stringPath("name2");
    var beanProjection = new QBean<>(Entity.class, name.as(name2), age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isNull();
    assertThat(bean.getName2()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
    assertThat(bean.isMarried()).isTrue();
  }

  @Test
  void with_nested_factoryExpression() {
    Map<String, Expression<?>> bindings = new LinkedHashMap<>();
    bindings.put("age", age);
    bindings.put("name", new Concatenation(name, name2));
    var beanProjection = new QBean<>(Entity.class, bindings);
    FactoryExpression<Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
    Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
    assertThat(bean.getName()).isEqualTo("Fritz");
  }

  @Test
  void with_nested_factoryExpression2() {
    var beanProjection =
        new QBean<>(Entity.class, age, ExpressionUtils.as(new Concatenation(name, name2), "name"));
    FactoryExpression<Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
    Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
    assertThat(bean.getName()).isEqualTo("Fritz");
  }

  @Test
  void supertype_population() {
    var beanProjection = new QBean<>(SubEntity.class, true, name, age, married);
    var bean = beanProjection.newInstance("Fritz", 30, true);
    assertThat(bean.getName()).isEqualTo("Fritz");
    assertThat(bean.getAge()).isEqualTo(30);
    assertThat(bean.isMarried()).isTrue();
  }

  @Test
  void skipNulls() {
    QBean<Object> bean = Projections.bean(Object.class);
    assertThat(bean).isEqualTo(bean);
    assertThat(bean.skipNulls()).isEqualTo(bean.skipNulls());
    assertThat(bean.skipNulls()).isNotEqualTo(bean);
    assertThat(bean).isNotEqualTo(bean.skipNulls());
  }

  @Test
  void alias() {
    var beanProjection = new QBean<>(Entity.class, name.as("name2"));
    assertThat(beanProjection.getArgs()).first().isEqualTo(name.as("name2"));
  }
}
