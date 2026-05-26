package com.querydsl.r2dbc.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.KeyAccessorsTest.QEmployee;
import com.querydsl.r2dbc.R2DBCExpressions;
import com.querydsl.r2dbc.SQLTemplates;
import java.util.Collections;
import org.junit.Test;

public class R2DBCUpdateClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var emp1 = new QEmployee("emp1");
    var update = new R2DBCUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, 1);
    update.execute().block();
  }

  @Test
  public void getSQL() {
    var emp1 = new QEmployee("emp1");
    var update = new R2DBCUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, 1);

    var sql = update.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("update EMPLOYEE\nset ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void intertable() {
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    var update = new R2DBCUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update
        .set(emp1.id, 1)
        .where(
            emp1.id.eq(
                R2DBCExpressions.select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull())));

    var sql = update.getSQL().getFirst();
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set ID = ?
            where EMPLOYEE.ID = (select emp2.ID
            from EMPLOYEE emp2
            where emp2.SUPERIOR_ID is not null)\
            """);
  }

  @Test
  public void intertable2() {
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    var update = new R2DBCUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(
        emp1.id, R2DBCExpressions.select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull()));

    var sql = update.getSQL().getFirst();
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set ID = (select emp2.ID
            from EMPLOYEE emp2
            where emp2.SUPERIOR_ID is not null)\
            """);
  }

  @Test
  public void intertable3() {
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    var update = new R2DBCUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(
        emp1.superiorId, R2DBCExpressions.select(emp2.id).from(emp2).where(emp2.id.eq(emp1.id)));

    var sql = update.getSQL().getFirst();
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set SUPERIOR_ID = (select emp2.ID
            from EMPLOYEE emp2
            where emp2.ID = EMPLOYEE.ID)\
            """);
  }
}
