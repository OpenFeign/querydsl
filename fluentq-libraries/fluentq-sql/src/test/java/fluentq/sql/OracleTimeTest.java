package fluentq.sql;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class OracleTimeTest {

  @AfterEach
  public void tearDown() throws SQLException {
    Connections.close();
  }

  @Test
  public void test() throws ClassNotFoundException, SQLException {
    Connections.initOracle();
    var conn = Connections.getConnection();
    var stmt = conn.createStatement();
    var rs = stmt.executeQuery("select timefield from employee");
    rs.next();
    var o = rs.getObject(1);
    System.err.println(o.getClass().getName() + ": " + o);
    o = rs.getTimestamp(1);
    System.err.println(o.getClass().getName() + ": " + o);
    o = rs.getTime(1);
    System.err.println(o.getClass().getName() + ": " + o);
    rs.close();
    stmt.close();
  }
}
