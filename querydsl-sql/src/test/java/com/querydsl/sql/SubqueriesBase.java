package com.querydsl.sql;

import static com.querydsl.core.Target.*;
import static com.querydsl.sql.Constants.*;
import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QEmployee;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class SubqueriesBase extends AbstractBaseTest {

  @Test
  @ExcludeIn({CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER})
  public void keys() {
    QEmployee employee2 = new QEmployee("employee2");
    ForeignKey<Employee> nameKey1 =
        new ForeignKey<Employee>(
            employee,
            Arrays.asList(employee.firstname, employee.lastname),
            Arrays.asList("a", "b"));
    ForeignKey<Employee> nameKey2 =
        new ForeignKey<Employee>(
            employee,
            Arrays.asList(employee.firstname, employee.lastname),
            Arrays.asList("a", "b"));

    query()
        .from(employee)
        .where(nameKey1.in(query().from(employee2).select(nameKey2.getProjection())))
        .select(employee.id)
        .fetch();
  }

  @Test
  @ExcludeIn({CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER})
  public void list_in_query() {
    QEmployee employee2 = new QEmployee("employee2");
    query()
        .from(employee)
        .where(
            Expressions.list(employee.id, employee.lastname)
                .in(query().from(employee2).select(employee2.id, employee2.lastname)))
        .select(employee.id)
        .fetch();
  }

  @Test
  @SkipForQuoted
  @ExcludeIn(DB2) // ID is reserved IN DB2
  public void subQueries() throws SQLException {
    // subquery in where block
    expectedQuery =
        """
        select e.ID from EMPLOYEE e \
        where e.ID = (select max(e.ID) \
        from EMPLOYEE e)\
        """;
    List<Integer> list =
        query()
            .from(employee)
            .where(employee.id.eq(query().from(employee).select(employee.id.max())))
            .select(employee.id)
            .fetch();
    assertThat(list).isNotEmpty();
  }

  @Test
  public void subQuery_alias() {
    query()
        .from(query().from(employee).select(employee.all()).as(employee2))
        .select(employee2.all())
        .fetch();
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_all() {
    query()
        .from(employee)
        .where(employee.id.gtAll(query().from(employee2).select(employee2.id)))
        .fetchCount();
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_any() {
    query()
        .from(employee)
        .where(employee.id.gtAny(query().from(employee2).select(employee2.id)))
        .fetchCount();
  }

  @Test
  public void subQuery_innerJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    QEmployee sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .innerJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch();
  }

  @Test
  public void subQuery_leftJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    QEmployee sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .leftJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch();
  }

  @Test
  @ExcludeIn({MYSQL, POSTGRESQL, DERBY, SQLSERVER, TERADATA})
  public void subQuery_params() {
    Param<String> aParam = new Param<String>(String.class, "param");
    SQLQuery<?> subQuery = select(Wildcard.all).from(employee).where(employee.firstname.eq(aParam));
    subQuery.set(aParam, "Mike");

    assertThat(query().from(subQuery).fetchCount()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(SQLITE)
  public void subQuery_rightJoin() {
    SubQueryExpression<Integer> sq = query().from(employee2).select(employee2.id);
    QEmployee sqEmp = new QEmployee("sq");
    query()
        .from(employee)
        .rightJoin(sq, sqEmp)
        .on(sqEmp.id.eq(employee.id))
        .select(employee.id)
        .fetch();
  }

  @Test
  public void subQuery_with_alias() {
    List<Integer> ids1 = query().from(employee).select(employee.id).fetch();
    List<Integer> ids2 =
        query()
            .from(query().from(employee).select(employee.id), employee)
            .select(employee.id)
            .fetch();
    assertThat(ids2).isEqualTo(ids1);
  }

  @Test
  public void subQuery_with_alias2() {
    List<Integer> ids1 = query().from(employee).select(employee.id).fetch();
    List<Integer> ids2 =
        query()
            .from(query().from(employee).select(employee.id).as(employee))
            .select(employee.id)
            .fetch();
    assertThat(ids2).isEqualTo(ids1);
  }

  @Test
  @SkipForQuoted
  public void subQuerySerialization() {
    SQLQuery<?> query = query();
    query.from(survey);
    assertThat(query.toString()).isEqualTo("from SURVEY s");

    query.from(survey2);
    assertThat(query.toString()).isEqualTo("from SURVEY s, SURVEY s2");
  }

  @Test
  public void subQuerySerialization2() {
    NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
    PathBuilder<Object[]> sq = new PathBuilder<Object[]>(Object[].class, "sq");
    SQLSerializer serializer = new SQLSerializer(Configuration.DEFAULT);

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
    SQLSerializer serializer = new SQLSerializer(Configuration.DEFAULT);

    serializer.handle(
        this.query()
            .from(employee)
            .where(
                SQLExpressions.select(employee.firstname)
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

    assertThat(serializer.toString()).isEqualTo(expectedQuery);
  }

  @Test
  public void scalarSubQueryInClause2() {
    SQLSerializer serializer = new SQLSerializer(Configuration.DEFAULT);

    serializer.handle(
        this.query()
            .from(employee)
            .where(
                SQLExpressions.select(employee.firstname)
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

    assertThat(serializer.toString()).isEqualTo(expectedQuery);
  }
}
