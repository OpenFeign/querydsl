package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryFlag;
import com.querydsl.sql.KeyAccessorsTest.QEmployee;
import com.querydsl.sql.SQLTemplates;
import java.util.Collections;
import org.junit.Test;

public class SQLInsertClauseTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var emp1 = new QEmployee("emp1");
    var insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);
    insert.execute();
  }

  @Test
  public void getSQL() {
    var emp1 = new QEmployee("emp1");
    var insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);

    var sql = insert.getSQL().getFirst();
    assertThat(sql.getSQL()).isEqualTo("insert into EMPLOYEE (ID)\nvalues (?)");
    assertThat(sql.getNullFriendlyBindings()).isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void bulk() {
    var emp1 = new QEmployee("emp1");
    var insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);
    insert.addBatch();
    insert.set(emp1.id, 2);
    insert.addBatch();
    insert.addFlag(QueryFlag.Position.END, " on duplicate key ignore");
    insert.setBatchToBulk(true);
    assertThat(insert.getSQL().getFirst().getSQL())
        .isEqualTo("insert into EMPLOYEE (ID)\n" + "values (?), (?) on duplicate key ignore");
  }

  @Test
  public void getSQLWithPreservedColumnOrder() {
    var emp1 = new com.querydsl.sql.domain.QEmployee("emp1");
    var insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.populate(emp1);

    var sql = insert.getSQL().getFirst();
    assertThat(sql.getSQL())
        .as("The order of columns in generated sql should be predictable")
        .isEqualTo(
            """
insert into EMPLOYEE (ID, FIRSTNAME, LASTNAME, SALARY, DATEFIELD, TIMEFIELD, SUPERIOR_ID)
values (EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID)\
""");
  }

  @Test
  public void clear() {
    var emp1 = new QEmployee("emp1");
    var insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
    insert.set(emp1.id, 1);
    insert.addBatch();
    assertThat(insert.getBatchCount()).isEqualTo(1);
    insert.clear();
    assertThat(insert.getBatchCount()).isEqualTo(0);
  }
}
