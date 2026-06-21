package fluentq.r2dbc.suites;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

// TODO r2dbc-postgres drops some inserts, readd after fix
@Tag("fluentq.core.testutil.PostgreSQL")
public abstract class PostgreSQLLiteralsSuiteTest extends AbstractSuite {

  @Nested
  class BeanPopulation extends BeanPopulationBase {}

  @Nested
  class Delete extends DeleteBase {}

  @Nested
  class Insert extends InsertBase {}

  @Nested
  class KeywordQuoting extends KeywordQuotingBase {

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

  @Nested
  class LikeEscape extends LikeEscapeBase {}

  @Nested
  class Select extends SelectBase {}

  @Nested
  class SelectWindowFunctions extends SelectWindowFunctionsBase {}

  @Nested
  class Subqueries extends SubqueriesBase {}

  @Nested
  class Types extends TypesBase {}

  @Nested
  class Union extends UnionBase {}

  @Nested
  class Update extends UpdateBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initConfiguration(
        PostgreSQLTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
    Connections.initPostgreSQL();
  }
}
