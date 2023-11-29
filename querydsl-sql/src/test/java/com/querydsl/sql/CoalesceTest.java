package com.querydsl.sql;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.querydsl.core.types.dsl.Coalesce;
import org.junit.Test;

public class CoalesceTest {

  @Test
  public void coalesce_supports_subquery() {
    Coalesce<String> coalesce =
        new Coalesce<String>(
            SQLExpressions.select(QCompanies.companies.name).from(QCompanies.companies),
            QCompanies.companies.name);
    assertThat(
        SQLExpressions.select(coalesce).toString(),
        is(
            equalTo(
                "select coalesce((select COMPANIES.NAME\nfrom COMPANIES COMPANIES), COMPANIES.NAME)\nfrom dual")));
  }
}
