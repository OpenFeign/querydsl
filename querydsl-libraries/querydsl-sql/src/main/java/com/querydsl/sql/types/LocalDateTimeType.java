package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import org.jetbrains.annotations.Nullable;

public class LocalDateTimeType extends AbstractJSR310DateTimeType<LocalDateTime> {

  public LocalDateTimeType() {
    super(Types.TIMESTAMP);
  }

  public LocalDateTimeType(int type) {
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
    try {
      return rs.getObject(startIndex, LocalDateTime.class);
    } catch (SQLException e) {
      var timestamp = rs.getTimestamp(startIndex);
      if (timestamp == null) {
        return null;
      }
      return timestamp.toLocalDateTime();
    }
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, LocalDateTime value)
      throws SQLException {
    st.setObject(startIndex, value);
  }
}
