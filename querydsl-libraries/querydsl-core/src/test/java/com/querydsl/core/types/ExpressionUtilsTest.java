/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class ExpressionUtilsTest {

  private static final StringPath str = Expressions.stringPath("str");

  private static final StringPath str2 = Expressions.stringPath("str2");

  @Test
  public void likeToRegex() {
    assertThat(regex(ConstantImpl.create("%"))).isEqualTo(".*");
    assertThat(regex(ConstantImpl.create("abc%"))).isEqualTo("^abc.*");
    assertThat(regex(ConstantImpl.create("%abc"))).isEqualTo(".*abc$");
    assertThat(regex(ConstantImpl.create("_"))).isEqualTo("^.$");

    var path = Expressions.stringPath("path");
    assertThat(regex(path.append("%"))).isEqualTo("path + .*");
    assertThat(regex(path.prepend("%"))).isEqualTo(".* + path");
    assertThat(regex(path.append("_"))).isEqualTo("path + .");
    assertThat(regex(path.prepend("_"))).isEqualTo(". + path");
  }

  @Test
  @Ignore
  public void likeToRegexSpeed() {
    // 4570
    var path = Expressions.stringPath("path");
    final var iterations = 1000000;
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      regex(ConstantImpl.create("%"));
      regex(ConstantImpl.create("abc%"));
      regex(ConstantImpl.create("%abc"));
      regex(ConstantImpl.create("_"));
      regex(path.append("%"));
      regex(path.prepend("%"));
      regex(path.append("_"));
      regex(path.prepend("_"));
    }
    var duration = System.currentTimeMillis() - start;
    System.err.println(duration);
  }

  @Test
  public void likeToRegex_escape() {
    assertThat(regex(ConstantImpl.create("."))).isEqualTo("^\\.$");
  }

  @Test
  public void regexToLike() {
    assertThat(like(ConstantImpl.create(".*"))).isEqualTo("%");
    assertThat(like(ConstantImpl.create("."))).isEqualTo("_");
    assertThat(like(ConstantImpl.create("\\."))).isEqualTo(".");

    var path = Expressions.stringPath("path");
    assertThat(like(path.append(".*"))).isEqualTo("path + %");
    assertThat(like(path.prepend(".*"))).isEqualTo("% + path");
    assertThat(like(path.append("."))).isEqualTo("path + _");
    assertThat(like(path.prepend("."))).isEqualTo("_ + path");
  }

  @Test(expected = QueryException.class)
  public void regexToLike_fail() {
    like(ConstantImpl.create("a*"));
  }

  @Test(expected = QueryException.class)
  public void regexToLike_fail2() {
    like(ConstantImpl.create("\\d"));
  }

  @Test(expected = QueryException.class)
  public void regexToLike_fail3() {
    like(ConstantImpl.create("[ab]"));
  }

  @Test
  @Ignore
  public void regexToLikeSpeed() {
    // 3255
    var path = Expressions.stringPath("path");
    final var iterations = 1000000;
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      like(ConstantImpl.create(".*"));
      like(ConstantImpl.create("."));
      like(path.append(".*"));
      like(path.prepend(".*"));
      like(path.append("."));
      like(path.prepend("."));
    }
    var duration = System.currentTimeMillis() - start;
    System.err.println(duration);
  }

  private String regex(Expression<String> expr) {
    return ExpressionUtils.likeToRegex(expr).toString();
  }

  private String like(Expression<String> expr) {
    return ExpressionUtils.regexToLike(expr).toString();
  }

  @Test
  public void count() {
    assertThat(ExpressionUtils.count(str)).hasToString("count(str)");
  }

  @Test
  public void eqConst() {
    assertThat(ExpressionUtils.eqConst(str, "X")).hasToString("str = X");
  }

  @Test
  public void eq() {
    assertThat(ExpressionUtils.eq(str, str2)).hasToString("str = str2");
  }

  @Test
  public void in() {
    assertThat(ExpressionUtils.in(str, Arrays.asList("a", "b", "c")).toString())
        .isEqualTo("str in [a, b, c]");
  }

  @Test
  public void in_subQuery() {
    var s =
        ExpressionUtils.in(
                str, new SubQueryExpressionImpl<>(String.class, new DefaultQueryMetadata()))
            .toString();
    assertThat(s).startsWith("str in com.querydsl.core.DefaultQueryMetadata@c");
  }

  @Test
  public void inAny() {
    Collection<List<String>> of =
        Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"));
    assertThat(ExpressionUtils.inAny(str, of).toString())
        .isEqualTo("str in [a, b, c] || str in [d, e, f]");
  }

  @Test
  public void isNull() {
    assertThat(ExpressionUtils.isNull(str)).hasToString("str is null");
  }

  @Test
  public void isNotNull() {
    assertThat(ExpressionUtils.isNotNull(str)).hasToString("str is not null");
  }

  @Test
  public void neConst() {
    assertThat(ExpressionUtils.neConst(str, "X")).hasToString("str != X");
  }

  @Test
  public void ne() {
    assertThat(ExpressionUtils.ne(str, str2)).hasToString("str != str2");
  }

  @Test
  public void notInAny() {
    Collection<List<String>> of =
        Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"));
    assertThat(ExpressionUtils.notInAny(str, of).toString())
        .isEqualTo("str not in [a, b, c] && str not in [d, e, f]");
  }

  @Test
  public void notIn_subQuery() {
    var s =
        ExpressionUtils.notIn(
                str, new SubQueryExpressionImpl<>(String.class, new DefaultQueryMetadata()))
            .toString();
    assertThat(s).startsWith("str not in com.querydsl.core.DefaultQueryMetadata@c");
  }
}
