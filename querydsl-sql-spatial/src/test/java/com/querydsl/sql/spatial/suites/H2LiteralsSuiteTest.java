package com.querydsl.sql.spatial.suites;

import com.querydsl.core.testutil.H2;
import com.querydsl.sql.Connections;
import com.querydsl.sql.spatial.H2GISTemplates;
import com.querydsl.sql.spatial.SpatialBase;
import com.querydsl.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(H2.class)
public class H2LiteralsSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initH2();
    Connections.initConfiguration(H2GISTemplates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
