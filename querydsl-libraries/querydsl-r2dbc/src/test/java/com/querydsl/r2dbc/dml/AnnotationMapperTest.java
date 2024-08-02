package com.querydsl.r2dbc.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.domain.QEmployee;
import org.junit.Test;

public class AnnotationMapperTest extends AbstractMapperTest {

  private static final QEmployee emp = QEmployee.employee;

  @Test
  public void extract_success() {
    var names = new EmployeeNames();
    names._id = 9;
    names._firstname = "A";
    names._lastname = "B";

    var values = AnnotationMapper.DEFAULT.createMap(emp, names);
    assertThat(values).hasSize(3);
    assertThat(values).containsEntry(emp.id, names._id);
    assertThat(values).containsEntry(emp.firstname, names._firstname);
    assertThat(values).containsEntry(emp.lastname, names._lastname);
  }

  @Test
  public void extract_failure() {
    var values = AnnotationMapper.DEFAULT.createMap(emp, employee);
    assertThat(values).isEmpty();
  }

  @Test
  public void extract2() {
    var values = AnnotationMapper.DEFAULT.createMap(emp, new EmployeeX());
    assertThat(values).isEmpty();
  }
}
