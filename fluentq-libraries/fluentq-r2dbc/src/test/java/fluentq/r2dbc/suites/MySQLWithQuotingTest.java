package fluentq.r2dbc.suites;

import fluentq.r2dbc.BeanPopulationBase;
import fluentq.r2dbc.Connections;
import fluentq.r2dbc.DeleteBase;
import fluentq.r2dbc.InsertBase;
import fluentq.r2dbc.KeywordQuotingBase;
import fluentq.r2dbc.LikeEscapeBase;
import fluentq.r2dbc.MySQLTemplates;
import fluentq.r2dbc.SelectBase;
import fluentq.r2dbc.SubqueriesBase;
import fluentq.r2dbc.TypesBase;
import fluentq.r2dbc.UnionBase;
import fluentq.r2dbc.UpdateBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.MySQL")
public class MySQLWithQuotingTest extends AbstractSuite {

  @Nested
  class BeanPopulation extends BeanPopulationBase {}

  @Nested
  class Delete extends DeleteBase {}

  @Nested
  class Insert extends InsertBase {}

  @Nested
  class KeywordQuoting extends KeywordQuotingBase {}

  @Nested
  class LikeEscape extends LikeEscapeBase {}

  @Nested
  class Select extends SelectBase {}

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
    Connections.initConfiguration(MySQLTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.initMySQL();
  }
}
