package fluentq;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.dsl.NumberExpression;
import fluentq.core.types.dsl.NumberPath;

public class IntegerExtensions {

  @QueryDelegate(Integer.class)
  public static NumberExpression<Integer> difference(
      NumberPath<Integer> left, NumberExpression<Integer> right) {
    return right.subtract(left);
  }
}
