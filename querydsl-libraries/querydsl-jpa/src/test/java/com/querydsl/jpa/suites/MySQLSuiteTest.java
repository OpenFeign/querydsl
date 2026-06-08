package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.jpa.HibernateBase;
import com.querydsl.jpa.HibernateSQLBase;
import com.querydsl.jpa.JPABase;
import com.querydsl.jpa.JPAIntegrationBase;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import com.querydsl.jpa.SerializationBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.MySQL")
public class MySQLSuiteTest extends AbstractSuite {

  @Nested
  class JPA extends JPABase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  @Nested
  class JPASQL extends JPASQLBase {}

  @Nested
  class JPAIntegration extends JPAIntegrationBase {}

  @Nested
  class Serialization extends SerializationBase {}

  @Nested
  class Hibernate extends HibernateBase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  @Nested
  class HibernateSQL extends HibernateSQLBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Mode.mode.set("mysql");
    Mode.target.set(Target.MYSQL);
  }
}
