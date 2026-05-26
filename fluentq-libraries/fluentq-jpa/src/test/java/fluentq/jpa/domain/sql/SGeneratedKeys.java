package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SGeneratedKeys is a FluentQ query type for SGeneratedKeys */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SGeneratedKeys extends fluentq.sql.RelationalPathBase<SGeneratedKeys> {

  private static final long serialVersionUID = 2097474715;

  public static final SGeneratedKeys generatedKeys = new SGeneratedKeys("GENERATED_KEYS");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SGeneratedKeys> primary = createPrimaryKey(id);

  public SGeneratedKeys(String variable) {
    super(SGeneratedKeys.class, forVariable(variable), "null", "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(String variable, String schema, String table) {
    super(SGeneratedKeys.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SGeneratedKeys(String variable, String schema) {
    super(SGeneratedKeys.class, forVariable(variable), schema, "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(Path<? extends SGeneratedKeys> path) {
    super(path.getType(), path.getMetadata(), "null", "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(PathMetadata metadata) {
    super(SGeneratedKeys.class, metadata, "null", "GENERATED_KEYS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(30));
  }
}
