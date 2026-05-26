package fluentq;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.Predicate;
import fluentq.core.types.dsl.BooleanPath;

public class BooleanExtensions2 {

  @QueryDelegate(boolean.class)
  public static Predicate isFalse(BooleanPath path) {
    return path.isNotNull().or(path.eq(false));
  }

  @QueryDelegate(boolean.class)
  public static Predicate isTrue(BooleanPath path) {
    return path.eq(true);
  }
}
