package com.querydsl.sql.spatial.suites;

import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.H2GISTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.H2")
public class H2SuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initH2();
    Connections.initConfiguration(H2GISTemplates.builder().newLineToSingleSpace().build());
  }
}
