package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Coalesce;
import org.junit.Test;

public class CoalesceTest {

  @Test
  public void coalesce_supports_subquery() {
    var coalesce =
        new Coalesce<>(
            SQLExpressions.select(QCompanies.companies.name).from(QCompanies.companies),
            QCompanies.companies.name);
    assertThat(SQLExpressions.select(coalesce).toString())
        .isEqualTo(
            """
            select coalesce((select COMPANIES.NAME
            from COMPANIES COMPANIES), COMPANIES.NAME)
            from dual\
            """);
  }
}
