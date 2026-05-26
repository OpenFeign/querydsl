package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNameListNAMES is a FluentQ query type for SNameListNAMES */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SNameListNAMES extends fluentq.sql.RelationalPathBase<SNameListNAMES> {

  private static final long serialVersionUID = 1866941303;

  public static final SNameListNAMES NameListNAMES = new SNameListNAMES("NameList_NAMES");

  public final NumberPath<Long> nameListID = createNumber("nameListID", Long.class);

  public final StringPath names = createString("names");

  public final fluentq.sql.ForeignKey<SNamelist_> nameListNAMESNameListIDFK =
      createForeignKey(nameListID, "ID");

  public SNameListNAMES(String variable) {
    super(SNameListNAMES.class, forVariable(variable), "null", "NameList_NAMES");
    addMetadata();
  }

  public SNameListNAMES(String variable, String schema, String table) {
    super(SNameListNAMES.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNameListNAMES(String variable, String schema) {
    super(SNameListNAMES.class, forVariable(variable), schema, "NameList_NAMES");
    addMetadata();
  }

  public SNameListNAMES(Path<? extends SNameListNAMES> path) {
    super(path.getType(), path.getMetadata(), "null", "NameList_NAMES");
    addMetadata();
  }

  public SNameListNAMES(PathMetadata metadata) {
    super(SNameListNAMES.class, metadata, "null", "NameList_NAMES");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        nameListID,
        ColumnMetadata.named("NameList_ID").withIndex(1).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        names, ColumnMetadata.named("NAMES").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
