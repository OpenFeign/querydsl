package fluentq.apt.domain;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.dsl.BooleanExpression;
import fluentq.core.types.dsl.ComparablePath;
import fluentq.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

public class Delegate3Test {

  public static class Geometry implements Comparable<Geometry> {

    @Override
    public int compareTo(Geometry o) {
      return 0;
    }
  }

  public static class Point extends Geometry {}

  public static class Polygon extends Geometry {}

  @QueryDelegate(Geometry.class)
  public static BooleanExpression isWithin(
      ComparablePath<? extends Geometry> geo1, ComparablePath<? extends Geometry> geo2) {
    return Expressions.TRUE;
  }

  @Test
  public void test() {
    QDelegate3Test_Geometry.geometry.isWithin(null);
    QDelegate3Test_Point.point.isWithin(null);
    QDelegate3Test_Polygon.polygon.isWithin(null);
  }
}
