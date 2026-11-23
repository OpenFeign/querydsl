package fluentq.apt.domain;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.types.Ops;
import fluentq.core.types.dsl.Expressions;
import fluentq.core.types.dsl.NumberExpression;
import java.math.BigDecimal;
import org.joda.money.Money;
import org.joda.money.QMoney;

public final class JodaMoneyHelpers {

  private JodaMoneyHelpers() {}

  @QueryDelegate(Money.class)
  public static NumberExpression<BigDecimal> sum(QMoney money) {
    return Expressions.numberOperation(BigDecimal.class, Ops.AggOps.SUM_AGG, money);
  }
}
