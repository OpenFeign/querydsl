package com.querydsl.collections;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplatesTestUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class CollQueryTemplatesTest {

  @Test
  public void generic_precedence() {
    TemplatesTestUtils.testPrecedence(CollQueryTemplates.DEFAULT);
  }

  @Test
  public void concat() {
    StringPath a = Expressions.stringPath("a");
    StringPath b = Expressions.stringPath("b");
    Expression<?> expr = a.append(b).toLowerCase();
    String str = new CollQuerySerializer(CollQueryTemplates.DEFAULT).handle(expr).toString();
    assertEquals("(a + b).toLowerCase()", str);
  }
}
