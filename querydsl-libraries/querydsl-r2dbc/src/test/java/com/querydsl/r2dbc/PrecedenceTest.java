package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class PrecedenceTest {

  @Test
  public void test() {
    var str1 = Expressions.stringPath("str1");
    var str2 = Expressions.stringPath("str2");
    var pending = str1.eq("3").and(str2.eq("1"));
    var notNew = str1.ne("1").and(str2.in("2", "3"));
    var whereClause = str1.eq("a").and(pending.or(notNew));
    var str = new SQLSerializer(Configuration.DEFAULT).handle(whereClause).toString();
    assertThat(str)
        .isEqualTo("str1 = ? and (str1 = ? and str2 = ? or str1 != ? and str2 in (?, ?))");
  }
}
