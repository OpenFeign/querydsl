package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.KeyAccessorsTest.QEmployee;
import com.querydsl.sql.SQLTemplates;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;

public class SQLDeleteClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var emp1 = new QEmployee("emp1");
    var delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));
    delete.execute();
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void error() {
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    var delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp2.id.eq(1));
  }

  @Test
  public void getSQL() {
    var emp1 = new QEmployee("emp1");
    var delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));

    var sql = delete.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void clear() {
    var emp1 = new QEmployee("emp1");
    var delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));
    delete.addBatch();
    assertThat(delete.getBatchCount()).isEqualTo(1);
    delete.clear();
    assertThat(delete.getBatchCount()).isEqualTo(0);
  }
}
