package com.querydsl.r2dbc;

import static org.junit.Assert.assertEquals;

import com.querydsl.sql.SchemaAndTable;
import org.junit.Test;

public class SchemaAndTableTest {

  @Test
  public void equalsAndHashCode() {
    SchemaAndTable st1 = new SchemaAndTable(null, "table");
    SchemaAndTable st2 = new SchemaAndTable(null, "table");
    assertEquals(st1, st2);
    assertEquals(st1.hashCode(), st2.hashCode());
  }
}
