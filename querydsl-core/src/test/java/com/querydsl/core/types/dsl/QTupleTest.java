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

import static org.junit.Assert.assertEquals;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import org.junit.Test;

public class QTupleTest {

  private StringPath first = new StringPath("x");

  private NumberPath<Integer> second = new NumberPath<Integer>(Integer.class, "y");

  private BooleanPath third = new BooleanPath("z");

  private QTuple tupleExpression = Projections.tuple(first, second, third);

  @Test
  public void newInstanceObjectArray() {
    Tuple tuple = tupleExpression.newInstance("1", 42, true);
    assertEquals(3, tuple.size());
    assertEquals("1", tuple.get(0, String.class));
    assertEquals(Integer.valueOf(42), tuple.get(1, Integer.class));
    assertEquals(Boolean.TRUE, tuple.get(2, Boolean.class));
    assertEquals("1", tuple.get(first));
    assertEquals(Integer.valueOf(42), tuple.get(second));
    assertEquals(Boolean.TRUE, tuple.get(third));
  }
}
