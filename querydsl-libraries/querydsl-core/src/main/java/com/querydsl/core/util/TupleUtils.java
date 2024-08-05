package com.querydsl.core.util;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;

/** TupleUtils provides tuple related utility functionality */
public final class TupleUtils {

  public static Tuple toTuple(Object next, Expression<?>[] expressions) {
    // workaround from https://github.com/querydsl/querydsl/issues/3264
    Tuple tuple;
    if (next instanceof Tuple) {
      tuple = (Tuple) next;
    } else if (next instanceof Object[]) {
      tuple = Projections.tuple(expressions).newInstance((Object[]) next);
    } else {
      throw new IllegalArgumentException(String.format("Could not translate %s into tuple", next));
    }
    return tuple;
  }
}
