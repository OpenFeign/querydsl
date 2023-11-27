package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.TemplatesTestUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class JPQLTemplatesTest {

  @Test
  public void escape() {
    List<Templates> templates =
        Arrays.<Templates>asList(
            new JPQLTemplates(), new HQLTemplates(),
            new EclipseLinkTemplates(), new OpenJPATemplates());

    for (Templates t : templates) {
      assertThat(t.getTemplate(Ops.LIKE).toString()).isEqualTo("{0} like {1} escape '!'");
    }
  }

  @Test
  public void custom_escape() {
    List<Templates> templates =
        Arrays.<Templates>asList(
            new JPQLTemplates('X'), new HQLTemplates('X'),
            new EclipseLinkTemplates('X'), new OpenJPATemplates('X'));

    for (Templates t : templates) {
      assertThat(t.getTemplate(Ops.LIKE).toString()).isEqualTo("{0} like {1} escape 'X'");
    }
  }

  @Test
  public void precedence() {
    // Navigation operator (.)
    // +, - unary *,
    int p1 = getPrecedence(Ops.NEGATE);
    // / multiplication and division
    int p2 = getPrecedence(Ops.MULT, Ops.DIV);
    // +, - addition and subtraction
    int p3 = getPrecedence(Ops.ADD, Ops.SUB);
    // Comparison operators : =, >, >=, <, <=, <> (not equal), [NOT] BETWEEN, [NOT] LIKE, [NOT] IN,
    // IS [NOT] NULL, IS [NOT] EMPTY, [NOT] MEMBER [OF]
    int p4 =
        getPrecedence(
            Ops.EQ,
            Ops.GT,
            Ops.GOE,
            Ops.LT,
            Ops.LOE,
            Ops.NE,
            Ops.BETWEEN,
            Ops.LIKE,
            Ops.LIKE_ESCAPE,
            Ops.IN,
            Ops.IS_NULL,
            Ops.IS_NOT_NULL,
            JPQLOps.MEMBER_OF,
            JPQLOps.NOT_MEMBER_OF);
    // NOT
    int p5 = getPrecedence(Ops.NOT);
    // AND
    int p6 = getPrecedence(Ops.AND);
    // OR
    int p7 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
  }

  protected int getPrecedence(Operator... ops) {
    int precedence = JPQLTemplates.DEFAULT.getPrecedence(ops[0]);
    for (int i = 1; i < ops.length; i++) {
      assertThat(JPQLTemplates.DEFAULT.getPrecedence(ops[i]))
          .as(ops[i].name())
          .isEqualTo(precedence);
    }
    return precedence;
  }

  @Test
  public void generic_precedence() {
    for (JPQLTemplates templates :
        Arrays.asList(JPQLTemplates.DEFAULT, HQLTemplates.DEFAULT, EclipseLinkTemplates.DEFAULT)) {
      TemplatesTestUtils.testPrecedence(templates);
    }
  }
}
