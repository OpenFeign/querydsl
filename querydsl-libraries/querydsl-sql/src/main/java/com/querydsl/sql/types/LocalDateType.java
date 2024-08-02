package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import org.jetbrains.annotations.Nullable;

public class LocalDateType extends AbstractJSR310DateTimeType<LocalDate> {

  public LocalDateType() {
    super(Types.DATE);
  }

  public LocalDateType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(LocalDate value) {
    return dateFormatter.format(value);
  }

  @Override
  public Class<LocalDate> getReturnedClass() {
    return LocalDate.class;
  }

  @Nullable
  @Override
  public LocalDate getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, LocalDate.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, LocalDate value) throws SQLException {
    st.setObject(startIndex, value);
  }
}
