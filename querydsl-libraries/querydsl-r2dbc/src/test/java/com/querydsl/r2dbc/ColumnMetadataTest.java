package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import org.junit.Test;

public class ColumnMetadataTest {

  @Test
  public void defaultColumn() {
    var column = ColumnMetadata.named("Person");
    assertThat(column.getName()).isEqualTo("Person");
    assertThat(column.hasJdbcType()).isFalse();
    assertThat(column.hasSize()).isFalse();
    assertThat(column.isNullable()).isTrue();
  }

  @Test
  public void fullyConfigured() {
    var column = ColumnMetadata.named("Person").withSize(10).notNull().ofType(Types.BIGINT);
    assertThat(column.getName()).isEqualTo("Person");
    assertThat(column.hasJdbcType()).isTrue();
    assertThat(column.getJdbcType()).isEqualTo(Types.BIGINT);
    assertThat(column.hasSize()).isTrue();
    assertThat(column.getSize()).isEqualTo(10);
    assertThat(column.isNullable()).isFalse();
  }

  @Test
  public void extractFromRelationalPath() {
    var column = ColumnMetadata.getColumnMetadata(QEmployee.employee.id);
    assertThat(column.getName()).isEqualTo("ID");
  }

  @Test
  public void fallBackToDefaultWhenMissing() {
    var column = ColumnMetadata.getColumnMetadata(QEmployee.employee.salary);
    assertThat(column.getName()).isEqualTo("SALARY");
  }
}
