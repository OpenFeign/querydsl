package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import org.jetbrains.annotations.Nullable;

public class JSR310LocalDateTimeType extends AbstractJSR310DateTimeType<LocalDateTime> {

  public JSR310LocalDateTimeType() {
    super(Types.TIMESTAMP);
  }

  public JSR310LocalDateTimeType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(LocalDateTime value) {
    return dateTimeFormatter.format(value);
  }

  @Override
  public Class<LocalDateTime> getReturnedClass() {
    return LocalDateTime.class;
  }

  @Nullable
  @Override
  public LocalDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, LocalDateTime.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, LocalDateTime value)
      throws SQLException {
    st.setObject(startIndex, value);
  }
}
