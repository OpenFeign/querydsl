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
package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Fetchable;
import com.querydsl.core.QueryExecution;
import com.querydsl.core.QuerydslModule;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ArrayConstructorExpression;
import com.querydsl.core.types.Concatenation;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class CollQueryStandardTest {

  private final Date birthDate = new Date();

  private final java.sql.Date date = new java.sql.Date(birthDate.getTime());

  private final java.sql.Time time = new java.sql.Time(birthDate.getTime());

  private final QCat cat = new QCat("cat");

  private final QCat otherCat = new QCat("otherCat");

  private final List<Cat> data =
      Arrays.asList(
          new Cat("Bob", 1, birthDate),
          new Cat("Ruth", 2, birthDate),
          new Cat("Felix", 3, birthDate),
          new Cat("Allen", 4, birthDate),
          new Cat("Mary", 5, birthDate));

  private static final Expression<?>[] NO_EXPRESSIONS = new Expression[0];

  private QueryExecution standardTest =
      new QueryExecution(QuerydslModule.COLLECTIONS, Target.MEM) {
        @Override
        protected Fetchable<?> createQuery() {
          return CollQueryFactory.from(cat, data).from(otherCat, data);
        }

        @Override
        protected Fetchable<?> createQuery(Predicate filter) {
          return CollQueryFactory.from(cat, data)
              .from(otherCat, data)
              .where(filter)
              .select(cat.name);
        }
      };

  @Test
  public void test() {
    Cat kitten = data.getFirst().getKittens().get(0);
    standardTest.runArrayTests(cat.kittenArray, otherCat.kittenArray, kitten, new Cat());
    standardTest.runBooleanTests(cat.name.isNull(), otherCat.kittens.isEmpty());
    standardTest.runCollectionTests(cat.kittens, otherCat.kittens, kitten, new Cat());
    standardTest.runDateTests(cat.dateField, otherCat.dateField, date);
    standardTest.runDateTimeTests(cat.birthdate, otherCat.birthdate, birthDate);
    standardTest.runListTests(cat.kittens, otherCat.kittens, kitten, new Cat());
    standardTest.runMapTests(
        cat.kittensByName, otherCat.kittensByName, "Kitty", kitten, "NoName", new Cat());

    // int
    standardTest.runNumericCasts(cat.id, otherCat.id, 1);
    standardTest.runNumericTests(cat.id, otherCat.id, 1);

    standardTest.runStringTests(cat.name, otherCat.name, "Bob");
    standardTest.runTimeTests(cat.timeField, otherCat.timeField, time);
    standardTest.report();
  }

  @Test
  public void tupleMultipleFieldsProjection() {
    List<Tuple> tuples = CollQueryFactory.from(cat, data).select(cat.name, cat.birthdate).fetch();
    for (Tuple tuple : tuples) {
      assertThat(tuple.get(cat.name)).isNotNull();
      assertThat(tuple.get(cat.birthdate)).isNotNull();
    }
  }

  @Test
  public void tupleOneFieldProjection() {
    List<Tuple> tuples =
        CollQueryFactory.from(cat, data).select(new Expression<?>[] {cat.name}).fetch();
    for (Tuple tuple : tuples) {
      assertThat(tuple.size()).isEqualTo(1);
      assertThat(tuple.get(cat.name)).isNotNull();
    }
  }

  @Test
  public void tupleOneNullFieldProjection() {
    List<Tuple> tuples =
        CollQueryFactory.from(cat, data)
            .select(new Expression<?>[] {Expressions.nullExpression()})
            .fetch();
    for (Tuple tuple : tuples) {
      assertThat(tuple).isNotNull();
      assertThat(tuple.size()).isEqualTo(1); // THIS FAILS WITH NPE
      assertThat(tuple.get(Expressions.<Void>nullExpression())).isNull();
    }
  }

  @Test
  public void nested_tupleProjection() {
    Concatenation concat = new Concatenation(cat.name, cat.name);
    List<Tuple> tuples =
        CollQueryFactory.from(cat, data).select(concat, cat.name, cat.birthdate).fetch();
    for (Tuple tuple : tuples) {
      assertThat(tuple.get(cat.name)).isNotNull();
      assertThat(tuple.get(cat.birthdate)).isNotNull();
      assertThat(tuple.get(concat)).isEqualTo(tuple.get(cat.name) + tuple.get(cat.name));
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void arrayProjection() {
    List<String[]> results =
        CollQueryFactory.from(cat, data)
            .select(new ArrayConstructorExpression<String>(String[].class, cat.name))
            .fetch();
    assertThat(results).isNotEmpty();
    for (String[] result : results) {
      assertThat(result[0]).isNotNull();
    }
  }

  @Test
  public void constructorProjection() {
    List<Projection> projections =
        CollQueryFactory.from(cat, data)
            .select(Projections.constructor(Projection.class, cat.name, cat))
            .fetch();
    assertThat(projections).isNotEmpty();
    for (Projection projection : projections) {
      assertThat(projection).isNotNull();
    }
  }

  @Test
  public void params() {
    Param<String> name = new Param<String>(String.class, "name");
    assertThat(
            CollQueryFactory.from(cat, data)
                .where(cat.name.eq(name))
                .set(name, "Bob")
                .select(cat.name)
                .fetchOne())
        .isEqualTo("Bob");
  }

  @Test
  public void params_anon() {
    Param<String> name = new Param<String>(String.class);
    assertThat(
            CollQueryFactory.from(cat, data)
                .where(cat.name.eq(name))
                .set(name, "Bob")
                .select(cat.name)
                .fetchOne())
        .isEqualTo("Bob");
  }

  @Test(expected = ParamNotSetException.class)
  public void params_not_set() {
    Param<String> name = new Param<String>(String.class, "name");
    assertThat(
            CollQueryFactory.from(cat, data).where(cat.name.eq(name)).select(cat.name).fetchOne())
        .isEqualTo("Bob");
  }

  @Test
  public void limit() {
    assertThat(CollQueryFactory.from(cat, data).limit(Long.MAX_VALUE).fetch()).isEqualTo(data);
  }
}
