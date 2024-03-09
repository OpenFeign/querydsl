package com.querydsl.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Path;
import com.querydsl.sql.domain.QEmployee;
import java.util.Map;
import org.junit.Test;

public class AnnotationMapperTest extends AbstractMapperTest {

  private static final QEmployee emp = QEmployee.employee;

  @Test
  public void extract_success() {
    EmployeeNames names = new EmployeeNames();
    names._id = 9;
    names._firstname = "A";
    names._lastname = "B";

    Map<Path<?>, Object> values = AnnotationMapper.DEFAULT.createMap(emp, names);
    assertThat(values).hasSize(3);
    assertThat(values).containsEntry(emp.id, names._id);
    assertThat(values).containsEntry(emp.firstname, names._firstname);
    assertThat(values).containsEntry(emp.lastname, names._lastname);
  }

  @Test
  public void extract_failure() {
    Map<Path<?>, Object> values = AnnotationMapper.DEFAULT.createMap(emp, employee);
    assertThat(values).isEmpty();
  }

  @Test
  public void extract2() {
    Map<Path<?>, Object> values = AnnotationMapper.DEFAULT.createMap(emp, new EmployeeX());
    assertThat(values).isEmpty();
  }
}
