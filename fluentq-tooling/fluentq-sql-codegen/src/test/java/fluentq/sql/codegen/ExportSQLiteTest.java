package fluentq.sql.codegen;

import fluentq.core.testutil.SQLite;
import fluentq.sql.Connections;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(SQLite.class)
public class ExportSQLiteTest extends ExportBaseTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    Connections.initSQLite();
  }
}
