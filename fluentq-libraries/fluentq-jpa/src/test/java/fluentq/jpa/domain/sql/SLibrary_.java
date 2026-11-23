package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SLibrary_ is a FluentQ query type for SLibrary_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SLibrary_ extends fluentq.sql.RelationalPathBase<SLibrary_> {

  private static final long serialVersionUID = 866514828;

  public static final SLibrary_ library_ = new SLibrary_("library_");

  public final NumberPath<Long> identity = createNumber("identity", Long.class);

  public final fluentq.sql.PrimaryKey<SLibrary_> primary = createPrimaryKey(identity);

  public final fluentq.sql.ForeignKey<SBookversion_> _bookversion_LIBRARYIDENTITYFK =
      createInvForeignKey(identity, "LIBRARY_IDENTITY");

  public SLibrary_(String variable) {
    super(SLibrary_.class, forVariable(variable), "null", "library_");
    addMetadata();
  }

  public SLibrary_(String variable, String schema, String table) {
    super(SLibrary_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLibrary_(String variable, String schema) {
    super(SLibrary_.class, forVariable(variable), schema, "library_");
    addMetadata();
  }

  public SLibrary_(Path<? extends SLibrary_> path) {
    super(path.getType(), path.getMetadata(), "null", "library_");
    addMetadata();
  }

  public SLibrary_(PathMetadata metadata) {
    super(SLibrary_.class, metadata, "null", "library_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        identity,
        ColumnMetadata.named("IDENTITY").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
