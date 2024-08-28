package com.querydsl.sql;

import static com.querydsl.core.Target.CUBRID;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.TERADATA;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.ddl.CreateTableClause;
import com.querydsl.sql.ddl.DropTableClause;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public abstract class TypesBase extends AbstractBaseTest {

  @Test
  public void create_tables() {
    Map<Class<?>, Object> instances = new LinkedHashMap<>();
    instances.put(BigInteger.class, BigInteger.valueOf(1));
    instances.put(Long.class, 1L);
    instances.put(Integer.class, 1);
    instances.put(Short.class, (short) 1);
    instances.put(Byte.class, (byte) 1);
    instances.put(BigDecimal.class, BigDecimal.valueOf(1.0));
    instances.put(Double.class, 1.0);
    instances.put(Float.class, 1.0f);
    instances.put(Boolean.class, Boolean.TRUE);
    instances.put(Character.class, 'a');
    instances.put(String.class, "ABC");

    for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
      var tableName = "test_" + entry.getKey().getSimpleName();
      new DropTableClause(connection, configuration, tableName).execute();
      var c =
          new CreateTableClause(connection, configuration, tableName).column("col", entry.getKey());
      if (entry.getKey().equals(String.class)) {
        c.size(256);
      }
      c.execute();
      RelationalPath<Object> entityPath =
          new RelationalPathBase<>(Object.class, tableName, "PUBLIC", tableName);
      Path<?> columnPath = Expressions.path(entry.getKey(), entityPath, "col");
      insert(entityPath).set((Path) columnPath, entry.getValue()).execute();
      new DropTableClause(connection, configuration, tableName).execute();
    }
  }

  @Test
  @ExcludeIn({CUBRID, POSTGRESQL, TERADATA})
  public void dump_types() throws SQLException {
    var conn = Connections.getConnection();
    var md = conn.getMetaData();

    // types
    try (var rs = md.getUDTs(null, null, null, null)) {
      while (rs.next()) {
        // cat, schema, name, classname, datatype, remarks, base_type
        var cat = rs.getString(1);
        var schema = rs.getString(2);
        var name = rs.getString(3);
        var classname = rs.getString(4);
        var datatype = rs.getString(5);
        var remarks = rs.getString(6);
        var baseType = rs.getString(7);
        System.out.println(
            name + " " + classname + " " + datatype + " " + remarks + " " + baseType);

        // attributes
        try (var rs2 = md.getAttributes(cat, schema, name, null)) {
          while (rs2.next()) {
            // cat, schema, name, attr_name, data_type, attr_type_name, attr_size
            // decimal_digits, num_prec_radix, nullable, remarks, attr_def, sql_data_type,
            // ordinal_position
            // ...
            var cat2 = rs2.getString(1);
            var schema2 = rs2.getString(2);
            var name2 = rs2.getString(3);
            var attrName2 = rs2.getString(4);
            var dataType2 = rs2.getString(5);
            var attrTypeName2 = rs2.getString(6);
            var attrSize2 = rs2.getString(7);

            System.out.println(
                " " + attrName2 + " " + dataType2 + " " + attrTypeName2 + " " + attrSize2);
          }
        }
      }
    }
  }
}
