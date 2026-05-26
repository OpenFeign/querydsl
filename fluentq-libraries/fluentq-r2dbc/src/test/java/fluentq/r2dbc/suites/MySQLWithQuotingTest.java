package fluentq.r2dbc.suites;

import fluentq.core.testutil.MySQL;
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
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(MySQL.class)
public class MySQLWithQuotingTest extends AbstractSuite {

  public static class BeanPopulation extends BeanPopulationBase {}

  public static class Delete extends DeleteBase {}

  public static class Insert extends InsertBase {}

  public static class KeywordQuoting extends KeywordQuotingBase {}

  public static class LikeEscape extends LikeEscapeBase {}

  public static class Select extends SelectBase {}

  public static class Subqueries extends SubqueriesBase {}

  public static class Types extends TypesBase {}

  public static class Union extends UnionBase {}

  public static class Update extends UpdateBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initConfiguration(MySQLTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.initMySQL();
  }
}
