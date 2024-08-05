package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.KeyAccessorsTest;
import org.junit.Test;

public class SQLMergeClauseTest {

  @Test
  public void clear() {
    var emp1 = new KeyAccessorsTest.QEmployee("emp1");
    var merge = new SQLMergeClause(null, new H2Templates(), emp1);
    merge.set(emp1.id, 1);
    merge.addBatch();
    assertThat(merge.getBatchCount()).isEqualTo(1);
    merge.clear();
    assertThat(merge.getBatchCount()).isEqualTo(0);
  }
}
