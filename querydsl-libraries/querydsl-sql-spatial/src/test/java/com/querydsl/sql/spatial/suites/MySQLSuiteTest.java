package com.querydsl.sql.spatial.suites;

import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.MySQLSpatialTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.MySQL")
public class MySQLSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initMySQL();
    Connections.initConfiguration(MySQLSpatialTemplates.builder().newLineToSingleSpace().build());
  }
}
