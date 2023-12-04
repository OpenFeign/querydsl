package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class PrecedenceTest {

  @Test
  public void test() {
    StringPath str1 = Expressions.stringPath("str1");
    StringPath str2 = Expressions.stringPath("str2");
    BooleanExpression pending = str1.eq("3").and(str2.eq("1"));
    BooleanExpression notNew = str1.ne("1").and(str2.in("2", "3"));
    BooleanExpression whereClause = str1.eq("a").and(pending.or(notNew));
    String str = new SQLSerializer(Configuration.DEFAULT).handle(whereClause).toString();
    assertThat(str)
        .isEqualTo("str1 = ? and (str1 = ? and str2 = ? or str1 != ? and str2 in (?, ?))");
  }
}
