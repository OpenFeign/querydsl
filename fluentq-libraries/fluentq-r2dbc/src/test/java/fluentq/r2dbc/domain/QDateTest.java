package fluentq.r2dbc.domain;

import fluentq.core.types.PathMetadata;
import fluentq.core.types.PathMetadataFactory;
import fluentq.core.types.dsl.DatePath;
import fluentq.sql.ColumnMetadata;
import fluentq.sql.RelationalPathBase;
import java.time.LocalDateTime;

public class QDateTest extends RelationalPathBase<QDateTest> {

  private static final long serialVersionUID = 1394463749655231079L;

  public static final QDateTest qDateTest = new QDateTest("DATE_TEST");

  public final DatePath<LocalDateTime> dateTest = createDate("dateTest", LocalDateTime.class);

  public QDateTest(String path) {
    super(QDateTest.class, PathMetadataFactory.forVariable(path), "PUBLIC", "DATE_TEST");
    addMetadata();
  }

  public QDateTest(PathMetadata metadata) {
    super(QDateTest.class, metadata, "PUBLIC", "DATE_TEST");
    addMetadata();
  }

  protected void addMetadata() {
    addMetadata(dateTest, ColumnMetadata.named("DATE_TEST"));
  }
}
