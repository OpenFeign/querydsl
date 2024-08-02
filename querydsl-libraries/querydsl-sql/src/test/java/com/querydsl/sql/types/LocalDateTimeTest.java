package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.easymock.EasyMock;
import org.junit.Test;

public class LocalDateTimeTest {

  private LocalDateTimeType type = new LocalDateTimeType();

  @Test
  public void set() throws SQLException {
    var value = LocalDateTime.now();

    var stmt = (PreparedStatement) EasyMock.createNiceMock(PreparedStatement.class);
    stmt.setObject(0, value);
    EasyMock.replay(stmt);

    type.setValue(stmt, 0, value);
    EasyMock.verify(stmt);
  }
}
