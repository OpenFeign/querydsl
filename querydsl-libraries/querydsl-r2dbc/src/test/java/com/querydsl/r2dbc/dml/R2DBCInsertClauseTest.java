package com.querydsl.r2dbc.dml;

import static org.junit.Assert.assertEquals;

import com.querydsl.r2dbc.KeyAccessorsTest.QEmployee;
import com.querydsl.r2dbc.SQLTemplates;
import com.querydsl.sql.SQLBindings;
import java.util.Collections;
import org.junit.Test;

public class R2DBCInsertClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    QEmployee emp1 = new QEmployee("emp1");
    R2DBCInsertClause insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);
    insert.execute().block();
  }

  @Test
  public void getSQL() {
    QEmployee emp1 = new QEmployee("emp1");
    R2DBCInsertClause insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);

    SQLBindings sql = insert.getSQL().get(0);
    assertEquals("insert into EMPLOYEE (ID)\nvalues (?)", sql.getSQL());
    assertEquals(Collections.singletonList(1), sql.getNullFriendlyBindings());
  }

  @Test
  public void getSQLWithPreservedColumnOrder() {
    com.querydsl.r2dbc.domain.QEmployee emp1 = new com.querydsl.r2dbc.domain.QEmployee("emp1");
    R2DBCInsertClause insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.populate(emp1);

    SQLBindings sql = insert.getSQL().get(0);
    assertEquals(
        "The order of columns in generated sql should be predictable",
        "insert into EMPLOYEE (ID, FIRSTNAME, LASTNAME, SALARY, DATEFIELD, TIMEFIELD,"
            + " SUPERIOR_ID)\n"
            + "values (EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY,"
            + " EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID)",
        sql.getSQL());
  }
}
