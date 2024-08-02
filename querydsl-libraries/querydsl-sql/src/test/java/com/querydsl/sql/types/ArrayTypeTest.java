package com.querydsl.sql.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.easymock.EasyMock;
import org.junit.Test;

public class ArrayTypeTest {

  @Test
  public void set_object_array() throws SQLException {
    var value = new Integer[] {1, 2, 3};
    var type = new ArrayType<Integer[]>(Integer[].class, "INTEGER ARRAY");

    var arr = (Array) EasyMock.createMock(Array.class);
    var conn = (Connection) EasyMock.createMock(Connection.class);
    var stmt = (PreparedStatement) EasyMock.createMock(PreparedStatement.class);
    EasyMock.expect(stmt.getConnection()).andStubReturn(conn);
    EasyMock.expect(conn.createArrayOf("INTEGER ARRAY", value)).andReturn(arr);
    stmt.setArray(1, arr);
    EasyMock.replay(arr, conn, stmt);

    type.setValue(stmt, 1, value);

    EasyMock.verify(arr, conn, stmt);
  }

  @Test
  public void set_primitive_array() throws SQLException {
    var value = new int[] {1, 2, 3};
    var boxedValue = new Integer[] {1, 2, 3};
    var type = new ArrayType<int[]>(int[].class, "INTEGER ARRAY");

    var arr = (Array) EasyMock.createMock(Array.class);
    var conn = (Connection) EasyMock.createMock(Connection.class);
    var stmt = (PreparedStatement) EasyMock.createMock(PreparedStatement.class);
    EasyMock.expect(stmt.getConnection()).andStubReturn(conn);
    EasyMock.expect(conn.createArrayOf("INTEGER ARRAY", boxedValue)).andReturn(arr);
    stmt.setArray(1, arr);
    EasyMock.replay(arr, conn, stmt);

    type.setValue(stmt, 1, value);

    EasyMock.verify(arr, conn, stmt);
  }

  @Test
  public void get_typed_object_array() throws SQLException {
    var value = new Integer[] {1, 2, 3};
    var type = new ArrayType<Integer[]>(Integer[].class, "INTEGER ARRAY");

    var rs = (ResultSet) EasyMock.createMock(ResultSet.class);
    var arr = (Array) EasyMock.createMock(Array.class);
    EasyMock.expect(rs.getArray(1)).andReturn(arr);
    EasyMock.expect(arr.getArray()).andReturn(value);
    EasyMock.replay(rs, arr);

    Integer[] result = type.getValue(rs, 1);
    assertThat(result).isSameAs(value);

    EasyMock.verify(rs, arr);
  }

  @Test
  public void get_generic_object_array() throws SQLException {
    var value = new Integer[] {1, 2, 3};
    var type = new ArrayType<Integer[]>(Integer[].class, "INTEGER ARRAY");

    var rs = (ResultSet) EasyMock.createMock(ResultSet.class);
    var arr = (Array) EasyMock.createMock(Array.class);
    EasyMock.expect(rs.getArray(1)).andReturn(arr);
    EasyMock.expect(arr.getArray()).andReturn(value);
    EasyMock.replay(rs, arr);

    Integer[] result = type.getValue(rs, 1);
    assertThat(result).containsExactly(value);

    EasyMock.verify(rs, arr);
  }

  @Test
  public void get_primitive_array() throws SQLException {
    var value = new int[] {1, 2, 3};
    var boxedValue = new Integer[] {1, 2, 3};
    var type = new ArrayType<Integer[]>(Integer[].class, "INTEGER ARRAY");

    var rs = (ResultSet) EasyMock.createMock(ResultSet.class);
    var arr = (Array) EasyMock.createMock(Array.class);
    EasyMock.expect(rs.getArray(1)).andReturn(arr);
    EasyMock.expect(arr.getArray()).andReturn(value);
    EasyMock.replay(rs, arr);

    Integer[] result = type.getValue(rs, 1);
    assertThat(result).containsExactly(boxedValue);

    EasyMock.verify(rs, arr);
  }
}
