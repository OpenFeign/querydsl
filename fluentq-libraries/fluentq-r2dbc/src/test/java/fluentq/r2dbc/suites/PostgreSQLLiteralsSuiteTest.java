package fluentq.r2dbc.suites;

import fluentq.core.testutil.PostgreSQL;
import fluentq.r2dbc.BeanPopulationBase;
import fluentq.r2dbc.Configuration;
import fluentq.r2dbc.Connections;
import fluentq.r2dbc.DeleteBase;
import fluentq.r2dbc.InsertBase;
import fluentq.r2dbc.KeywordQuotingBase;
import fluentq.r2dbc.LikeEscapeBase;
import fluentq.r2dbc.PostgreSQLTemplates;
import fluentq.r2dbc.SelectBase;
import fluentq.r2dbc.SelectWindowFunctionsBase;
import fluentq.r2dbc.SubqueriesBase;
import fluentq.r2dbc.TypesBase;
import fluentq.r2dbc.UnionBase;
import fluentq.r2dbc.UpdateBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

// TODO r2dbc-postgres drops some inserts, readd after fix
@Category(PostgreSQL.class)
public abstract class PostgreSQLLiteralsSuiteTest extends AbstractSuite {

  public static class BeanPopulation extends BeanPopulationBase {}

  public static class Delete extends DeleteBase {}

  public static class Insert extends InsertBase {}

  public static class KeywordQuoting extends KeywordQuotingBase {

    private Configuration previous;

    @Override
    public void setUp() throws Exception {
      // NOTE: replacing the templates with a non-quoting one
      previous = configuration;
      configuration =
          new Configuration(PostgreSQLTemplates.builder().newLineToSingleSpace().build());
      super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
      super.tearDown();
      // NOTE: restoring old templates
      configuration = previous;
    }
  }

  public static class LikeEscape extends LikeEscapeBase {}

  public static class Select extends SelectBase {}

  public static class SelectWindowFunctions extends SelectWindowFunctionsBase {}

  public static class Subqueries extends SubqueriesBase {}

  public static class Types extends TypesBase {}

  public static class Union extends UnionBase {}

  public static class Update extends UpdateBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initConfiguration(
        PostgreSQLTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
    Connections.initPostgreSQL();
  }
}
