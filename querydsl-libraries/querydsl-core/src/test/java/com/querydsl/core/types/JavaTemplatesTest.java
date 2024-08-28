package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class JavaTemplatesTest {

  private Templates templates = JavaTemplates.DEFAULT;

  @Test
  public void precedence() {
    // postfix    expr++ expr--
    // unary    ++expr --expr +expr -expr ~ !
    // multiplicative    * / %
    // additive    + -
    // shift    << >> >>>
    // relational    < > <= >= instanceof
    // equality    == !=
    // bitwise AND    &
    // bitwise exclusive OR    ^
    // bitwise inclusive OR    |
    // logical AND    &&
    // logical OR    ||
    // ternary    ? :
    // assignment    = += -= *= /= %= &= ^= |= <<= >>= >>>=

    var p1 = getPrecedence(Ops.NOT);
    var p2 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    var p3 = getPrecedence(Ops.ADD, Ops.SUB);
    var p4 = getPrecedence(Ops.LT, Ops.GT, Ops.GOE, Ops.LOE, Ops.BETWEEN, Ops.INSTANCE_OF);
    var p5 = getPrecedence(Ops.EQ, Ops.NE);
    var p6 = getPrecedence(Ops.AND);
    var p7 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
  }

  @Test
  public void generic_precedence() {
    TemplatesTestUtils.testPrecedence(JavaTemplates.DEFAULT);
  }

  protected int getPrecedence(Operator... ops) {
    var precedence = templates.getPrecedence(ops[0]);
    for (var i = 1; i < ops.length; i++) {
      assertThat(templates.getPrecedence(ops[i])).as(ops[i].name()).isEqualTo(precedence);
    }
    return precedence;
  }
}
