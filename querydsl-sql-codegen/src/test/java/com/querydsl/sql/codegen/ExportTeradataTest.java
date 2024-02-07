package com.querydsl.sql.codegen;

import com.querydsl.core.testutil.Teradata;
import com.querydsl.sql.Connections;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(Teradata.class)
public class ExportTeradataTest extends ExportBaseTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    Connections.initTeradata();
  }
}
