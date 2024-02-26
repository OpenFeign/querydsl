package com.querydsl.r2dbc.types;

import java.util.Calendar;
import java.util.TimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalDateTimeTest {

  private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  //    private LocalDateTimeType type = new LocalDateTimeType();

  @BeforeClass
  public static void setUpClass() {
    UTC.setTimeInMillis(0);
  }

  @Test
  public void set() {
    //        LocalDateTime value = new LocalDateTime();
    //        DateTime dt = value.toDateTime(DateTimeZone.UTC);
    //        Timestamp ts = new Timestamp(dt.getMillis());
    //
    //        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
    //        stmt.setTimestamp(0, ts, UTC);
    //        EasyMock.replay(stmt);
    //
    //        type.setValue(stmt, 0, value);
    //        EasyMock.verify(stmt);
  }
}
