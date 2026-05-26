package fluentq.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.Tuple;
import fluentq.core.types.FactoryExpression;
import fluentq.core.types.Projections;
import fluentq.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

class ConstantHidingExpressionTest {

  @Test
  void constants_hidden() {
    FactoryExpression<Tuple> tuple =
        Projections.tuple(
            Expressions.stringPath("str"),
            Expressions.TRUE,
            Expressions.FALSE.as("false"),
            Expressions.constant(1));
    FactoryExpression<Tuple> wrapped = new ConstantHidingExpression<>(tuple);
    assertThat(wrapped.getArgs()).hasSize(1);
    Tuple t = wrapped.newInstance("s");

    assertThat(t.get(Expressions.stringPath("str"))).isEqualTo("s");
    assertThat(t.get(Expressions.TRUE)).isEqualTo(Boolean.TRUE);
    assertThat(t.get(Expressions.FALSE.as("false"))).isEqualTo(Boolean.FALSE);
    assertThat(t.get(Expressions.constant(1))).isEqualTo(Integer.valueOf(1));
  }
}
