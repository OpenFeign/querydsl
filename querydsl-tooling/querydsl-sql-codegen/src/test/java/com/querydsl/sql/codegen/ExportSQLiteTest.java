package com.querydsl.sql.codegen;

import com.querydsl.core.testutil.SQLite;
import com.querydsl.sql.Connections;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(SQLite.class)
public class ExportSQLiteTest extends ExportBaseTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    Connections.initSQLite();
  }
}
