package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;

public class InstantType extends AbstractJSR310DateTimeType<Instant> {

  // JDBC 4.2 does not define any support for Instant, unlike most other JSR-310 types
  // few drivers support it natively, so go through Timestamp to handle it

  public InstantType() {
    super(Types.TIMESTAMP);
  }

  public InstantType(int type) {
    super(type);
  }

  @Override
  public String getLiteral(Instant value) {
    return dateTimeFormatter.format(Timestamp.from(value).toLocalDateTime());
  }

  @Override
  public Class<Instant> getReturnedClass() {
    return Instant.class;
  }

  @Nullable
  @Override
  public Instant getValue(ResultSet rs, int startIndex) throws SQLException {
    var timestamp = rs.getTimestamp(startIndex);
    return timestamp != null ? timestamp.toInstant() : null;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, Instant value) throws SQLException {
    st.setTimestamp(startIndex, Timestamp.from(value));
  }
}
