package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Ops;
import org.junit.Test;

public class TeradataTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new TeradataTemplates();
  }

  @Test
  public void limit() {
    query.from(survey1).limit(5);
    assertThat(query.toString())
        .isEqualTo("from SURVEY survey1 " + "qualify row_number() over (order by 1) <= ?");
  }

  @Test
  public void offset() {
    query.from(survey1).offset(5);
    assertThat(query.toString())
        .isEqualTo("from SURVEY survey1 " + "qualify row_number() over (order by 1) > ?");
  }

  @Test
  public void limit_offset() {
    query.from(survey1).limit(5).offset(10);
    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY survey1 \
            qualify row_number() over (order by 1) between ? and ?\
            """);
  }

  @Test
  public void orderBy_limit() {
    query.from(survey1).orderBy(survey1.name.asc()).limit(5);
    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY survey1 \
            order by survey1.NAME asc \
            qualify row_number() over (order by survey1.NAME asc) <= ?\
            """);
  }

  @Test
  public void precedence() {
    // +, - (unary)
    int p1 = getPrecedence(Ops.NEGATE);
    // ** (exponentation)
    // * / MOD
    int p2 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // +, - (binary)
    int p3 = getPrecedence(Ops.ADD, Ops.SUB);
    // concat
    int p4 = getPrecedence(Ops.CONCAT);
    // EQ, NE, GT, LE, LT, GE, IN, NOT IN, BEWEEN, LIKE
    int p5 =
        getPrecedence(
            Ops.EQ,
            Ops.NE,
            Ops.GT,
            Ops.LT,
            Ops.GOE,
            Ops.LOE,
            Ops.IN,
            Ops.NOT_IN,
            Ops.BETWEEN,
            Ops.LIKE,
            Ops.LIKE_ESCAPE);
    // NOT
    int p6 = getPrecedence(Ops.NOT);
    // AND
    int p7 = getPrecedence(Ops.AND);
    // OR
    int p8 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
    assertThat(p7 < p8).isTrue();
  }
}
