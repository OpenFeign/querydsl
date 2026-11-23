package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DatePath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDateTest is a FluentQ query type for SDateTest */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SDateTest extends fluentq.sql.RelationalPathBase<SDateTest> {

  private static final long serialVersionUID = 1801758184;

  public static final SDateTest dateTest1 = new SDateTest("DATE_TEST");

  public final DatePath<java.sql.Date> dateTest = createDate("dateTest", java.sql.Date.class);

  public SDateTest(String variable) {
    super(SDateTest.class, forVariable(variable), "null", "DATE_TEST");
    addMetadata();
  }

  public SDateTest(String variable, String schema, String table) {
    super(SDateTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDateTest(String variable, String schema) {
    super(SDateTest.class, forVariable(variable), schema, "DATE_TEST");
    addMetadata();
  }

  public SDateTest(Path<? extends SDateTest> path) {
    super(path.getType(), path.getMetadata(), "null", "DATE_TEST");
    addMetadata();
  }

  public SDateTest(PathMetadata metadata) {
    super(SDateTest.class, metadata, "null", "DATE_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        dateTest, ColumnMetadata.named("DATE_TEST").withIndex(1).ofType(Types.DATE).withSize(10));
  }
}
