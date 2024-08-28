package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Test;

public class WindowFunctionTest {

  private static String toString(Expression<?> e) {
    return new SQLSerializer(Configuration.DEFAULT).handle(e).toString();
  }

  @Test
  public void complex() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    Expression<?> wf = R2DBCExpressions.sum(path).over().partitionBy(path2).orderBy(path);
    assertThat(toString(wf)).isEqualTo("sum(path) over (partition by path2 order by path asc)");
  }

  @Test
  public void complex_nullsFirst() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    Expression<?> wf =
        R2DBCExpressions.sum(path).over().partitionBy(path2).orderBy(path.desc().nullsFirst());
    assertThat(toString(wf))
        .isEqualTo("sum(path) over (partition by path2 order by path desc nulls first)");
  }

  @Test
  public void all() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    assertThat(toString(R2DBCExpressions.avg(path))).isEqualTo("avg(path)");
    assertThat(toString(R2DBCExpressions.count(path))).isEqualTo("count(path)");
    assertThat(toString(R2DBCExpressions.corr(path, path2))).isEqualTo("corr(path,path2)");
    assertThat(toString(R2DBCExpressions.covarPop(path, path2))).isEqualTo("covar_pop(path,path2)");
    assertThat(toString(R2DBCExpressions.covarSamp(path, path2)))
        .isEqualTo("covar_samp(path,path2)");
    assertThat(toString(R2DBCExpressions.cumeDist())).isEqualTo("cume_dist()");
    assertThat(toString(R2DBCExpressions.denseRank())).isEqualTo("dense_rank()");
    assertThat(toString(R2DBCExpressions.firstValue(path))).isEqualTo("first_value(path)");
    assertThat(toString(R2DBCExpressions.lag(path))).isEqualTo("lag(path)");
    assertThat(toString(R2DBCExpressions.lastValue(path))).isEqualTo("last_value(path)");
    assertThat(toString(R2DBCExpressions.lead(path))).isEqualTo("lead(path)");
    assertThat(toString(R2DBCExpressions.max(path))).isEqualTo("max(path)");
    assertThat(toString(R2DBCExpressions.min(path))).isEqualTo("min(path)");
    assertThat(toString(R2DBCExpressions.nthValue(path, 3))).isEqualTo("nth_value(path, ?)");
    assertThat(toString(R2DBCExpressions.ntile(4))).isEqualTo("ntile(?)");
    assertThat(toString(R2DBCExpressions.percentRank())).isEqualTo("percent_rank()");
    assertThat(toString(R2DBCExpressions.rank())).isEqualTo("rank()");
    assertThat(toString(R2DBCExpressions.ratioToReport(path))).isEqualTo("ratio_to_report(path)");
    assertThat(toString(R2DBCExpressions.rowNumber())).isEqualTo("row_number()");
    assertThat(toString(R2DBCExpressions.stddev(path))).isEqualTo("stddev(path)");
    assertThat(toString(R2DBCExpressions.stddevDistinct(path))).isEqualTo("stddev(distinct path)");
    assertThat(toString(R2DBCExpressions.stddevPop(path))).isEqualTo("stddev_pop(path)");
    assertThat(toString(R2DBCExpressions.stddevSamp(path))).isEqualTo("stddev_samp(path)");
    assertThat(toString(R2DBCExpressions.sum(path))).isEqualTo("sum(path)");
    assertThat(toString(R2DBCExpressions.variance(path))).isEqualTo("variance(path)");
    assertThat(toString(R2DBCExpressions.varPop(path))).isEqualTo("var_pop(path)");
    assertThat(toString(R2DBCExpressions.varSamp(path))).isEqualTo("var_samp(path)");

    // TODO FIRST
    // TODO LAST
    // TODO NTH_VALUE ... FROM (FIRST|LAST) (RESPECT|IGNORE) NULLS
  }

  @Test
  public void regr() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    assertThat(toString(R2DBCExpressions.regrSlope(path, path2)))
        .isEqualTo("regr_slope(path, path2)");
    assertThat(toString(R2DBCExpressions.regrIntercept(path, path2)))
        .isEqualTo("regr_intercept(path, path2)");
    assertThat(toString(R2DBCExpressions.regrCount(path, path2)))
        .isEqualTo("regr_count(path, path2)");
    assertThat(toString(R2DBCExpressions.regrR2(path, path2))).isEqualTo("regr_r2(path, path2)");
    assertThat(toString(R2DBCExpressions.regrAvgx(path, path2)))
        .isEqualTo("regr_avgx(path, path2)");
    assertThat(toString(R2DBCExpressions.regrAvgy(path, path2)))
        .isEqualTo("regr_avgy(path, path2)");
    assertThat(toString(R2DBCExpressions.regrSxx(path, path2))).isEqualTo("regr_sxx(path, path2)");
    assertThat(toString(R2DBCExpressions.regrSyy(path, path2))).isEqualTo("regr_syy(path, path2)");
    assertThat(toString(R2DBCExpressions.regrSxy(path, path2))).isEqualTo("regr_sxy(path, path2)");
  }

  @Test
  public void rows_between() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    var wf = R2DBCExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().between().currentRow().unboundedFollowing()))
        .isEqualTo(
            "sum(path) over (order by path asc rows between current row and unbounded following)");
    assertThat(toString(wf.rows().between().preceding(intPath).following(intPath)))
        .isEqualTo(
            """
            sum(path) over (order by path asc rows between preceding intPath and following\
             intPath)\
            """);
    assertThat(toString(wf.rows().between().preceding(1).following(3)))
        .isEqualTo("sum(path) over (order by path asc rows between preceding ? and following ?)");
  }

  @Test
  public void rows_unboundedPreceding() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    var wf = R2DBCExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().unboundedPreceding()))
        .isEqualTo("sum(path) over (order by path asc rows unbounded preceding)");
  }

  @Test
  public void rows_currentRow() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    var wf = R2DBCExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().currentRow()))
        .isEqualTo("sum(path) over (order by path asc rows current row)");
  }

  @Test
  public void rows_precedingRow() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    var wf = R2DBCExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().preceding(intPath)))
        .isEqualTo("sum(path) over (order by path asc rows preceding intPath)");
    assertThat(toString(wf.rows().preceding(3)))
        .isEqualTo("sum(path) over (order by path asc rows preceding ?)");
  }

  @Test
  public void keep_first() {
    // MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id)
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    NumberPath<Long> path3 = Expressions.numberPath(Long.class, "path3");
    assertThat(toString(R2DBCExpressions.min(path).keepFirst().orderBy(path2)))
        .isEqualTo("min(path) keep (dense_rank first order by path2 asc)");
    assertThat(
            toString(
                R2DBCExpressions.min(path).keepFirst().orderBy(path2).over().partitionBy(path3)))
        .isEqualTo(
            "min(path) keep (dense_rank first order by path2 asc) over (partition by path3)");
  }

  @Test
  public void keep_last() {
    // MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id)
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    NumberPath<Long> path3 = Expressions.numberPath(Long.class, "path3");
    assertThat(
            toString(
                R2DBCExpressions.min(path).keepLast().orderBy(path2).over().partitionBy(path3)))
        .isEqualTo("min(path) keep (dense_rank last order by path2 asc) over (partition by path3)");
  }
}
