package com.querydsl.sql.codegen;

import com.querydsl.sql.Connections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

// Turso 0.6.0 JDBC DatabaseMetaData.getPrimaryKeys() returns null, so schema export codegen
// cannot read the database metadata. Re-enable once the driver implements it. See #1812.
@Disabled("Turso 0.6.0 JDBC metadata (getPrimaryKeys) incomplete, see #1812")
@Tag("com.querydsl.core.testutil.Turso")
public class ExportTursoTest extends ExportBaseTest {

  @BeforeAll
  public static void setUpClass() throws Exception {
    Connections.initTurso();
  }
}
