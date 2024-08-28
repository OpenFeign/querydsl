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
package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class DefaultQueryMetadataTest {

  private final QueryMetadata metadata = new DefaultQueryMetadata();

  public DefaultQueryMetadataTest() {
    metadata.setValidate(true);
  }

  private final StringPath str = Expressions.stringPath("str");

  private final StringPath str2 = Expressions.stringPath("str2");

  @Test
  public void addWhere_with_null() {
    metadata.addWhere(null);
  }

  @Test
  public void addWhere_with_booleanBuilder() {
    metadata.addWhere(new BooleanBuilder());
  }

  @Test
  public void addHaving_with_null() {
    metadata.addHaving(null);
  }

  @Test
  public void addHaving_with_booleanBuilder() {
    metadata.addHaving(new BooleanBuilder());
  }

  @Test(expected = IllegalArgumentException.class)
  public void validation() {
    metadata.addWhere(str.isNull());
  }

  @Test
  public void validation_no_error_for_groupBy() {
    metadata.addGroupBy(str);
  }

  @Test
  public void validation_no_error_for_having() {
    metadata.addHaving(str.isNull());
  }

  @Test
  public void getGroupBy() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addGroupBy(str);
    assertThat(metadata.getGroupBy()).isEqualTo(Collections.singletonList(str));
  }

  @Test
  public void getHaving() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addHaving(str.isNotNull());
    assertThat(metadata.getHaving()).isEqualTo(str.isNotNull());
  }

  @Test
  public void getJoins() {
    metadata.addJoin(JoinType.DEFAULT, str);
    assertThat(metadata.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, str)));
  }

  @Test
  public void getJoins2() {
    metadata.addJoin(JoinType.DEFAULT, str);
    assertThat(metadata.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, str)));
  }

  @Test
  public void getJoins3() {
    metadata.addJoin(JoinType.DEFAULT, str);
    assertThat(metadata.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, str)));
    metadata.addJoinCondition(str.isNull());
    assertThat(metadata.getJoins())
        .isEqualTo(
            Collections.singletonList(
                new JoinExpression(JoinType.DEFAULT, str, str.isNull(), Collections.emptySet())));
    metadata.addJoin(JoinType.DEFAULT, str2);
    assertThat(metadata.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, str, str.isNull(), Collections.emptySet()),
                new JoinExpression(JoinType.DEFAULT, str2)));
  }

  @Test
  public void getModifiers() {
    var modifiers = new QueryModifiers(1L, 2L);
    metadata.setModifiers(modifiers);
    assertThat(metadata.getModifiers()).isEqualTo(modifiers);
  }

  @Test
  public void setLimit() {
    var modifiers = new QueryModifiers(1L, 2L);
    metadata.setModifiers(modifiers);
    metadata.setLimit(3L);

    assertThat(metadata.getModifiers().getLimit()).isEqualTo(Long.valueOf(3L));
    assertThat(metadata.getModifiers().getOffset()).isEqualTo(Long.valueOf(2L));
  }

  @Test
  public void setOffset() {
    var modifiers = new QueryModifiers(1L, 1L);
    metadata.setModifiers(modifiers);
    metadata.setOffset(2L);

    assertThat(metadata.getModifiers().getLimit()).isEqualTo(Long.valueOf(1L));
    assertThat(metadata.getModifiers().getOffset()).isEqualTo(Long.valueOf(2L));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getOrderBy() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addOrderBy(str.asc());
    metadata.addOrderBy(str.desc());
    assertThat(metadata.getOrderBy()).isEqualTo(Arrays.asList(str.asc(), str.desc()));
  }

  @Test
  public void getProjection() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.setProjection(str.append("abc"));
    assertThat(metadata.getProjection()).isEqualTo(str.append("abc"));
  }

  @Test
  public void getWhere() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addWhere(str.eq("b"));
    metadata.addWhere(str.isNotEmpty());
    assertThat(metadata.getWhere()).isEqualTo(str.eq("b").and(str.isNotEmpty()));
  }

  @Test
  public void isDistinct() {
    assertThat(metadata.isDistinct()).isFalse();
    metadata.setDistinct(true);
    assertThat(metadata.isDistinct()).isTrue();
  }

  @Test
  public void isUnique() {
    assertThat(metadata.isUnique()).isFalse();
    metadata.setUnique(true);
    assertThat(metadata.isUnique()).isTrue();
  }

  @Test
  public void joinShouldBeCommitted() {
    var md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, str);
    var emptyMetadata = new DefaultQueryMetadata();
    assertThat(md.equals(emptyMetadata)).isFalse();
  }

  @Test
  public void clone_() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addGroupBy(str);
    metadata.addHaving(str.isNotNull());
    metadata.addJoin(JoinType.DEFAULT, str2);
    var modifiers = new QueryModifiers(1L, 2L);
    metadata.setModifiers(modifiers);
    metadata.addOrderBy(str.asc());
    metadata.setProjection(str.append("abc"));
    metadata.addWhere(str.eq("b"));
    metadata.addWhere(str.isNotEmpty());

    var clone = metadata.clone();
    assertThat(clone.getGroupBy()).isEqualTo(metadata.getGroupBy());
    assertThat(clone.getHaving()).isEqualTo(metadata.getHaving());
    assertThat(clone.getJoins()).isEqualTo(metadata.getJoins());
    assertThat(clone.getModifiers()).isEqualTo(metadata.getModifiers());
    assertThat(clone.getOrderBy()).isEqualTo(metadata.getOrderBy());
    assertThat(clone.getProjection()).isEqualTo(metadata.getProjection());
    assertThat(clone.getWhere()).isEqualTo(metadata.getWhere());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void setParam() {
    metadata.setParam(new Param(String.class, "str"), ConstantImpl.create("X"));
    assertThat(metadata.getParams()).hasSize(1);
    assertThat(
            metadata
                .getParams()
                .get(new Param(String.class, "str"))
                .equals(ConstantImpl.create("X")))
        .isTrue();
  }

  @Test
  public void addFlag() {
    var flag = new QueryFlag(Position.START, "X");
    metadata.addFlag(flag);
    assertThat(metadata.hasFlag(flag)).isTrue();
  }

  @Test
  public void equals() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addGroupBy(str);
    metadata.addHaving(str.isNotNull());
    metadata.addJoin(JoinType.DEFAULT, str2);
    var modifiers = new QueryModifiers(1L, 2L);
    metadata.setModifiers(modifiers);
    metadata.addOrderBy(str.asc());
    metadata.setProjection(str.append("abc"));
    metadata.addWhere(str.eq("b"));
    metadata.addWhere(str.isNotEmpty());

    QueryMetadata metadata2 = new DefaultQueryMetadata();
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addJoin(JoinType.DEFAULT, str);
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addGroupBy(str);
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addHaving(str.isNotNull());
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addJoin(JoinType.DEFAULT, str2);
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.setModifiers(modifiers);
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addOrderBy(str.asc());
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.setProjection(str.append("abc"));
    assertThat(metadata.equals(metadata2)).isFalse();
    metadata2.addWhere(str.eq("b"));
    metadata2.addWhere(str.isNotEmpty());
    assertThat(metadata.equals(metadata2)).isTrue();
  }

  @Test
  public void hashCode_() {
    metadata.addJoin(JoinType.DEFAULT, str);
    metadata.addGroupBy(str);
    metadata.addHaving(str.isNotNull());
    metadata.addJoin(JoinType.DEFAULT, str2);
    var modifiers = new QueryModifiers(1L, 2L);
    metadata.setModifiers(modifiers);
    metadata.addOrderBy(str.asc());
    metadata.setProjection(str.append("abc"));
    metadata.addWhere(str.eq("b"));
    metadata.addWhere(str.isNotEmpty());
    metadata.hashCode();
  }

  @Test
  public void hashCode_empty_metadata() {
    metadata.hashCode();
  }
}
