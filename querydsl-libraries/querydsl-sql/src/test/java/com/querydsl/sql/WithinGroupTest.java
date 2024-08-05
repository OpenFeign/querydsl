package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Before;
import org.junit.Test;

public class WithinGroupTest {
  NumberPath<Long> path = null;
  NumberPath<Long> path2 = null;

  private static String toString(Expression<?> e) {
    return new SQLSerializer(Configuration.DEFAULT).handle(e).toString();
  }

  @Before
  public void setPaths() {
    this.path = Expressions.numberPath(Long.class, "path");
    this.path2 = Expressions.numberPath(Long.class, "path2");
  }

  @Test
  public void cume_Dist() {
    assertThat(toString(SQLExpressions.cumeDist(path))).isEqualTo("cume_dist(path)");
    assertThat(toString(SQLExpressions.cumeDist(path, path2))).isEqualTo("cume_dist(path, path2)");
  }

  @Test
  public void dense_Rank() {
    assertThat(toString(SQLExpressions.denseRank(path, path2)))
        .isEqualTo("dense_rank(path, path2)");
  }

  @Test
  public void perfect_Rank() {
    assertThat(toString(SQLExpressions.percentRank(path, path2)))
        .isEqualTo("percent_rank(path, path2)");
  }

  @Test
  public void percentile() {
    assertThat(toString(SQLExpressions.percentileCont(path))).isEqualTo("percentile_cont(path)");
    assertThat(toString(SQLExpressions.percentileDisc(path))).isEqualTo("percentile_disc(path)");
  }

  @Test
  public void rank() {
    assertThat(toString(SQLExpressions.rank(path, path2))).isEqualTo("rank(path, path2)");
  }

  @Test
  public void listaggComma() {
    assertThat(toString(SQLExpressions.listagg(path, ","))).isEqualTo("listagg(path,',')");
  }

  @Test
  public void listaggEmpty() {
    assertThat(toString(SQLExpressions.listagg(path, ""))).isEqualTo("listagg(path,'')");
  }

  @Test
  public void listaggSpace() {
    assertThat(toString(SQLExpressions.listagg(path, " "))).isEqualTo("listagg(path,' ')");
  }

  @Test
  public void listaggDelimiter() {
    assertThat(toString(SQLExpressions.listagg(path, "|"))).isEqualTo("listagg(path,'|')");
  }

  @Test
  public void listaggCommaWithSpace() {
    assertThat(toString(SQLExpressions.listagg(path, ", "))).isEqualTo("listagg(path,', ')");
  }

  @Test
  public void listaggIntString() {
    assertThat(toString(SQLExpressions.listagg(path, "1"))).isEqualTo("listagg(path,'1')");
  }
}
