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
package com.querydsl.r2dbc;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.dml.R2DBCUpdateClause;
import com.querydsl.sql.SQLOps;
import org.junit.Test;

public class SQLServer2012TemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  @Test
  public void noFrom() {
    query.getMetadata().setProjection(Expressions.ONE);
    assertEquals("select 1", query.toString());
  }

  @Override
  protected SQLTemplates createTemplates() {
    return new SQLServer2012Templates();
  }

  @SuppressWarnings("unchecked")
  @Test
  @Override
  public void union() {
    NumberExpression<Integer> one = Expressions.ONE;
    NumberExpression<Integer> two = Expressions.TWO;
    NumberExpression<Integer> three = Expressions.THREE;
    Path<Integer> col1 = Expressions.path(Integer.class, "col1");
    Union union =
        query.union(
            R2DBCExpressions.select(one.as(col1)),
            R2DBCExpressions.select(two),
            R2DBCExpressions.select(three));
    assertEquals(
        "(select 1 as col1)\n" + "union\n" + "(select 2)\n" + "union\n" + "(select 3)",
        union.toString());
  }

  @Test
  public void limit() {
    query.from(survey1).limit(5);
    query.getMetadata().setProjection(survey1.id);
    assertEquals("select top 5 survey1.ID from SURVEY survey1", query.toString());
  }

  @Test
  public void limitOffset() {
    query.from(survey1).limit(5).offset(5);
    query.getMetadata().setProjection(survey1.id);
    assertEquals(
        "select survey1.ID from SURVEY survey1 "
            + "order by 1 asc "
            + "offset ? rows fetch next ? rows only",
        query.toString());
  }

  @Test
  public void delete_limit() {
    R2DBCDeleteClause clause = new R2DBCDeleteClause(null, createTemplates(), survey1);
    clause.where(survey1.name.eq("Bob"));
    clause.limit(5);
    assertEquals("delete top 5 from SURVEY\n" + "where SURVEY.NAME = ?", clause.toString());
  }

  @Test
  public void update_limit() {
    R2DBCUpdateClause clause = new R2DBCUpdateClause(null, createTemplates(), survey1);
    clause.set(survey1.name, "Bob");
    clause.limit(5);
    assertEquals("update top 5 SURVEY\n" + "set NAME = ?", clause.toString());
  }

  @Test
  public void modifiers() {
    query.from(survey1).limit(5).offset(3).orderBy(survey1.id.asc());
    query.getMetadata().setProjection(survey1.id);
    assertEquals(
        "select survey1.ID from SURVEY survey1 order by survey1.ID asc offset ? rows fetch next ?"
            + " rows only",
        query.toString());
  }

  @Test
  public void nextVal() {
    Operation<String> nextval =
        ExpressionUtils.operation(String.class, SQLOps.NEXTVAL, ConstantImpl.create("myseq"));
    assertSerialized(nextval, "next value for myseq");
  }
}
