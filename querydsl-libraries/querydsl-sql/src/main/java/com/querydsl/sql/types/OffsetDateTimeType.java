package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.Nullable;

public class OffsetDateTimeType extends AbstractJSR310DateTimeType<OffsetDateTime> {

  public OffsetDateTimeType() {
    super(Types.TIMESTAMP_WITH_TIMEZONE);
  }

  public OffsetDateTimeType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(OffsetDateTime value) {
    return dateTimeOffsetFormatter.format(value);
  }

  @Override
  public Class<OffsetDateTime> getReturnedClass() {
    return OffsetDateTime.class;
  }

  @Nullable
  @Override
  public OffsetDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, OffsetDateTime.class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, OffsetDateTime value)
      throws SQLException {
    st.setObject(startIndex, value);
  }
}
