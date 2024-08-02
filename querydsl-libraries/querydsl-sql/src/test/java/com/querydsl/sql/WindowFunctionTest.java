package com.querydsl.sql;

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
    Expression<?> wf = SQLExpressions.sum(path).over().partitionBy(path2).orderBy(path);
    assertThat(toString(wf)).isEqualTo("sum(path) over (partition by path2 order by path asc)");
  }

  @Test
  public void complex_nullsFirst() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    Expression<?> wf =
        SQLExpressions.sum(path).over().partitionBy(path2).orderBy(path.desc().nullsFirst());
    assertThat(toString(wf))
        .isEqualTo("sum(path) over (partition by path2 order by path desc nulls first)");
  }

  @Test
  public void all() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    assertThat(toString(SQLExpressions.avg(path))).isEqualTo("avg(path)");
    assertThat(toString(SQLExpressions.count(path))).isEqualTo("count(path)");
    assertThat(toString(SQLExpressions.corr(path, path2))).isEqualTo("corr(path,path2)");
    assertThat(toString(SQLExpressions.covarPop(path, path2))).isEqualTo("covar_pop(path,path2)");
    assertThat(toString(SQLExpressions.covarSamp(path, path2))).isEqualTo("covar_samp(path,path2)");
    assertThat(toString(SQLExpressions.cumeDist())).isEqualTo("cume_dist()");
    assertThat(toString(SQLExpressions.denseRank())).isEqualTo("dense_rank()");
    assertThat(toString(SQLExpressions.firstValue(path))).isEqualTo("first_value(path)");
    assertThat(toString(SQLExpressions.lag(path))).isEqualTo("lag(path)");
    assertThat(toString(SQLExpressions.lastValue(path))).isEqualTo("last_value(path)");
    assertThat(toString(SQLExpressions.lead(path))).isEqualTo("lead(path)");
    assertThat(toString(SQLExpressions.max(path))).isEqualTo("max(path)");
    assertThat(toString(SQLExpressions.min(path))).isEqualTo("min(path)");
    assertThat(toString(SQLExpressions.nthValue(path, 3))).isEqualTo("nth_value(path, ?)");
    assertThat(toString(SQLExpressions.ntile(4))).isEqualTo("ntile(?)");
    assertThat(toString(SQLExpressions.percentRank())).isEqualTo("percent_rank()");
    assertThat(toString(SQLExpressions.rank())).isEqualTo("rank()");
    assertThat(toString(SQLExpressions.ratioToReport(path))).isEqualTo("ratio_to_report(path)");
    assertThat(toString(SQLExpressions.rowNumber())).isEqualTo("row_number()");
    assertThat(toString(SQLExpressions.stddev(path))).isEqualTo("stddev(path)");
    assertThat(toString(SQLExpressions.stddevDistinct(path))).isEqualTo("stddev(distinct path)");
    assertThat(toString(SQLExpressions.stddevPop(path))).isEqualTo("stddev_pop(path)");
    assertThat(toString(SQLExpressions.stddevSamp(path))).isEqualTo("stddev_samp(path)");
    assertThat(toString(SQLExpressions.sum(path))).isEqualTo("sum(path)");
    assertThat(toString(SQLExpressions.variance(path))).isEqualTo("variance(path)");
    assertThat(toString(SQLExpressions.varPop(path))).isEqualTo("var_pop(path)");
    assertThat(toString(SQLExpressions.varSamp(path))).isEqualTo("var_samp(path)");

    // TODO FIRST
    // TODO LAST
    // TODO NTH_VALUE ... FROM (FIRST|LAST) (RESPECT|IGNORE) NULLS
  }

  @Test
  public void regr() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
    assertThat(toString(SQLExpressions.regrSlope(path, path2)))
        .isEqualTo("regr_slope(path, path2)");
    assertThat(toString(SQLExpressions.regrIntercept(path, path2)))
        .isEqualTo("regr_intercept(path, path2)");
    assertThat(toString(SQLExpressions.regrCount(path, path2)))
        .isEqualTo("regr_count(path, path2)");
    assertThat(toString(SQLExpressions.regrR2(path, path2))).isEqualTo("regr_r2(path, path2)");
    assertThat(toString(SQLExpressions.regrAvgx(path, path2))).isEqualTo("regr_avgx(path, path2)");
    assertThat(toString(SQLExpressions.regrAvgy(path, path2))).isEqualTo("regr_avgy(path, path2)");
    assertThat(toString(SQLExpressions.regrSxx(path, path2))).isEqualTo("regr_sxx(path, path2)");
    assertThat(toString(SQLExpressions.regrSyy(path, path2))).isEqualTo("regr_syy(path, path2)");
    assertThat(toString(SQLExpressions.regrSxy(path, path2))).isEqualTo("regr_sxy(path, path2)");
  }

  @Test
  public void rows_between() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    var wf = SQLExpressions.sum(path).over().orderBy(path);

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
    var wf = SQLExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().unboundedPreceding()))
        .isEqualTo("sum(path) over (order by path asc rows unbounded preceding)");
  }

  @Test
  public void rows_currentRow() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    var wf = SQLExpressions.sum(path).over().orderBy(path);

    assertThat(toString(wf.rows().currentRow()))
        .isEqualTo("sum(path) over (order by path asc rows current row)");
  }

  @Test
  public void rows_precedingRow() {
    NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    var wf = SQLExpressions.sum(path).over().orderBy(path);

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
    assertThat(toString(SQLExpressions.min(path).keepFirst().orderBy(path2)))
        .isEqualTo("min(path) keep (dense_rank first order by path2 asc)");
    assertThat(
            toString(SQLExpressions.min(path).keepFirst().orderBy(path2).over().partitionBy(path3)))
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
            toString(SQLExpressions.min(path).keepLast().orderBy(path2).over().partitionBy(path3)))
        .isEqualTo("min(path) keep (dense_rank last order by path2 asc) over (partition by path3)");
  }
}
