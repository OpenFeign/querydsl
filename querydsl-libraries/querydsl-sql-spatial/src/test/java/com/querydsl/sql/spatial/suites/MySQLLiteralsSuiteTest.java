package com.querydsl.sql.spatial.suites;

import com.querydsl.core.testutil.MySQL;
import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.MySQLSpatialTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(MySQL.class)
public class MySQLLiteralsSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initMySQL();
    Connections.initConfiguration(MySQLSpatialTemplates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
