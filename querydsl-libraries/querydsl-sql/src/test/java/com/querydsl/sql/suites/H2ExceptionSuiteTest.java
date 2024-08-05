package com.querydsl.sql.suites;

import static com.querydsl.sql.domain.QSurvey.survey;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryException;
import com.querydsl.core.testutil.H2;
import com.querydsl.sql.AbstractBaseTest;
import com.querydsl.sql.Connections;
import com.querydsl.sql.DefaultSQLExceptionTranslator;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLExceptionTranslator;
import java.sql.SQLException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(H2.class)
public class H2ExceptionSuiteTest extends AbstractBaseTest {

  private static final SQLExceptionTranslator exceptionTranslator =
      DefaultSQLExceptionTranslator.DEFAULT;

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initH2();
    Connections.initConfiguration(H2Templates.builder().build());

    Connections.getConnection()
        .createStatement()
        .execute("ALTER TABLE SURVEY ADD CONSTRAINT UNIQUE_ID UNIQUE(ID)");
  }

  public static void tearDown() throws Exception {
    Connections.getConnection()
        .createStatement()
        .execute("ALTER TABLE SURVEY DROP CONSTRAINT UNIQUE_ID");
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

  @Test
  public void updateBatchFailed() {
    execute(insert(survey).columns(survey.name, survey.name2).values("New Survey", "New Survey"));
    Exception result = null;
    try {
      execute(update(survey).set(survey.id, 1).addBatch().set(survey.id, 2).addBatch());
    } catch (QueryException e) {
      result = e;
    }
    assertThat(result).isNotNull();
    inspectExceptionResult(result);
  }

  private void inspectExceptionResult(Exception result) {
    assertThat(result.getSuppressed()).isNotEmpty();
  }
}
