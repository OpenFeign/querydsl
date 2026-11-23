package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SBookid_ is a FluentQ query type for SBookid_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SBookid_ extends fluentq.sql.RelationalPathBase<SBookid_> {

  private static final long serialVersionUID = -1320496749;

  public static final SBookid_ bookid_ = new SBookid_("bookid_");

  public final NumberPath<Long> identity = createNumber("identity", Long.class);

  public final fluentq.sql.PrimaryKey<SBookid_> primary = createPrimaryKey(identity);

  public final fluentq.sql.ForeignKey<SBookversion_> _bookversion_BOOKIDIDENTITYFK =
      createInvForeignKey(identity, "BOOKID_IDENTITY");

  public SBookid_(String variable) {
    super(SBookid_.class, forVariable(variable), "null", "bookid_");
    addMetadata();
  }

  public SBookid_(String variable, String schema, String table) {
    super(SBookid_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBookid_(String variable, String schema) {
    super(SBookid_.class, forVariable(variable), schema, "bookid_");
    addMetadata();
  }

  public SBookid_(Path<? extends SBookid_> path) {
    super(path.getType(), path.getMetadata(), "null", "bookid_");
    addMetadata();
  }

  public SBookid_(PathMetadata metadata) {
    super(SBookid_.class, metadata, "null", "bookid_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        identity,
        ColumnMetadata.named("IDENTITY").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
