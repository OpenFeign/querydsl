package com.querydsl.sql.codegen.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SchemaAndTable;
import org.junit.Test;

public class RenameMappingTest {

  private RenameMapping mapping = new RenameMapping();
  private Configuration configuration = new Configuration(SQLTemplates.DEFAULT);

  // to schema

  @Test
  public void schemaToSchema() {
    mapping.setFromSchema("ABC");
    mapping.setToSchema("DEF");
    mapping.apply(configuration);

    assertThat(configuration.getOverride(new SchemaAndTable("ABC", "TABLE")))
        .isEqualTo(new SchemaAndTable("DEF", "TABLE"));
    assertThat(configuration.getOverride(new SchemaAndTable("ABCD", "TABLE")))
        .isEqualTo(new SchemaAndTable("ABCD", "TABLE"));
  }

  // to table

  @Test
  public void tableToTable() {
    mapping.setFromTable("TABLE1");
    mapping.setToTable("TABLE2");
    mapping.apply(configuration);

    assertThat(configuration.getOverride(new SchemaAndTable("DEF", "TABLE1")))
        .isEqualTo(new SchemaAndTable("DEF", "TABLE2"));
    assertThat(configuration.getOverride(new SchemaAndTable("DEF", "TABLE3")))
        .isEqualTo(new SchemaAndTable("DEF", "TABLE3"));
  }

  @Test
  public void schemaTableToTable() {
    mapping.setFromSchema("ABC");
    mapping.setFromTable("TABLE1");
    mapping.setToTable("TABLE2");
    mapping.apply(configuration);

    assertThat(configuration.getOverride(new SchemaAndTable("ABC", "TABLE1")))
        .isEqualTo(new SchemaAndTable("ABC", "TABLE2"));
    assertThat(configuration.getOverride(new SchemaAndTable("DEF", "TABLE1")))
        .isEqualTo(new SchemaAndTable("DEF", "TABLE1"));
  }

  @Test
  public void schemaTableToSchemaTable() {
    mapping.setFromSchema("ABC");
    mapping.setFromTable("TABLE1");
    mapping.setToSchema("ABC");
    mapping.setToTable("TABLE2");
    mapping.apply(configuration);

    assertThat(configuration.getOverride(new SchemaAndTable("ABC", "TABLE1")))
        .isEqualTo(new SchemaAndTable("ABC", "TABLE2"));
    assertThat(configuration.getOverride(new SchemaAndTable("DEF", "TABLE1")))
        .isEqualTo(new SchemaAndTable("DEF", "TABLE1"));
  }

  // to column

  @Test
  public void schemaTableColumnToColumn() {
    mapping.setFromSchema("ABC");
    mapping.setFromTable("TABLE1");
    mapping.setFromColumn("COLUMN1");
    mapping.setToColumn("COLUMN2");
    mapping.apply(configuration);

    assertThat(configuration.getColumnOverride(new SchemaAndTable("ABC", "TABLE1"), "COLUMN1"))
        .isEqualTo("COLUMN2");
    assertThat(configuration.getColumnOverride(new SchemaAndTable("DEF", "TABLE1"), "COLUMN1"))
        .isEqualTo("COLUMN1");
  }

  @Test
  public void tableColumnToColumn() {
    mapping.setFromTable("TABLE1");
    mapping.setFromColumn("COLUMN1");
    mapping.setToColumn("COLUMN2");
    mapping.apply(configuration);

    assertThat(configuration.getColumnOverride(new SchemaAndTable("ABC", "TABLE1"), "COLUMN1"))
        .isEqualTo("COLUMN2");
    assertThat(configuration.getColumnOverride(new SchemaAndTable("ABC", "TABLE2"), "COLUMN1"))
        .isEqualTo("COLUMN1");
  }
}
