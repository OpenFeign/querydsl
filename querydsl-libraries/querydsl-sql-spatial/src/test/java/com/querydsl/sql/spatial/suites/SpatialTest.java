package com.querydsl.sql.spatial.suites;

import com.querydsl.core.testutil.H2;
import com.querydsl.sql.Connections;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(H2.class)
public class SpatialTest {

  @Before
  public void setUp() throws ClassNotFoundException, SQLException {
    Connections.initH2();
    //      Connections.initMySQL();
    //      Connections.initPostgreSQL();
    //      Connections.initTeradata();
  }

  @After
  public void tearDown() throws SQLException {
    Connections.close();
  }

  @Test
  public void test() throws SQLException {
    var stmt = Connections.getStatement();
    try (var rs = stmt.executeQuery("select \"GEOMETRY\" from \"SHAPES\"")) {
      while (rs.next()) {
        System.err.println(rs.getObject(1).getClass().getName());
        System.err.println(rs.getString(1));
        //                Clob clob = rs.getClob(1);
        //                System.err.println(clob.getSubString(1, (int) clob.length()));
      }
    }
  }

  @Test
  public void metadata() throws SQLException {
    var conn = Connections.getConnection();
    var md = conn.getMetaData();
    try (var rs = md.getColumns(null, null, "SHAPES", "GEOMETRY")) {
      rs.next();
      var type = rs.getInt("DATA_TYPE");
      var typeName = rs.getString("TYPE_NAME");
      System.err.println(type + " " + typeName);
    }
  }
}
