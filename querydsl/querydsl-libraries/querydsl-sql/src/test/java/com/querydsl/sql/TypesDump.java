package com.querydsl.sql;

import java.lang.reflect.Field;
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
      var c = Connections.getConnection();
      var m = c.getMetaData();
      System.out.println(m.getDatabaseProductName());
      try (var rs = m.getTypeInfo()) {
        while (rs.next()) {
          var name = rs.getString("TYPE_NAME");
          var jdbcType = rs.getInt("DATA_TYPE");
          var jdbcTypeField = typeConstants.get(jdbcType);
          if (jdbcTypeField == null || !jdbcTypeField.equalsIgnoreCase(name)) {
            var prefix = rs.getString("LITERAL_PREFIX");
            var suffix = rs.getString("LITERAL_SUFFIX");
            var jdbcTypeStr =
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
