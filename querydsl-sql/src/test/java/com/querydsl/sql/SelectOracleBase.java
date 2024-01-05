package com.querydsl.sql;

import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.sql.Constants.employee;
import static com.querydsl.sql.oracle.OracleGrammar.level;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.oracle.OracleQuery;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class SelectOracleBase extends AbstractBaseTest {

  private static final Logger logger = Logger.getLogger(AbstractSQLQuery.class.getName());

  protected OracleQuery<?> oracleQuery() {
    return new OracleQuery<Void>(connection, configuration) {
      @Override
      protected SQLSerializer serialize(boolean forCountRow) {
        SQLSerializer serializer = super.serialize(forCountRow);
        String rv = serializer.toString();
        if (expectedQuery != null) {
          assertThat(rv.replace('\n', ' ')).isEqualTo(expectedQuery);
          expectedQuery = null;
        }
        logger.fine(rv);
        return serializer;
      }
    };
  }

  @Test
  @Ignore
  public void connectBy() throws SQLException {
    // TODO : come up with a legal case
    oracleQuery()
        .from(employee)
        .where(level.eq(-1))
        .connectBy(level.lt(1000))
        .select(employee.id)
        .fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void connectByPrior() throws SQLException {
    expectedQuery =
        """
        select e.ID, e.LASTNAME, e.SUPERIOR_ID \
        from EMPLOYEE e \
        connect by prior e.ID = e.SUPERIOR_ID\
        """;
    oracleQuery()
        .from(employee)
        .connectByPrior(employee.id.eq(employee.superiorId))
        .select(employee.id, employee.lastname, employee.superiorId)
        .fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void connectByPrior2() throws SQLException {
    if (configuration.getUseLiterals()) {
      return;
    }

    expectedQuery =
        """
        select e.ID, e.LASTNAME, e.SUPERIOR_ID \
        from EMPLOYEE e \
        start with e.ID = ? \
        connect by prior e.ID = e.SUPERIOR_ID\
        """;
    oracleQuery()
        .from(employee)
        .startWith(employee.id.eq(1))
        .connectByPrior(employee.id.eq(employee.superiorId))
        .select(employee.id, employee.lastname, employee.superiorId)
        .fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void connectByPrior3() throws SQLException {
    if (configuration.getUseLiterals()) {
      return;
    }

    expectedQuery =
        """
        select e.ID, e.LASTNAME, e.SUPERIOR_ID \
        from EMPLOYEE e \
        start with e.ID = ? \
        connect by prior e.ID = e.SUPERIOR_ID \
        order siblings by e.LASTNAME\
        """;
    oracleQuery()
        .from(employee)
        .startWith(employee.id.eq(1))
        .connectByPrior(employee.id.eq(employee.superiorId))
        .orderSiblingsBy(employee.lastname)
        .select(employee.id, employee.lastname, employee.superiorId)
        .fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void connectByPrior4() throws SQLException {
    if (configuration.getUseLiterals()) {
      return;
    }

    expectedQuery =
        """
        select e.ID, e.LASTNAME, e.SUPERIOR_ID \
        from EMPLOYEE e \
        connect by nocycle prior e.ID = e.SUPERIOR_ID\
        """;
    oracleQuery()
        .from(employee)
        .connectByNocyclePrior(employee.id.eq(employee.superiorId))
        .select(employee.id, employee.lastname, employee.superiorId)
        .fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void sumOver() throws SQLException {
    //        SQL> select deptno,
    //        2  ename,
    //        3  sal,
    //        4  sum(sal) over (partition by deptno
    //        5  order by sal,ename) CumDeptTot,
    //        6  sum(sal) over (partition by deptno) SalByDept,
    //        7  sum(sal) over (order by deptno, sal) CumTot,
    //        8  sum(sal) over () TotSal
    //        9  from emp
    //       10  order by deptno, sal;
    expectedQuery =
        """
        select e.LASTNAME, e.SALARY, \
        sum(e.SALARY) over (partition by e.SUPERIOR_ID order by e.LASTNAME asc, e.SALARY asc), \
        sum(e.SALARY) over (order by e.SUPERIOR_ID asc, e.SALARY asc), \
        sum(e.SALARY) over () from EMPLOYEE e order by e.SALARY asc, e.SUPERIOR_ID asc\
        """;

    oracleQuery()
        .from(employee)
        .orderBy(employee.salary.asc(), employee.superiorId.asc())
        .select(
            employee.lastname,
            employee.salary,
            SQLExpressions.sum(employee.salary)
                .over()
                .partitionBy(employee.superiorId)
                .orderBy(employee.lastname, employee.salary),
            SQLExpressions.sum(employee.salary)
                .over()
                .orderBy(employee.superiorId, employee.salary),
            SQLExpressions.sum(employee.salary).over())
        .fetch();

    // shorter version
    QEmployee e = employee;
    oracleQuery()
        .from(e)
        .orderBy(e.salary.asc(), e.superiorId.asc())
        .select(
            e.lastname,
            e.salary,
            SQLExpressions.sum(e.salary)
                .over()
                .partitionBy(e.superiorId)
                .orderBy(e.lastname, e.salary),
            SQLExpressions.sum(e.salary).over().orderBy(e.superiorId, e.salary),
            SQLExpressions.sum(e.salary).over())
        .fetch();
  }
}
