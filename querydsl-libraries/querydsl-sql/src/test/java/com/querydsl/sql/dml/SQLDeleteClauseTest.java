package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.KeyAccessorsTest.QEmployee;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLTemplates;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;

public class SQLDeleteClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));
    delete.execute();
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void error() {
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp2.id.eq(1));
  }

  @Test
  public void getSQL() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));

    SQLBindings sql = delete.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void clear() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));
    delete.addBatch();
    assertThat(delete.getBatchCount()).isEqualTo(1);
    delete.clear();
    assertThat(delete.getBatchCount()).isEqualTo(0);
  }
}
