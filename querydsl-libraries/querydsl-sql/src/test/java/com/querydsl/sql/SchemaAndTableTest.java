package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SchemaAndTableTest {

  @Test
  public void equalsAndHashCode() {
    SchemaAndTable st1 = new SchemaAndTable(null, "table");
    SchemaAndTable st2 = new SchemaAndTable(null, "table");
    assertThat(st2).isEqualTo(st1);
    assertThat(st2.hashCode()).isEqualTo(st1.hashCode());
  }
}
