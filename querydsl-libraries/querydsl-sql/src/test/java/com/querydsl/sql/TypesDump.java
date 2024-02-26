package com.querydsl.sql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public final class TypesDump {

  private TypesDump() {}

  public static void main(String[] args) throws Exception {
    Map<Integer, String> typeConstants = new HashMap<>();
    for (Field field : java.sql.Types.class.getDeclaredFields()) {
      if (field.getType().equals(Integer.TYPE)) {
        typeConstants.put(field.getInt(null), field.getName());
      }
    }

    Connections.initOracle();
    try {
      Connection c = Connections.getConnection();
      DatabaseMetaData m = c.getMetaData();
      System.out.println(m.getDatabaseProductName());
      try (ResultSet rs = m.getTypeInfo()) {
        while (rs.next()) {
          String name = rs.getString("TYPE_NAME");
          int jdbcType = rs.getInt("DATA_TYPE");
          String jdbcTypeField = typeConstants.get(jdbcType);
          if (jdbcTypeField == null || !jdbcTypeField.equalsIgnoreCase(name)) {
            String prefix = rs.getString("LITERAL_PREFIX");
            String suffix = rs.getString("LITERAL_SUFFIX");
            String jdbcTypeStr =
                jdbcTypeField != null ? ("Types." + jdbcTypeField) : String.valueOf(jdbcType);
            System.out.println(
                "addTypeNameToCode(\"" + name.toLowerCase() + "\", " + jdbcTypeStr + ");");
            // System.out.println(rs.getInt("PRECISION"));
          }
        }
      }
    } finally {
      Connections.close();
    }
  }
}
