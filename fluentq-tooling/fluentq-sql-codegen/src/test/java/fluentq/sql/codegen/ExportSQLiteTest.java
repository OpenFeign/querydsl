package fluentq.sql.codegen;

import fluentq.sql.Connections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.SQLite")
public class ExportSQLiteTest extends ExportBaseTest {

  @BeforeAll
  public static void setUpClass() throws Exception {
    Connections.initSQLite();
  }
}
