package com.querydsl.sql.suites;

import com.querydsl.core.testutil.SQLServer;
import com.querydsl.sql.*;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(SQLServer.class)
public class MSSQLSuiteTest extends AbstractSuite {

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
    Connections.initSQLServer();
    Connections.initConfiguration(SQLServer2008Templates.builder().newLineToSingleSpace().build());
  }
}
