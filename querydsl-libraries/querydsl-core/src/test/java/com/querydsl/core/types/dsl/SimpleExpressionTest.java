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

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class SimpleExpressionTest {

  enum ExampleEnum {
    A,
    B
  }

  @Test
  public void as_usage() {
    SimpleExpression<String> str = new StringPath("str");
    assertThat(str.as("alias")).hasToString("str as alias");
    assertThat(str.as(new StringPath("alias"))).hasToString("str as alias");
  }

  @Test
  public void case_() {
    SimpleExpression<String> str = new StringPath("str");
    // nullif(str, 'xxx')
    str.when("xxx").thenNull().otherwise(str);
  }

  @Test
  public void subclasses_override_as() throws SecurityException, NoSuchMethodException {
    List<Class<?>> classes =
        Arrays.<Class<?>>asList(
            BooleanExpression.class,
            ComparableExpression.class,
            DateExpression.class,
            DateTimeExpression.class,
            EnumExpression.class,
            NumberExpression.class,
            SimpleExpression.class,
            StringExpression.class,
            TimeExpression.class);

    for (Class<?> cl : classes) {
      var asPath = cl.getDeclaredMethod("as", Path.class);
      assertThat(asPath.getReturnType()).isEqualTo(cl);

      var asString = cl.getDeclaredMethod("as", String.class);
      assertThat(asString.getReturnType()).isEqualTo(cl);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void various() {
    List<DslExpression<?>> paths = new ArrayList<>();
    paths.add(new ArrayPath(String[].class, "p"));
    paths.add(new BeanPath(Object.class, "p"));
    paths.add(new BooleanPath("p"));
    paths.add(new CollectionPath(String.class, StringPath.class, "p"));
    paths.add(new ComparablePath(String.class, "p"));
    paths.add(new DatePath(Date.class, "p"));
    paths.add(new DateTimePath(Date.class, "p"));
    paths.add(new EnumPath(ExampleEnum.class, "p"));
    paths.add(new ListPath(String.class, StringPath.class, "p"));
    paths.add(new MapPath(String.class, String.class, StringPath.class, "p"));
    paths.add(new NumberPath(Integer.class, "p"));
    paths.add(new SetPath(String.class, StringPath.class, "p"));
    paths.add(new SimplePath(String.class, "p"));
    paths.add(new StringPath("p"));
    paths.add(new TimePath(Time.class, "p"));

    for (DslExpression<?> expr : paths) {
      Path<?> o = ExpressionUtils.path(expr.getType(), "o");
      assertThat(expr.as("o"))
          .isEqualTo(ExpressionUtils.operation(expr.getType(), Ops.ALIAS, expr, o));
      Path p = ExpressionUtils.path(expr.getType(), "p");
      assertThat(expr.as(p))
          .isEqualTo(ExpressionUtils.operation(expr.getType(), Ops.ALIAS, expr, p));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void eq_null() {
    new SimplePath<>(Object.class, "path").eq((Object) null);
  }
}
