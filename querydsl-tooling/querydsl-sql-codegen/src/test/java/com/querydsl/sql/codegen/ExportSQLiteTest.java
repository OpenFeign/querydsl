package com.querydsl.sql.codegen;

import com.querydsl.sql.Connections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.SQLite")
public class ExportSQLiteTest extends ExportBaseTest {

  @BeforeAll
  public static void setUpClass() throws Exception {
    Connections.initSQLite();
  }
}
