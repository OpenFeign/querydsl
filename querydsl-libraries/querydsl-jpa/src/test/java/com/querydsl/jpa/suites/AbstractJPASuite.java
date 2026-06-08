package com.querydsl.jpa.suites;

import com.querydsl.jpa.Mode;
import org.junit.jupiter.api.AfterAll;

public abstract class AbstractJPASuite {

  @AfterAll
  public static void tearDownClass() throws Exception {
    Mode.mode.remove();
    Mode.target.remove();
  }
}
