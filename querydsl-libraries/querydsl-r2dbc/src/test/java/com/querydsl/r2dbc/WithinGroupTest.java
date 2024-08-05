package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Test;

public class WithinGroupTest {

  private static String toString(Expression<?> e) {
    return new SQLSerializer(Configuration.DEFAULT).handle(e).toString();
  }

  @Test
  public void all() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");

    assertThat(toString(R2DBCExpressions.cumeDist(path))).isEqualTo("cume_dist(path)");
    assertThat(toString(R2DBCExpressions.cumeDist(path, path2)))
        .isEqualTo("cume_dist(path, path2)");
    assertThat(toString(R2DBCExpressions.denseRank(path, path2)))
        .isEqualTo("dense_rank(path, path2)");
    assertThat(toString(R2DBCExpressions.listagg(path, ","))).isEqualTo("listagg(path,',')");
    assertThat(toString(R2DBCExpressions.percentRank(path, path2)))
        .isEqualTo("percent_rank(path, path2)");
    assertThat(toString(R2DBCExpressions.percentileCont(path))).isEqualTo("percentile_cont(path)");
    assertThat(toString(R2DBCExpressions.percentileDisc(path))).isEqualTo("percentile_disc(path)");
    assertThat(toString(R2DBCExpressions.rank(path, path2))).isEqualTo("rank(path, path2)");
  }
}
