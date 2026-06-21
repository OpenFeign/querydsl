package com.querydsl.sql.suites;

import com.querydsl.sql.BeanPopulationBase;
import com.querydsl.sql.Connections;
import com.querydsl.sql.DeleteBase;
import com.querydsl.sql.InsertBase;
import com.querydsl.sql.KeywordQuotingBase;
import com.querydsl.sql.LikeEscapeBase;
import com.querydsl.sql.MergeBase;
import com.querydsl.sql.SelectBase;
import com.querydsl.sql.SubqueriesBase;
import com.querydsl.sql.TursoTemplates;
import com.querydsl.sql.TypesBase;
import com.querydsl.sql.UnionBase;
import com.querydsl.sql.UpdateBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.Turso")
public class TursoSuiteTest extends AbstractSuite {

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
    Connections.initTurso();
    Connections.initConfiguration(TursoTemplates.builder().newLineToSingleSpace().build());
  }
}
