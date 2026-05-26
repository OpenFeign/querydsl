package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SWorld is a FluentQ query type for SWorld */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SWorld extends fluentq.sql.RelationalPathBase<SWorld> {

  private static final long serialVersionUID = -938400758;

  public static final SWorld world = new SWorld("WORLD");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final fluentq.sql.PrimaryKey<SWorld> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SWorldMammal> _wORLDMAMMALWorldIDFK =
      createInvForeignKey(id, "World_ID");

  public SWorld(String variable) {
    super(SWorld.class, forVariable(variable), "null", "WORLD");
    addMetadata();
  }

  public SWorld(String variable, String schema, String table) {
    super(SWorld.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SWorld(String variable, String schema) {
    super(SWorld.class, forVariable(variable), schema, "WORLD");
    addMetadata();
  }

  public SWorld(Path<? extends SWorld> path) {
    super(path.getType(), path.getMetadata(), "null", "WORLD");
    addMetadata();
  }

  public SWorld(PathMetadata metadata) {
    super(SWorld.class, metadata, "null", "WORLD");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
