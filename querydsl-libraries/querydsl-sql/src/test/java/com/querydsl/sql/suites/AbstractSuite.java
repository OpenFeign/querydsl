package com.querydsl.sql.suites;

import com.querydsl.sql.Connections;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public abstract class AbstractSuite {

  @AfterClass
  public static void tearDown() throws SQLException {
    Connections.close();
  }
}
