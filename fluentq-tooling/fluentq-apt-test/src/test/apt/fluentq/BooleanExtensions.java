package fluentq;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.Predicate;
import fluentq.core.types.dsl.BooleanPath;

public class BooleanExtensions {

  @QueryDelegate(Boolean.class)
  public static Predicate isFalse(BooleanPath path) {
    return path.isNotNull().or(path.eq(false));
  }

  @QueryDelegate(Boolean.class)
  public static Predicate isTrue(BooleanPath path) {
    return path.eq(true);
  }
}
