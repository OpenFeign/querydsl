package com.querydsl.r2dbc.types;

import java.time.LocalTime;
import org.junit.Test;

public class JSR310LocalTimeTypeTest extends AbstractJSR310DateTimeTypeTest<LocalTime> {

  public JSR310LocalTimeTypeTest() {
    super(new JSR310LocalTimeType());
  }

  @Override
  @Test
  public void set() {
    //        LocalTime value = LocalTime.now();
    //        Time time = new Time(value.get(ChronoField.MILLI_OF_DAY));
    //
    //        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
    //        stmt.setTime(1, time, UTC);
    //        EasyMock.replay(stmt);
    //
    //        type.setValue(stmt, 1, value);
    //        EasyMock.verify(stmt);
  }

  @Override
  @Test
  public void get() {
    //        ResultSet resultSet = EasyMock.createNiceMock(ResultSet.class);
    //        EasyMock.expect(resultSet.getTime(1, UTC)).andReturn(new Time(UTC.getTimeInMillis()));
    //        EasyMock.replay(resultSet);
    //
    //        LocalTime result = type.getValue(resultSet, 1);
    //        EasyMock.verify(resultSet);
    //
    //        assertNotNull(result);
    //        assertTrue(result.getSecond() == 0);
  }
}
