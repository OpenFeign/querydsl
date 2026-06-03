package com.querydsl.r2dbc.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.querydsl.r2dbc.KeyAccessorsTest.QEmployee;
import com.querydsl.r2dbc.SQLTemplates;
import java.util.Collections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class R2DBCDeleteClauseTest {

  @Test
  public void noConnection() {
    assertThatThrownBy(
            () -> {
              var emp1 = new QEmployee("emp1");
              var delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
              delete.where(emp1.id.eq(1));
              delete.execute().block();
            })
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @Disabled
  public void error() {
    assertThatThrownBy(
            () -> {
              var emp1 = new QEmployee("emp1");
              var emp2 = new QEmployee("emp2");
              var delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
              delete.where(emp2.id.eq(1));
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void getSQL() {
    var emp1 = new QEmployee("emp1");
    var delete = new R2DBCDeleteClause(null, SQLTemplates.DEFAULT, emp1);
    delete.where(emp1.id.eq(1));

    var sql = delete.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }
}
