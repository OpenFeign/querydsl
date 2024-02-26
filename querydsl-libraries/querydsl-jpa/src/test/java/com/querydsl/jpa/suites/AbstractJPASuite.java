package com.querydsl.jpa.suites;

import com.querydsl.jpa.Mode;
import org.junit.AfterClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public abstract class AbstractJPASuite {

  @AfterClass
  public static void tearDownClass() throws Exception {
    Mode.mode.remove();
    Mode.target.remove();
  }
}
