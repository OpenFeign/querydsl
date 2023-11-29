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

import com.querydsl.codegen.utils.Evaluator;
import com.querydsl.core.util.NullSafeComparableComparator;
import java.io.Serializable;
import java.util.Comparator;

/**
 * {@code MultiComparator} compares arrays
 *
 * @param <T> element type
 * @author tiwe
 */
public class MultiComparator<T> implements Comparator<T>, Serializable {

  @SuppressWarnings("unchecked")
  private static final Comparator<Object> naturalOrder = new NullSafeComparableComparator();

  private static final long serialVersionUID = 1121416260773566299L;

  private final boolean[] asc;

  private final boolean[] nullsLast;

  private final transient Evaluator<Object[]> ev;

  public MultiComparator(Evaluator<Object[]> ev, boolean[] directions, boolean[] nullsLast) {
    this.ev = ev;
    this.asc = directions.clone();
    this.nullsLast = nullsLast.clone();
  }

  @Override
  public int compare(T o1, T o2) {
    if (o1 instanceof Object[]) {
      return innerCompare(ev.evaluate((Object[]) o1), ev.evaluate((Object[]) o2));
    } else {
      return innerCompare(ev.evaluate(o1), ev.evaluate(o2));
    }
  }

  private int innerCompare(Object[] o1, Object[] o2) {
    for (int i = 0; i < o1.length; i++) {
      if (o1[i] == null) {
        return o2[i] == null ? 0 : nullsLast[i] ? 1 : -1;
      } else if (o2[i] == null) {
        return nullsLast[i] ? -1 : 1;
      } else {
        int res;
        res = naturalOrder.compare(o1[i], o2[i]);
        if (res != 0) {
          return asc[i] ? res : -res;
        }
      }
    }
    return 0;
  }
}
