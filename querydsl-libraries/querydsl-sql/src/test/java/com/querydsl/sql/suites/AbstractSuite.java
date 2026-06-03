package com.querydsl.sql.suites;

import com.querydsl.sql.Connections;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;

public abstract class AbstractSuite {

  @AfterAll
  public static void tearDown() throws SQLException {
    Connections.close();
  }
}
