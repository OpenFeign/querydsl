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

import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class ToStringVisitorTest {

  private Templates templates =
      new Templates() {
        {
          add(PathType.PROPERTY, "{0}_{1}");
          add(PathType.COLLECTION_ANY, "{0}");
        }
      };

  @Test
  public void operation() {
    assertThat(QCat.cat.name.isNotNull().accept(ToStringVisitor.DEFAULT, templates))
        .isEqualTo("cat_name is not null");
  }

  @Test
  public void template() {
    Expression<Boolean> template =
        ExpressionUtils.template(Boolean.class, "{0} is not null", QCat.cat.name);
    assertThat(template.accept(ToStringVisitor.DEFAULT, templates))
        .isEqualTo("cat_name is not null");
  }

  @Test
  public void path() {
    assertThat(QCat.cat.kittens.any().kittens.any().name.accept(ToStringVisitor.DEFAULT, templates))
        .isEqualTo("cat_kittens_kittens_name");
  }

  @Test
  public void complex() {
    var a = Expressions.booleanPath("a");
    var b = Expressions.booleanPath("d");
    var c = Expressions.booleanPath("c");
    var d = Expressions.booleanPath("d");
    Predicate complex = a.or(b).and(c.or(d));
    assertThat(complex.accept(ToStringVisitor.DEFAULT, templates))
        .isEqualTo("(a || d) && (c || d)");
  }
}
