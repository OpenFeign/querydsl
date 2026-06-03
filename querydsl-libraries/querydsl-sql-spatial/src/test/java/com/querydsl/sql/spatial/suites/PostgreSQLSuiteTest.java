package com.querydsl.sql.spatial.suites;

import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.PostGISTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.PostgreSQL")
public class PostgreSQLSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initPostgreSQL();
    Connections.initConfiguration(
        PostGISTemplates.builder().quote().newLineToSingleSpace().build());
  }
}
