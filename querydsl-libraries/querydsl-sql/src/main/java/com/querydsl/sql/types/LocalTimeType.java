package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalTime;
import org.jetbrains.annotations.Nullable;

public class LocalTimeType extends AbstractJSR310DateTimeType<LocalTime> {

  public LocalTimeType() {
    super(Types.TIME);
  }

  public LocalTimeType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(LocalTime value) {
    return timeFormatter.format(value);
  }

  @Override
  public Class<LocalTime> getReturnedClass() {
    return LocalTime.class;
  }

  @Nullable
  @Override
  public LocalTime getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, LocalTime.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, LocalTime value) throws SQLException {
    st.setObject(startIndex, value);
  }
}
