package fluentq.sql.spatial.suites;

import fluentq.sql.Connections;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.H2")
public class SpatialTest {

  @BeforeEach
  public void setUp() throws ClassNotFoundException, SQLException {
    Connections.initH2();
    //      Connections.initMySQL();
    //      Connections.initPostgreSQL();
    //      Connections.initTeradata();
  }

  @AfterEach
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
