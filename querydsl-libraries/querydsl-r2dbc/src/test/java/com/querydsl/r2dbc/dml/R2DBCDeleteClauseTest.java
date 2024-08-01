package com.querydsl.r2dbc.dml;

import static org.assertj.core.api.Assertions.assertThat;

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

    SQLBindings sql = delete.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }
}
