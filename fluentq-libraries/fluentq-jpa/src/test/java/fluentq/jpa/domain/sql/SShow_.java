package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SShow_ is a FluentQ query type for SShow_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SShow_ extends fluentq.sql.RelationalPathBase<SShow_> {

  private static final long serialVersionUID = -942305926;

  public static final SShow_ show_ = new SShow_("show_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

  public final fluentq.sql.PrimaryKey<SShow_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SShow_> show_PARENTIDFK = createForeignKey(parentId, "ID");

  public final fluentq.sql.ForeignKey<SShowACTS> _showACTSShowIDFK =
      createInvForeignKey(id, "Show_ID");

  public final fluentq.sql.ForeignKey<SShow_> _show_PARENTIDFK =
      createInvForeignKey(id, "PARENT_ID");

  public SShow_(String variable) {
    super(SShow_.class, forVariable(variable), "null", "show_");
    addMetadata();
  }

  public SShow_(String variable, String schema, String table) {
    super(SShow_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SShow_(String variable, String schema) {
    super(SShow_.class, forVariable(variable), schema, "show_");
    addMetadata();
  }

  public SShow_(Path<? extends SShow_> path) {
    super(path.getType(), path.getMetadata(), "null", "show_");
    addMetadata();
  }

  public SShow_(PathMetadata metadata) {
    super(SShow_.class, metadata, "null", "show_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        parentId, ColumnMetadata.named("PARENT_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
