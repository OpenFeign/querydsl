package com.querydsl.r2dbc.suites;

import com.querydsl.core.testutil.MySQL;
import com.querydsl.r2dbc.BeanPopulationBase;
import com.querydsl.r2dbc.Connections;
import com.querydsl.r2dbc.DeleteBase;
import com.querydsl.r2dbc.InsertBase;
import com.querydsl.r2dbc.KeywordQuotingBase;
import com.querydsl.r2dbc.LikeEscapeBase;
import com.querydsl.r2dbc.MySQLTemplates;
import com.querydsl.r2dbc.SelectBase;
import com.querydsl.r2dbc.SubqueriesBase;
import com.querydsl.r2dbc.TypesBase;
import com.querydsl.r2dbc.UnionBase;
import com.querydsl.r2dbc.UpdateBase;
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
