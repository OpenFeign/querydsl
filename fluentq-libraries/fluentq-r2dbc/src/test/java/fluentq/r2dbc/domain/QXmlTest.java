package fluentq.r2dbc.domain;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import fluentq.sql.RelationalPathBase;
import jakarta.annotation.Generated;
import java.sql.Types;

/** QXmlTest is a FluentQ query type for QXmlTest */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class QXmlTest extends RelationalPathBase<QXmlTest> {

  private static final long serialVersionUID = 574759316;

  public static final QXmlTest xmlTest = new QXmlTest("XML_TEST");

  public final StringPath col = createString("COL");

  public QXmlTest(String variable) {
    super(QXmlTest.class, forVariable(variable), "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public QXmlTest(String variable, String schema, String table) {
    super(QXmlTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public QXmlTest(Path<? extends QXmlTest> path) {
    super(path.getType(), path.getMetadata(), "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public QXmlTest(PathMetadata metadata) {
    super(QXmlTest.class, metadata, "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        col, ColumnMetadata.named("COL").withIndex(1).ofType(Types.SQLXML).withSize(2147483647));
  }
}
