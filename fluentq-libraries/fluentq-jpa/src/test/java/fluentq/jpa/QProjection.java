package fluentq.jpa;

import fluentq.core.types.ConstructorExpression;
import fluentq.core.types.Expression;
import fluentq.core.types.dsl.BooleanExpression;
import fluentq.core.types.dsl.NumberExpression;
import fluentq.core.types.dsl.StringExpression;
import fluentq.jpa.domain.Cat;
import fluentq.jpa.domain.QCat;

public class QProjection extends ConstructorExpression<Projection> {

  private static final long serialVersionUID = -5866362075090550839L;

  public QProjection(StringExpression str, QCat cat) {
    super(Projection.class, new Class<?>[] {String.class, Cat.class}, new Expression[] {str, cat});
  }

  public QProjection(NumberExpression<Integer> i, BooleanExpression b) {
    super(Projection.class, new Class<?>[] {int.class, boolean.class}, new Expression[] {i, b});
  }
}
