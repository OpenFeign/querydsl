package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Coalesce;
import org.junit.Test;

public class CoalesceTest {

  @Test
  public void coalesce_supports_subquery() {
    Coalesce<String> coalesce =
        new Coalesce<String>(
            R2DBCExpressions.select(QCompanies.companies.name).from(QCompanies.companies),
            QCompanies.companies.name);
    assertThat(R2DBCExpressions.select(coalesce).toString())
        .isEqualTo(
            "select coalesce((select COMPANIES.NAME\n"
                + "from COMPANIES COMPANIES), COMPANIES.NAME)\n"
                + "from dual");
  }
}
