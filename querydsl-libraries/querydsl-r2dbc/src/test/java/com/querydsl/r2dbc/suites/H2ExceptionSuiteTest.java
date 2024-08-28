package com.querydsl.r2dbc.suites;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Throwables;
import com.querydsl.core.testutil.H2;
import com.querydsl.r2dbc.AbstractBaseTest;
import com.querydsl.r2dbc.Connections;
import com.querydsl.r2dbc.H2Templates;
import com.querydsl.sql.DefaultSQLExceptionTranslator;
import com.querydsl.sql.SQLExceptionTranslator;
import java.sql.SQLException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import reactor.core.publisher.Mono;

@Category(H2.class)
public class H2ExceptionSuiteTest extends AbstractBaseTest {

  private static final SQLExceptionTranslator exceptionTranslator =
      DefaultSQLExceptionTranslator.DEFAULT;

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initConfiguration(H2Templates.builder().build());
    Connections.initH2();

    Mono.just(
            Connections.getConnection()
                .createStatement("ALTER TABLE SURVEY ADD CONSTRAINT UNIQUE_ID UNIQUE(ID)")
                .execute())
        .block();
  }

  public static void tearDown() throws Exception {
    Mono.just(
            Connections.getConnection()
                .createStatement("ALTER TABLE SURVEY DROP CONSTRAINT UNIQUE_ID")
                .execute())
        .block();
  }

  @Test
  public void sQLExceptionCreationTranslated() {
    var e1 = new SQLException("Exception #1", "42001", 181);
    var e2 = new SQLException("Exception #2", "HY000", 1030);
    e1.setNextException(e2);
    var sqlException = new SQLException("Batch operation failed");
    sqlException.setNextException(e1);
    var result = exceptionTranslator.translate(sqlException);
    inspectExceptionResult(result);
  }

  private void inspectExceptionResult(Exception result) {
    var stackTraceAsString = Throwables.getStackTraceAsString(result);
    assertThat(stackTraceAsString).contains("Suppressed:");
  }
}
