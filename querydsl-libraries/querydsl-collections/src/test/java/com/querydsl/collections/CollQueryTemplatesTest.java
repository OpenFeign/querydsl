package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplatesTestUtils;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class CollQueryTemplatesTest {

  @Test
  public void generic_precedence() {
    TemplatesTestUtils.testPrecedence(CollQueryTemplates.DEFAULT);
  }

  @Test
  public void concat() {
    var a = Expressions.stringPath("a");
    var b = Expressions.stringPath("b");
    Expression<?> expr = a.append(b).toLowerCase();
    var str = new CollQuerySerializer(CollQueryTemplates.DEFAULT).handle(expr).toString();
    assertThat(str).isEqualTo("(a + b).toLowerCase()");
  }
}
