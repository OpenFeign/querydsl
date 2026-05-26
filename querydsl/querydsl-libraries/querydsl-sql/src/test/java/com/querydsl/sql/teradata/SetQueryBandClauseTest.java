package com.querydsl.sql.teradata;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import org.junit.Before;
import org.junit.Test;

public class SetQueryBandClauseTest {

  private Configuration conf;

  private SetQueryBandClause clause;

  @Before
  public void setUp() {
    conf = new Configuration(SQLTemplates.DEFAULT);
    conf.setUseLiterals(true);
    clause = new SetQueryBandClause((Connection) null, conf);
  }

  @Test
  public void toString_() {
    clause.set("a", "b");
    assertThat(clause).hasToString("set query_band='a=b;' for session");
  }

  @Test
  public void toString2() {
    conf.setUseLiterals(false);
    clause.set("a", "b");
    clause.forTransaction();
    assertThat(clause).hasToString("set query_band=? for transaction");
  }

  @Test
  public void forTransaction() {
    clause.forTransaction();
    clause.set("a", "b");
    clause.set("b", "c");
    assertThat(clause).hasToString("set query_band='a=b;b=c;' for transaction");
  }

  @Test
  public void getSQL() {
    clause.forTransaction();
    clause.set("a", "b");
    clause.set("b", "c");
    assertThat(clause.getSQL().getFirst().getSQL())
        .isEqualTo("set query_band='a=b;b=c;' for transaction");
  }
}
