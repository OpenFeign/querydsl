package com.querydsl.sql.suites;

import com.querydsl.core.testutil.DB2;
import com.querydsl.sql.BeanPopulationBase;
import com.querydsl.sql.Connections;
import com.querydsl.sql.DB2Templates;
import com.querydsl.sql.DeleteBase;
import com.querydsl.sql.InsertBase;
import com.querydsl.sql.KeywordQuotingBase;
import com.querydsl.sql.LikeEscapeBase;
import com.querydsl.sql.MergeBase;
import com.querydsl.sql.MergeUsingBase;
import com.querydsl.sql.SelectBase;
import com.querydsl.sql.SelectWindowFunctionsBase;
import com.querydsl.sql.SubqueriesBase;
import com.querydsl.sql.TypesBase;
import com.querydsl.sql.UnionBase;
import com.querydsl.sql.UpdateBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(DB2.class)
public class DB2LiteralsSuiteTest extends AbstractSuite {

  public static class BeanPopulation extends BeanPopulationBase {}

  public static class Delete extends DeleteBase {}

  public static class Insert extends InsertBase {}

  public static class KeywordQuoting extends KeywordQuotingBase {}

  public static class LikeEscape extends LikeEscapeBase {}

  public static class Merge extends MergeBase {}

  public static class MergeUsing extends MergeUsingBase {}

  public static class Select extends SelectBase {}

  public static class SelectWindowFunctions extends SelectWindowFunctionsBase {}

  public static class Subqueries extends SubqueriesBase {}

  public static class Types extends TypesBase {}

  public static class Union extends UnionBase {}

  public static class Update extends UpdateBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initDB2();
    Connections.initConfiguration(DB2Templates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
