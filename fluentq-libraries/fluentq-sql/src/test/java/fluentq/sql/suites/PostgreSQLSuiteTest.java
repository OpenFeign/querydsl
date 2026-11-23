package fluentq.sql.suites;

import fluentq.sql.BeanPopulationBase;
import fluentq.sql.Configuration;
import fluentq.sql.Connections;
import fluentq.sql.DeleteBase;
import fluentq.sql.InsertBase;
import fluentq.sql.KeywordQuotingBase;
import fluentq.sql.LikeEscapeBase;
import fluentq.sql.MergeBase;
import fluentq.sql.MergeUsingBase;
import fluentq.sql.PostgreSQLTemplates;
import fluentq.sql.SelectBase;
import fluentq.sql.SelectWindowFunctionsBase;
import fluentq.sql.SubqueriesBase;
import fluentq.sql.TypesBase;
import fluentq.sql.UnionBase;
import fluentq.sql.UpdateBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.PostgreSQL")
public class PostgreSQLSuiteTest extends AbstractSuite {

  @Nested
  class BeanPopulation extends BeanPopulationBase {}

  @Nested
  class Delete extends DeleteBase {}

  @Nested
  class Insert extends InsertBase {}

  @Nested
  class KeywordQuoting extends KeywordQuotingBase {

    private Configuration previous;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
      // NOTE: replacing the templates with a non-quoting one
      previous = configuration;
      configuration =
          new Configuration(PostgreSQLTemplates.builder().newLineToSingleSpace().build());
      super.setUp();
    }

    @AfterEach
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
  class Merge extends MergeBase {}

  @Nested
  class MergeUsing extends MergeUsingBase {}

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
    Connections.initPostgreSQL();
    Connections.initConfiguration(
        PostgreSQLTemplates.builder().quote().newLineToSingleSpace().build());
  }
}
