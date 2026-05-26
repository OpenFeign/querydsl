package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.domain.QEmployee;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class DefaultMapperTest extends AbstractMapperTest {

  private static final QEmployee emp = QEmployee.employee;

  @Test
  public void extract() {
    var values = DefaultMapper.DEFAULT.createMap(emp, employee);
    assertThat(values).containsEntry(emp.datefield, employee.getDatefield());
    assertThat(values).containsEntry(emp.firstname, employee.getFirstname());
    assertThat(values).containsEntry(emp.lastname, employee.getLastname());
    assertThat(values).containsEntry(emp.salary, employee.getSalary());
    assertThat(values).containsEntry(emp.superiorId, employee.getSuperiorId());
    assertThat(values).containsEntry(emp.timefield, employee.getTimefield());
  }

  @Test
  public void extract2() {
    var values = DefaultMapper.DEFAULT.createMap(emp, new EmployeeX());
    assertThat(values).isEmpty();
  }

  @Test
  public void preservedColumnOrder() {
    final var columns = DefaultMapper.DEFAULT.getColumns(emp);
    final List<String> expectedKeySet =
        Arrays.asList(
            "id", "firstname", "lastname", "salary", "datefield", "timefield", "superiorId");
    assertThat(new ArrayList<>(columns.keySet())).isEqualTo(expectedKeySet);
  }
}
