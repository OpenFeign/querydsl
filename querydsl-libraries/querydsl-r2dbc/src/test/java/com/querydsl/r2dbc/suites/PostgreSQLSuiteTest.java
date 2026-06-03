package com.querydsl.r2dbc.suites;

import com.querydsl.r2dbc.BeanPopulationBase;
import com.querydsl.r2dbc.Configuration;
import com.querydsl.r2dbc.Connections;
import com.querydsl.r2dbc.DeleteBase;
import com.querydsl.r2dbc.InsertBase;
import com.querydsl.r2dbc.KeywordQuotingBase;
import com.querydsl.r2dbc.LikeEscapeBase;
import com.querydsl.r2dbc.PostgreSQLTemplates;
import com.querydsl.r2dbc.SelectBase;
import com.querydsl.r2dbc.SelectWindowFunctionsBase;
import com.querydsl.r2dbc.SubqueriesBase;
import com.querydsl.r2dbc.TypesBase;
import com.querydsl.r2dbc.UnionBase;
import com.querydsl.r2dbc.UpdateBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

// TODO r2dbc-postgres drops some inserts, readd after fix
@Tag("com.querydsl.core.testutil.PostgreSQL")
public abstract class PostgreSQLSuiteTest extends AbstractSuite {

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
    Connections.initPostgreSQL();
  }
}
