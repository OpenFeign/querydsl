package fluentq.core.util;

import fluentq.core.Tuple;
import fluentq.core.types.Expression;
import fluentq.core.types.Projections;

/** TupleUtils provides tuple related utility functionality */
public final class TupleUtils {

  public static Tuple toTuple(Object next, Expression<?>[] expressions) {
    // workaround from https://github.fluentq/fluentq/issues/3264
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
