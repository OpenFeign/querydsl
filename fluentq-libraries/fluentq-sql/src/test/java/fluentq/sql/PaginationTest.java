package fluentq.sql;

import fluentq.core.QueryMetadata;
import fluentq.core.QueryModifiers;
import fluentq.core.support.QueryMixin;
import fluentq.sql.domain.QEmployee;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.ReportingOnly")
public class PaginationTest {

  private String serialize(QueryMetadata metadata, SQLTemplates templates) {
    var serializer = new SQLSerializer(new Configuration(templates));
    serializer.serialize(metadata, false);
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
    list.add(new OracleTemplates()); // inner query
    list.add(new PostgreSQLTemplates());
    list.add(new SQLiteTemplates());
    list.add(new SQLServerTemplates());
    list.add(new SQLServer2005Templates()); // inner query
    list.add(new SQLServer2012Templates());
    list.add(new TeradataTemplates()); // qualify

    for (SQLTemplates templates : list) {
      var employee = QEmployee.employee;
      QueryMixin<?> query = new QueryMixin<Void>();
      query.from(employee);
      query.orderBy(employee.firstname.asc());
      query.setProjection(employee.id);

      System.out.println(templates.getClass().getSimpleName());
      System.out.println();

      // limit
      query.restrict(QueryModifiers.limit(10L));
      System.out.println("* limit");
      System.out.println(serialize(query.getMetadata(), templates));
      System.out.println();

      if (!templates.getClass().equals(SQLServerTemplates.class)) {
        // offset
        query.restrict(QueryModifiers.offset(10L));
        System.out.println("* offset");
        System.out.println(serialize(query.getMetadata(), templates));
        System.out.println();

        // limit and offset
        query.restrict(new QueryModifiers(10L, 10L));
        System.out.println("* limit and offset");
        System.out.println(serialize(query.getMetadata(), templates));
        System.out.println();
      }
    }
  }
}
