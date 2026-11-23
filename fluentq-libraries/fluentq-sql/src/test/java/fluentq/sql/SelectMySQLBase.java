package fluentq.sql;

import static fluentq.core.Target.MYSQL;
import static fluentq.sql.Constants.survey;

import fluentq.core.testutil.IncludeIn;
import fluentq.sql.mysql.MySQLQuery;
import org.junit.jupiter.api.Test;

public abstract class SelectMySQLBase extends AbstractBaseTest {

  protected MySQLQuery<?> mysqlQuery() {
    return new MySQLQuery<Void>(connection, configuration);
  }

  @Test
  @IncludeIn(MYSQL)
  public void mysql_extensions() {
    mysqlQuery().from(survey).bigResult().select(survey.id).fetch();
    mysqlQuery().from(survey).bufferResult().select(survey.id).fetch();
    mysqlQuery().from(survey).cache().select(survey.id).fetch();
    mysqlQuery().from(survey).calcFoundRows().select(survey.id).fetch();
    mysqlQuery().from(survey).noCache().select(survey.id).fetch();

    mysqlQuery().from(survey).highPriority().select(survey.id).fetch();
    mysqlQuery().from(survey).lockInShareMode().select(survey.id).fetch();
    mysqlQuery().from(survey).smallResult().select(survey.id).fetch();
    mysqlQuery().from(survey).straightJoin().select(survey.id).fetch();
  }
}
