package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNamed_ is a FluentQ query type for SNamed_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SNamed_ extends fluentq.sql.RelationalPathBase<SNamed_> {

  private static final long serialVersionUID = 703600334;

  public static final SNamed_ named_ = new SNamed_("named_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SNamed_> primary = createPrimaryKey(id);

  public SNamed_(String variable) {
    super(SNamed_.class, forVariable(variable), "null", "named_");
    addMetadata();
  }

  public SNamed_(String variable, String schema, String table) {
    super(SNamed_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNamed_(String variable, String schema) {
    super(SNamed_.class, forVariable(variable), schema, "named_");
    addMetadata();
  }

  public SNamed_(Path<? extends SNamed_> path) {
    super(path.getType(), path.getMetadata(), "null", "named_");
    addMetadata();
  }

  public SNamed_(PathMetadata metadata) {
    super(SNamed_.class, metadata, "null", "named_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
