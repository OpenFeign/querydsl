package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.jpa.JPABase;
import com.querydsl.jpa.JPAIntegrationBase;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import com.querydsl.jpa.SerializationBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.MySQL")
public class MySQLEclipseLinkTest extends AbstractJPASuite {

  @Nested
  class JPA extends JPABase {
    @Override
    public void cast() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void enum_startsWith() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_to_integer() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_toLong() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_toBigInteger() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_nullsFirst() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_nullsLast() {
      // not supported in MySQL/EclipseLink
    }
  }

  @Nested
  class JPASQL extends JPASQLBase {}

  @Nested
  class JPAIntegration extends JPAIntegrationBase {}

  @Nested
  class Serialization extends SerializationBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Mode.mode.set("mysql-eclipselink");
    Mode.target.set(Target.MYSQL);
  }
}
