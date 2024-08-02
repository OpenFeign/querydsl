package com.querydsl.r2dbc;

import static com.querydsl.core.Target.CUBRID;
import static com.querydsl.core.Target.DB2;
import static com.querydsl.core.Target.DERBY;
import static com.querydsl.core.Target.FIREBIRD;
import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.HSQLDB;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLITE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.core.Target.TERADATA;
import static com.querydsl.r2dbc.Constants.employee;
import static com.querydsl.r2dbc.Constants.employee2;
import static com.querydsl.r2dbc.Constants.survey;
import static com.querydsl.r2dbc.Constants.survey2;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.r2dbc.domain.Employee;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.sql.ForeignKey;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;

public abstract class SubqueriesBase extends AbstractBaseTest {

  @Test
  @ExcludeIn({CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER})
  public void keys() {
    var employee2 = new QEmployee("employee2");
    var nameKey1 =
        new ForeignKey<Employee>(
            employee,
            Arrays.asList(employee.firstname, employee.lastname),
            Arrays.asList("a", "b"));
    var nameKey2 =
        new ForeignKey<Employee>(
            employee,
            Arrays.asList(employee.firstname, employee.lastname),
            Arrays.asList("a", "b"));

    query()
        .from(employee)
        .where(nameKey1.in(query().from(employee2).select(nameKey2.getProjection())))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @ExcludeIn({CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER})
  public void list_in_query() {
    var employee2 = new QEmployee("employee2");
    query()
        .from(employee)
        .where(
            Expressions.list(employee.id, employee.lastname)
                .in(query().from(employee2).select(employee2.id, employee2.lastname)))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @SkipForQuoted
  @ExcludeIn(DB2) // ID is reserved IN DB2
  public void subQueries() {
    // subquery in where block
    expectedQuery =
        """
        select e.ID from EMPLOYEE e \
        where e.ID = (select max(e.ID) \
        from EMPLOYEE e)\
        """;
    var list =
        query()
            .from(employee)
            .where(employee.id.eq(query().from(employee).select(employee.id.max())))
            .select(employee.id)
            .fetch()
            .collectList()
            .block();
    assertThat(list).isNotEmpty();
  }

  @Test
  public void subQuery_alias() {
    query()
        .from(query().from(employee).select(employee.all()).as(employee2))
        .select(employee2.all())
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_all() {
    query()
        .from(employee)
        .where(employee.id.gtAll(query().from(employee2).select(employee2.id)))
        .fetchCount()
        .block();
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_any() {
    query()
        .from(employee)
        .where(employee.id.gtAny(query().from(employee2).select(employee2.id)))
        .fetchCount()
        .block();
  }

  @Test
  public void subQuery_innerJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    var sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .innerJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void subQuery_leftJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    var sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .leftJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @ExcludeIn({MYSQL, POSTGRESQL, DERBY, SQLSERVER, TERADATA})
  public void subQuery_params() {
    var aParam = new Param<String>(String.class, "param");
    R2DBCQuery<?> subQuery =
        R2DBCExpressions.select(Wildcard.all).from(employee).where(employee.firstname.eq(aParam));
    subQuery.set(aParam, "Mike");

    assertThat((long) query().from(subQuery).fetchCount().block()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_rightJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    var sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .rightJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void subQuery_with_alias() {
    var ids1 = query().from(employee).select(employee.id).fetch().collectList().block();
    var ids2 =
        query()
            .from(query().from(employee).select(employee.id), employee)
            .select(employee.id)
            .fetch()
            .collectList()
            .block();
    assertThat(ids2).isEqualTo(ids1);
  }

  @Test
  public void subQuery_with_alias2() {
    var ids1 = query().from(employee).select(employee.id).fetch().collectList().block();
    var ids2 =
        query()
            .from(query().from(employee).select(employee.id).as(employee))
            .select(employee.id)
            .fetch()
            .collectList()
            .block();
    assertThat(ids2).isEqualTo(ids1);
  }

  @Test
  @SkipForQuoted
  public void subQuerySerialization() {
    R2DBCQuery<?> query = query();
    query.from(survey);
    assertThat(query).hasToString("from SURVEY s");

    query.from(survey2);
    assertThat(query).hasToString("from SURVEY s, SURVEY s2");
  }

  @Test
  public void subQuerySerialization2() {
    NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
    var sq = new PathBuilder<Object[]>(Object[].class, "sq");
    var serializer = new SQLSerializer(Configuration.DEFAULT);

    serializer.handle(
        query()
            .from(employee)
            .select(employee.salary.add(employee.salary).add(employee.salary).as(sal))
            .as(sq));
    assertThat(serializer.toString())
        .isEqualTo("(select (e.SALARY + e.SALARY + e.SALARY) as sal\nfrom EMPLOYEE e) as sq");
  }

  @Test
  public void scalarSubQueryInClause() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);

    serializer.handle(
        this.query()
            .from(employee)
            .where(
                R2DBCExpressions.select(employee.firstname)
                    .from(employee)
                    .orderBy(employee.salary.asc())
                    .limit(1)
                    .in(Arrays.asList("Mike", "Mary"))));

    expectedQuery =
        """
        (
        from EMPLOYEE e
        where (select e.FIRSTNAME
        from EMPLOYEE e
        order by e.SALARY asc
        limit ?) in (?, ?))\
        """;

    assertThat(serializer).hasToString(expectedQuery);
  }

  @Test
  public void scalarSubQueryInClause2() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);

    serializer.handle(
        this.query()
            .from(employee)
            .where(
                R2DBCExpressions.select(employee.firstname)
                    .from(employee)
                    .orderBy(employee.salary.asc())
                    .limit(1)
                    .in("Mike", "Mary")));

    expectedQuery =
        """
        (
        from EMPLOYEE e
        where (select e.FIRSTNAME
        from EMPLOYEE e
        order by e.SALARY asc
        limit ?) in (?, ?))\
        """;

    assertThat(serializer).hasToString(expectedQuery);
  }
}
