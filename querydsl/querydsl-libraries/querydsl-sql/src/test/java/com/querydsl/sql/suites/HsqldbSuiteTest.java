package com.querydsl.sql.suites;

import com.querydsl.core.testutil.HSQLDB;
import com.querydsl.sql.BeanPopulationBase;
import com.querydsl.sql.Connections;
import com.querydsl.sql.DeleteBase;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.InsertBase;
import com.querydsl.sql.KeywordQuotingBase;
import com.querydsl.sql.LikeEscapeBase;
import com.querydsl.sql.MergeBase;
import com.querydsl.sql.SelectBase;
import com.querydsl.sql.SubqueriesBase;
import com.querydsl.sql.TypesBase;
import com.querydsl.sql.UnionBase;
import com.querydsl.sql.UpdateBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(HSQLDB.class)
public class HsqldbSuiteTest extends AbstractSuite {

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
    Connections.initHSQL();
    Connections.initConfiguration(HSQLDBTemplates.builder().newLineToSingleSpace().build());
  }
}
