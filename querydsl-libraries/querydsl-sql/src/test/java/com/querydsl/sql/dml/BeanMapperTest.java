package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Path;
import com.querydsl.sql.domain.QEmployee;
import java.util.Map;
import org.junit.Test;

public class BeanMapperTest extends AbstractMapperTest {

  private static final QEmployee emp = QEmployee.employee;

  @Test
  public void extract() {
    Map<Path<?>, Object> values = BeanMapper.DEFAULT.createMap(emp, employee);
    assertThat(values).containsEntry(emp.datefield, employee.getDatefield());
    assertThat(values).containsEntry(emp.firstname, employee.getFirstname());
    assertThat(values).containsEntry(emp.lastname, employee.getLastname());
    assertThat(values).containsEntry(emp.salary, employee.getSalary());
    assertThat(values).containsEntry(emp.superiorId, employee.getSuperiorId());
    assertThat(values).containsEntry(emp.timefield, employee.getTimefield());
  }

  @Test
  public void extract2() {
    Map<Path<?>, Object> values = BeanMapper.DEFAULT.createMap(emp, new EmployeeX());
    assertThat(values).isEmpty();
  }
}
