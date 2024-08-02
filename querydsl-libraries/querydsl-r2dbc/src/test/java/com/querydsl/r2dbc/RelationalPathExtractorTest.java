package com.querydsl.r2dbc;

import static com.querydsl.r2dbc.Constants.employee;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.sql.RelationalPathExtractor;
import org.junit.Test;

public class RelationalPathExtractorTest {

  private R2DBCQuery<?> query() {
    return new R2DBCQuery<Void>();
  }

  @Test
  public void simpleQuery() {
    var employee2 = new QEmployee("employee2");
    R2DBCQuery<?> query = query().from(employee, employee2);

    assertThat(RelationalPathExtractor.extract(query.getMetadata()))
        .isEqualTo(ImmutableSet.of(employee, employee2));
  }

  @Test
  public void joins() {
    var employee2 = new QEmployee("employee2");
    R2DBCQuery<?> query =
        query().from(employee).innerJoin(employee2).on(employee.superiorId.eq(employee2.id));

    assertThat(RelationalPathExtractor.extract(query.getMetadata()))
        .isEqualTo(ImmutableSet.of(employee, employee2));
  }

  @Test
  public void subQuery() {
    R2DBCQuery<?> query =
        query()
            .from(employee)
            .where(employee.id.eq(query().from(employee).select(employee.id.max())));
    assertThat(RelationalPathExtractor.extract(query.getMetadata()))
        .isEqualTo(ImmutableSet.of(employee));
  }

  @Test
  public void subQuery2() {
    var employee2 = new QEmployee("employee2");
    R2DBCQuery<?> query =
        query()
            .from(employee)
            .where(
                Expressions.list(employee.id, employee.lastname)
                    .in(query().from(employee2).select(employee2.id, employee2.lastname)));

    assertThat(RelationalPathExtractor.extract(query.getMetadata()))
        .isEqualTo(ImmutableSet.of(employee, employee2));
  }
}
