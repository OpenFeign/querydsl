package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DateTimePath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SStatuschange_ is a FluentQ query type for SStatuschange_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SStatuschange_ extends fluentq.sql.RelationalPathBase<SStatuschange_> {

  private static final long serialVersionUID = 1805594357;

  public static final SStatuschange_ statuschange_ = new SStatuschange_("statuschange_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final DateTimePath<java.sql.Timestamp> timestamp =
      createDateTime("timestamp", java.sql.Timestamp.class);

  public final fluentq.sql.PrimaryKey<SStatuschange_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SItem_statuschange_> _item_statuschange_statusChangesIDFK =
      createInvForeignKey(id, "statusChanges_ID");

  public SStatuschange_(String variable) {
    super(SStatuschange_.class, forVariable(variable), "null", "statuschange_");
    addMetadata();
  }

  public SStatuschange_(String variable, String schema, String table) {
    super(SStatuschange_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SStatuschange_(String variable, String schema) {
    super(SStatuschange_.class, forVariable(variable), schema, "statuschange_");
    addMetadata();
  }

  public SStatuschange_(Path<? extends SStatuschange_> path) {
    super(path.getType(), path.getMetadata(), "null", "statuschange_");
    addMetadata();
  }

  public SStatuschange_(PathMetadata metadata) {
    super(SStatuschange_.class, metadata, "null", "statuschange_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        timestamp,
        ColumnMetadata.named("TIMESTAMP").withIndex(2).ofType(Types.TIMESTAMP).withSize(19));
  }
}
