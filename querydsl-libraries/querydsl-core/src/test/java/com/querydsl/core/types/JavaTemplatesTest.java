package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JavaTemplatesTest {

  private Templates templates = JavaTemplates.DEFAULT;

  @Test
  void precedence() {
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

    assertThat(p1).isLessThan(p2);
    assertThat(p2).isLessThan(p3);
    assertThat(p3).isLessThan(p4);
    assertThat(p4).isLessThan(p5);
    assertThat(p5).isLessThan(p6);
    assertThat(p6).isLessThan(p7);
  }

  @Test
  void generic_precedence() {
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
