package fluentq.sql.suites;

import fluentq.sql.BeanPopulationBase;
import fluentq.sql.Connections;
import fluentq.sql.DB2Templates;
import fluentq.sql.DeleteBase;
import fluentq.sql.InsertBase;
import fluentq.sql.KeywordQuotingBase;
import fluentq.sql.LikeEscapeBase;
import fluentq.sql.MergeBase;
import fluentq.sql.MergeUsingBase;
import fluentq.sql.SelectBase;
import fluentq.sql.SelectWindowFunctionsBase;
import fluentq.sql.SubqueriesBase;
import fluentq.sql.TypesBase;
import fluentq.sql.UnionBase;
import fluentq.sql.UpdateBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.DB2")
public class DB2SuiteTest extends AbstractSuite {

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
    Connections.initDB2();
    Connections.initConfiguration(DB2Templates.builder().newLineToSingleSpace().build());
  }
}
