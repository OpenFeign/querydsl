package com.querydsl.sql;

import com.querydsl.core.testutil.ReportingOnly;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(ReportingOnly.class)
public class DateArithmeticTest {

  private String serialize(Expression<?> expr, SQLTemplates templates) {
    SQLSerializer serializer = new SQLSerializer(new Configuration(templates));
    serializer.handle(expr);
    return serializer.toString();
  }

  @Test
  public void test() {
    List<SQLTemplates> list = new ArrayList<>();
    list.add(new CUBRIDTemplates());
    list.add(new DerbyTemplates());
    list.add(new H2Templates());
    list.add(new HSQLDBTemplates());
    list.add(new MySQLTemplates());
    list.add(new OracleTemplates());
    list.add(new PostgreSQLTemplates());
    list.add(new SQLiteTemplates());
    list.add(new SQLServerTemplates());
    list.add(new SQLServer2005Templates());
    list.add(new SQLServer2012Templates());
    list.add(new TeradataTemplates());

    List<Expression<?>> exprs = new ArrayList<>();
    DateTimePath<Date> path = Expressions.dateTimePath(Date.class, "date");
    exprs.add(SQLExpressions.addYears(path, 2));
    exprs.add(SQLExpressions.addMonths(path, 2));
    exprs.add(SQLExpressions.addDays(path, 2));
    exprs.add(SQLExpressions.addHours(path, 2));
    exprs.add(SQLExpressions.addMinutes(path, 2));
    exprs.add(SQLExpressions.addSeconds(path, 2));

    for (SQLTemplates templates : list) {
      System.out.println(templates.getClass().getSimpleName());
      for (Expression<?> expr : exprs) {
        System.err.println(serialize(expr, templates));
      }
      System.out.println();
    }
  }
}
