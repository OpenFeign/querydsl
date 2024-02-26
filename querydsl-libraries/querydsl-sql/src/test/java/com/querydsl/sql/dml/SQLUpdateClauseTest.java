package com.querydsl.sql.dml;

import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryFlag.Position;
import com.querydsl.sql.KeyAccessorsTest.QEmployee;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLTemplates;
import java.util.Collections;
import org.junit.Test;

public class SQLUpdateClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, 1);
    update.execute();
  }

  @Test
  public void getSQL() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, 1);

    SQLBindings sql = update.getSQL().get(0);
    assertThat(sql.getSQL()).isEqualTo("update EMPLOYEE\nset ID = ?");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void intertable() {
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update
        .set(emp1.id, 1)
        .where(emp1.id.eq(select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull())));

    SQLBindings sql = update.getSQL().get(0);
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
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull()));

    SQLBindings sql = update.getSQL().get(0);
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
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.superiorId, select(emp2.id).from(emp2).where(emp2.id.eq(emp1.id)));

    SQLBindings sql = update.getSQL().get(0);
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set SUPERIOR_ID = (select emp2.ID
            from EMPLOYEE emp2
            where emp2.ID = EMPLOYEE.ID)\
            """);
  }

  @Test
  public void testBeforeFiltersFlag() {
    QEmployee emp1 = new QEmployee("emp1");
    QEmployee emp2 = new QEmployee("emp2");
    SQLUpdateClause update =
        new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1)
            .set(emp1.superiorId, emp2.id)
            .addFlag(Position.BEFORE_FILTERS, "\nfrom %s %s".formatted(emp2.getTableName(), emp2))
            .where(emp2.id.eq(emp1.id));

    SQLBindings sql = update.getSQL().get(0);
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set SUPERIOR_ID = emp2.ID
            from EMPLOYEE emp2
            where emp2.ID = EMPLOYEE.ID\
            """);

    update =
        new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1)
            .set(emp1.superiorId, emp2.id)
            .addFlag(Position.BEFORE_FILTERS, " THE_FLAG")
            .where(emp2.id.eq(emp1.id));

    sql = update.getSQL().get(0);
    assertThat(sql.getSQL())
        .isEqualTo(
            """
            update EMPLOYEE
            set SUPERIOR_ID = emp2.ID THE_FLAG
            where emp2.ID = EMPLOYEE.ID\
            """);
  }

  @Test
  public void clear() {
    QEmployee emp1 = new QEmployee("emp1");
    SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
    update.set(emp1.id, 1);
    update.addBatch();
    assertThat(update.getBatchCount()).isEqualTo(1);
    update.clear();
    assertThat(update.getBatchCount()).isEqualTo(0);
  }
}
