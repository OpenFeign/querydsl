package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SLocation_ is a FluentQ query type for SLocation_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SLocation_ extends fluentq.sql.RelationalPathBase<SLocation_> {

  private static final long serialVersionUID = -917806142;

  public static final SLocation_ location_ = new SLocation_("location_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SLocation_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SStore_> _store_LOCATIONIDFK =
      createInvForeignKey(id, "LOCATION_ID");

  public SLocation_(String variable) {
    super(SLocation_.class, forVariable(variable), "null", "location_");
    addMetadata();
  }

  public SLocation_(String variable, String schema, String table) {
    super(SLocation_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLocation_(String variable, String schema) {
    super(SLocation_.class, forVariable(variable), schema, "location_");
    addMetadata();
  }

  public SLocation_(Path<? extends SLocation_> path) {
    super(path.getType(), path.getMetadata(), "null", "location_");
    addMetadata();
  }

  public SLocation_(PathMetadata metadata) {
    super(SLocation_.class, metadata, "null", "location_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
