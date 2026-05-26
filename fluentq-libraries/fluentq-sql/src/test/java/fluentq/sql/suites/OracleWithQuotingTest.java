package fluentq.sql.suites;

import fluentq.core.testutil.Oracle;
import fluentq.sql.BeanPopulationBase;
import fluentq.sql.Connections;
import fluentq.sql.DeleteBase;
import fluentq.sql.InsertBase;
import fluentq.sql.KeywordQuotingBase;
import fluentq.sql.LikeEscapeBase;
import fluentq.sql.MergeBase;
import fluentq.sql.OracleTemplates;
import fluentq.sql.SelectBase;
import fluentq.sql.SubqueriesBase;
import fluentq.sql.TypesBase;
import fluentq.sql.UnionBase;
import fluentq.sql.UpdateBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(Oracle.class)
public class OracleWithQuotingTest extends AbstractSuite {

  public static class BeanPopulation extends BeanPopulationBase {}

  public static class Delete extends DeleteBase {}

  public static class Insert extends InsertBase {}

  public static class KeywordQuoting extends KeywordQuotingBase {}

  public static class LikeEscape extends LikeEscapeBase {}

  public static class Merge extends MergeBase {}

  public static class Select extends SelectBase {}

  public static class Subqueries extends SubqueriesBase {}

  public static class Types extends TypesBase {}

  public static class Union extends UnionBase {}

  public static class Update extends UpdateBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initOracle();
    Connections.initConfiguration(OracleTemplates.builder().quote().newLineToSingleSpace().build());
  }
}
