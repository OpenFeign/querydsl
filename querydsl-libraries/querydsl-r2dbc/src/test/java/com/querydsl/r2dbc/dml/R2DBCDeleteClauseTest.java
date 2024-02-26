package com.querydsl.r2dbc.dml;

import static org.junit.Assert.assertEquals;

import com.querydsl.r2dbc.KeyAccessorsTest.QEmployee;
import com.querydsl.r2dbc.SQLTemplates;
import com.querydsl.sql.SQLBindings;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;

public class R2DBCDeleteClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    QEmployee emp1 = new QEmployee("emp1");
    R2DBCDeleteClause delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));
    delete.execute().block();
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void error() {
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    R2DBCDeleteClause delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp2.id.eq(1));
  }

  @Test
  public void getSQL() {
    QEmployee emp1 = new QEmployee("emp1");
    R2DBCDeleteClause delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));

    SQLBindings sql = delete.getSQL().get(0);
    assertEquals("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?", sql.getSQL());
    assertEquals(Collections.singletonList(1), sql.getNullFriendlyBindings());
  }
}
