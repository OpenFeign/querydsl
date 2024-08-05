package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.SchemaAndTable;
import org.junit.Test;

public class SchemaAndTableTest {

  @Test
  public void equalsAndHashCode() {
    var st1 = new SchemaAndTable(null, "table");
    var st2 = new SchemaAndTable(null, "table");
    assertThat(st2).isEqualTo(st1);
    assertThat(st2.hashCode()).isEqualTo(st1.hashCode());
  }
}
