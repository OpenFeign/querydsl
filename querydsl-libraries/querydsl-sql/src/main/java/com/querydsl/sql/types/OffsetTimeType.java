package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetTime;
import org.jetbrains.annotations.Nullable;

public class OffsetTimeType extends AbstractJSR310DateTimeType<OffsetTime> {

  public OffsetTimeType() {
    super(Types.TIME_WITH_TIMEZONE);
  }

  public OffsetTimeType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(OffsetTime value) {
    return timeOffsetFormatter.format(value);
  }

  @Override
  public Class<OffsetTime> getReturnedClass() {
    return OffsetTime.class;
  }

  @Nullable
  @Override
  public OffsetTime getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, OffsetTime.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, OffsetTime value) throws SQLException {
    st.setObject(startIndex, value);
  }
}
