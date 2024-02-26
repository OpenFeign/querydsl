package com.querydsl.sql.spatial.suites;

import com.querydsl.core.testutil.PostgreSQL;
import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.PostGISTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initPostgreSQL();
    Connections.initConfiguration(
        PostGISTemplates.builder().quote().newLineToSingleSpace().build());
  }
}
