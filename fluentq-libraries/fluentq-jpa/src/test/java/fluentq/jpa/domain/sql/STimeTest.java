package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.TimePath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** STimeTest is a FluentQ query type for STimeTest */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class STimeTest extends fluentq.sql.RelationalPathBase<STimeTest> {

  private static final long serialVersionUID = -2068356729;

  public static final STimeTest timeTest1 = new STimeTest("TIME_TEST");

  public final TimePath<java.sql.Time> timeTest = createTime("timeTest", java.sql.Time.class);

  public STimeTest(String variable) {
    super(STimeTest.class, forVariable(variable), "null", "TIME_TEST");
    addMetadata();
  }

  public STimeTest(String variable, String schema, String table) {
    super(STimeTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public STimeTest(String variable, String schema) {
    super(STimeTest.class, forVariable(variable), schema, "TIME_TEST");
    addMetadata();
  }

  public STimeTest(Path<? extends STimeTest> path) {
    super(path.getType(), path.getMetadata(), "null", "TIME_TEST");
    addMetadata();
  }

  public STimeTest(PathMetadata metadata) {
    super(STimeTest.class, metadata, "null", "TIME_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        timeTest, ColumnMetadata.named("TIME_TEST").withIndex(1).ofType(Types.TIME).withSize(8));
  }
}
