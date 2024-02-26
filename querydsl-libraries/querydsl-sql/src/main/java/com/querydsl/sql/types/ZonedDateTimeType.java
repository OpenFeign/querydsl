package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZonedDateTime;
import org.jetbrains.annotations.Nullable;

public class ZonedDateTimeType extends AbstractJSR310DateTimeType<ZonedDateTime> {

  public ZonedDateTimeType() {
    super(Types.TIMESTAMP_WITH_TIMEZONE);
  }

  public ZonedDateTimeType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(ZonedDateTime value) {
    return dateTimeZoneFormatter.format(value);
  }

  @Override
  public Class<ZonedDateTime> getReturnedClass() {
    return ZonedDateTime.class;
  }

  @Nullable
  @Override
  public ZonedDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, ZonedDateTime.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, ZonedDateTime value)
      throws SQLException {
    st.setObject(startIndex, value);
  }
}
