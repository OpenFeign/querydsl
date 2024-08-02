package com.querydsl.r2dbc.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.KeyAccessorsTest.QEmployee;
import com.querydsl.r2dbc.SQLTemplates;
import java.util.Collections;
import org.junit.Test;

public class R2DBCInsertClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var emp1 = new QEmployee("emp1");
    var insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);
    insert.execute().block();
  }

  @Test
  public void getSQL() {
    var emp1 = new QEmployee("emp1");
    var insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);

    var sql = insert.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("insert into EMPLOYEE (ID)\nvalues (?)");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void getSQLWithPreservedColumnOrder() {
    var emp1 = new com.querydsl.r2dbc.domain.QEmployee("emp1");
    var insert = new R2DBCInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.populate(emp1);

    var sql = insert.getSQL().getFirst();
    assertThat(sql.getSQL())
        .as("The order of columns in generated sql should be predictable")
        .isEqualTo(
            """
            insert into EMPLOYEE (ID, FIRSTNAME, LASTNAME, SALARY, DATEFIELD, TIMEFIELD,\
             SUPERIOR_ID)
            values (EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY,\
             EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID)\
            """);
  }
}
