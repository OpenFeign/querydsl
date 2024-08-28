package io.github.openfeign.querydsl.sql.json.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.querydsl.sql.types.Type;
import io.github.openfeign.querydsl.sql.json.JsonEntity;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class JSONType implements Type<JsonEntity> {
  private final ObjectMapper objectMapper;

  public JSONType(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public JSONType() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  }

  @Override
  public int[] getSQLTypes() {
    return new int[] {Types.BLOB, Types.VARBINARY, Types.VARCHAR, Types.BINARY};
  }

  @Override
  public Class<JsonEntity> getReturnedClass() {
    return JsonEntity.class;
  }

  @Override
  public String getLiteral(JsonEntity value) {
    if (value == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonEntity getValue(ResultSet rs, int startIndex) throws SQLException {
    try {
      return objectMapper.readValue(rs.getBytes(startIndex), JsonEntity.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonEntity getValue(ResultSet rs, int startIndex, Class<JsonEntity> clazz)
      throws SQLException {
    try {
      var value = rs.getBytes(startIndex);
      if (value == null) {
        return null;
      }
      return objectMapper.readValue(value, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, JsonEntity value) throws SQLException {
    st.setObject(startIndex, getLiteral(value));
  }
}
