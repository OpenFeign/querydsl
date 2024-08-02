package com.querydsl.core.util;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;

/** TupleUtils provides tuple related utility functionality */
public final class TupleUtils {

  public static Tuple toTuple(Object next, Expression<?>[] expressions) {
    // workaround from https://github.com/querydsl/querydsl/issues/3264
    Tuple tuple;
    if (next instanceof Tuple tuple1) {
      tuple = tuple1;
    } else if (next instanceof Object[] objects) {
      tuple = Projections.tuple(expressions).newInstance(objects);
    } else {
      throw new IllegalArgumentException("Could not translate %s into tuple".formatted(next));
    }
    return tuple;
  }
}
