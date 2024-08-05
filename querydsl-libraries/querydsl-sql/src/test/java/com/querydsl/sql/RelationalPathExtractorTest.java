package com.querydsl.sql;

import static com.querydsl.sql.Constants.employee;
import static com.querydsl.sql.RelationalPathExtractor.extract;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.domain.QEmployee;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Test;

public class RelationalPathExtractorTest {

  private SQLQuery<?> query() {
    return new SQLQuery<Void>();
  }

  @Test
  public void simpleQuery() {
    var employee2 = new QEmployee("employee2");
    SQLQuery<?> query = query().from(employee, employee2);

    assertThat(extract(query.getMetadata()))
        .isEqualTo(new HashSet<>(Arrays.asList(employee, employee2)));
  }

  @Test
  public void joins() {
    var employee2 = new QEmployee("employee2");
    SQLQuery<?> query =
        query().from(employee).innerJoin(employee2).on(employee.superiorId.eq(employee2.id));

    assertThat(extract(query.getMetadata()))
        .isEqualTo(new HashSet<>(Arrays.asList(employee, employee2)));
  }

  @Test
  public void subQuery() {
    SQLQuery<?> query =
        query()
            .from(employee)
            .where(employee.id.eq(query().from(employee).select(employee.id.max())));
    assertThat(extract(query.getMetadata())).isEqualTo(Collections.singleton(employee));
  }

  @Test
  public void subQuery2() {
    var employee2 = new QEmployee("employee2");
    SQLQuery<?> query =
        query()
            .from(employee)
            .where(
                Expressions.list(employee.id, employee.lastname)
                    .in(query().from(employee2).select(employee2.id, employee2.lastname)));

    assertThat(extract(query.getMetadata()))
        .isEqualTo(new HashSet<>(Arrays.asList(employee, employee2)));
  }
}
