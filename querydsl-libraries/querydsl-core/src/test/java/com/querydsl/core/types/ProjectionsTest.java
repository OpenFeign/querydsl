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

import com.querydsl.core.types.QBeanPropertyTest.Entity;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import org.junit.Test;

public class ProjectionsTest {

  public static class VarArgs {
    String[] args;

    public VarArgs(String... strs) {
      args = strs;
    }
  }

  public static class VarArgs2 {
    String arg;
    String[] args;

    public VarArgs2(String s, String... strs) {
      arg = s;
      args = strs;
    }
  }

  public static class Entity1 {
    String arg1, arg2;

    public Entity1(String arg1, String arg2) {
      this.arg1 = arg1;
      this.arg2 = arg2;
    }
  }

  public static class Entity2 {
    String arg1;
    Entity1 entity;

    public Entity2(String arg1, Entity1 entity) {
      this.arg1 = arg1;
      this.entity = entity;
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void array() {
    FactoryExpression<String[]> expr =
        Projections.array(
            String[].class,
            ExpressionUtils.path(String.class, "p1"),
            ExpressionUtils.path(String.class, "p2"));
    assertThat(expr.newInstance("1", "2").getClass()).isEqualTo(String[].class);
  }

  @Test
  public void beanClassOfTExpressionOfQArray() {
    var entity = new PathBuilder<Entity>(Entity.class, "entity");
    QBean<Entity> beanProjection =
        Projections.bean(
            Entity.class,
            entity.getNumber("cId", Integer.class),
            entity.getNumber("eId", Integer.class));

    assertThat(beanProjection.newInstance(1, 2).getClass()).isEqualTo(Entity.class);
  }

  @Test
  public void constructor() {
    Expression<Long> longVal = ConstantImpl.create(1L);
    Expression<String> stringVal = ConstantImpl.create("");
    assertThat(
            Projections.constructor(ProjectionExample.class, longVal, stringVal)
                .newInstance(0L, "")
                .getClass())
        .isEqualTo(ProjectionExample.class);
  }

  @Test
  public void constructor_varArgs() {
    Expression<String> stringVal = ConstantImpl.create("");
    var instance =
        Projections.constructor(VarArgs.class, stringVal, stringVal).newInstance("X", "Y");
    assertThat(instance.args).containsExactly(new String[] {"X", "Y"});
  }

  @Test
  public void constructor_varArgs2() {
    Expression<String> stringVal = ConstantImpl.create("");
    var instance =
        Projections.constructor(VarArgs2.class, stringVal, stringVal, stringVal)
            .newInstance("X", "Y", "Z");
    assertThat(instance.arg).isEqualTo("X");
    assertThat(instance.args).containsExactly(new String[] {"Y", "Z"});
  }

  @Test
  public void constructor_varArgs3() {
    var longVal = ConstantImpl.create(1L);
    var charVal = ConstantImpl.create('\0');
    var instance =
        Projections.constructor(
                ProjectionExample.class,
                longVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal,
                charVal)
            .newInstance(null, 'm', 'y', 's', 'e', 'm', 'a', null, 'l', 't', 'd');
    assertThat((long) instance.id).isEqualTo(0L);
    // null character cannot be inserted, so a literal String can't be used.
    var expectedText =
        String.valueOf(new char[] {'m', 'y', 's', 'e', 'm', 'a', '\0', 'l', 't', 'd'});
    assertThat(instance.text).isEqualTo(expectedText);
  }

  @Test
  public void fieldsClassOfTExpressionOfQArray() {
    var entity = new PathBuilder<Entity>(Entity.class, "entity");
    QBean<Entity> beanProjection =
        Projections.fields(
            Entity.class,
            entity.getNumber("cId", Integer.class),
            entity.getNumber("eId", Integer.class));

    assertThat(beanProjection.newInstance(1, 2).getClass()).isEqualTo(Entity.class);
  }

  @Test
  public void nested() {
    var str1 = Expressions.stringPath("str1");
    var str2 = Expressions.stringPath("str2");
    var str3 = Expressions.stringPath("str3");
    FactoryExpression<Entity1> entity = Projections.constructor(Entity1.class, str1, str2);
    FactoryExpression<Entity2> wrapper = Projections.constructor(Entity2.class, str3, entity);
    FactoryExpression<Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);

    Entity2 w = wrapped.newInstance("a", "b", "c");
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity.arg1).isEqualTo("b");
    assertThat(w.entity.arg2).isEqualTo("c");

    w = wrapped.newInstance("a", null, null);
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity).isNotNull();

    w = wrapped.newInstance(null, null, null);
    assertThat(w.entity).isNotNull();
  }

  @Test
  public void nestedSkipNulls() {
    var str1 = Expressions.stringPath("str1");
    var str2 = Expressions.stringPath("str2");
    var str3 = Expressions.stringPath("str3");
    var entity = Projections.constructor(Entity1.class, str1, str2).skipNulls();
    FactoryExpression<Entity2> wrapper = Projections.constructor(Entity2.class, str3, entity);
    FactoryExpression<Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);

    Entity2 w = wrapped.newInstance("a", "b", "c");
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity.arg1).isEqualTo("b");
    assertThat(w.entity.arg2).isEqualTo("c");

    w = wrapped.newInstance("a", null, null);
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity).isNull();

    w = wrapped.newInstance(null, null, null);
    assertThat(w.entity).isNull();
  }

  @Test
  public void nestedSkipNulls2() {
    var str1 = Expressions.stringPath("str1");
    var str2 = Expressions.stringPath("str2");
    var str3 = Expressions.stringPath("str3");
    var entity = Projections.constructor(Entity1.class, str1, str2).skipNulls();
    var wrapper = Projections.constructor(Entity2.class, str3, entity).skipNulls();
    FactoryExpression<Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);

    Entity2 w = wrapped.newInstance("a", "b", "c");
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity.arg1).isEqualTo("b");
    assertThat(w.entity.arg2).isEqualTo("c");

    w = wrapped.newInstance("a", null, null);
    assertThat(w.arg1).isEqualTo("a");
    assertThat(w.entity).isNull();

    w = wrapped.newInstance(null, null, null);
    assertThat(w).isNull();
  }
}
