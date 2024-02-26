package com.querydsl.r2dbc.dml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.querydsl.core.types.Path;
import com.querydsl.r2dbc.domain.QEmployee;
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
    assertEquals(3, values.size());
    assertEquals(names._id, values.get(emp.id));
    assertEquals(names._firstname, values.get(emp.firstname));
    assertEquals(names._lastname, values.get(emp.lastname));
  }

  @Test
  public void extract_failure() {
    Map<Path<?>, Object> values = AnnotationMapper.DEFAULT.createMap(emp, employee);
    assertTrue(values.isEmpty());
  }

  @Test
  public void extract2() {
    Map<Path<?>, Object> values = AnnotationMapper.DEFAULT.createMap(emp, new EmployeeX());
    assertTrue(values.isEmpty());
  }
}
