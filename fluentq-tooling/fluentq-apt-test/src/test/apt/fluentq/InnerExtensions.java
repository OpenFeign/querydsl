package fluentq;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.dsl.BooleanExpression;

public class InnerExtensions {

  public static class ExampleEntity2Extensions {

    @QueryDelegate(ExampleEntity2.class)
    public static BooleanExpression isZero(QExampleEntity2 left) {
      return left.id.eq(0);
    }
  }
}
