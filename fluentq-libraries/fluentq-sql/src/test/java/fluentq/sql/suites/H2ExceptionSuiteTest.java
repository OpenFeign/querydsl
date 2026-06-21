package fluentq.sql.suites;

import static fluentq.sql.domain.QSurvey.survey;
import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.QueryException;
import fluentq.sql.AbstractBaseTest;
import fluentq.sql.Connections;
import fluentq.sql.DefaultSQLExceptionTranslator;
import fluentq.sql.H2Templates;
import fluentq.sql.SQLExceptionTranslator;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.H2")
public class H2ExceptionSuiteTest extends AbstractBaseTest {

  private static final SQLExceptionTranslator exceptionTranslator =
      DefaultSQLExceptionTranslator.DEFAULT;

  @BeforeAll
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
