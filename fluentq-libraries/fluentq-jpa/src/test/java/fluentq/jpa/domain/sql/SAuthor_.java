package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SAuthor_ is a FluentQ query type for SAuthor_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SAuthor_ extends fluentq.sql.RelationalPathBase<SAuthor_> {

  private static final long serialVersionUID = -2031691092;

  public static final SAuthor_ author_ = new SAuthor_("author_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SAuthor_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SBook_> _book_AUTHORIDFK =
      createInvForeignKey(id, "AUTHOR_ID");

  public SAuthor_(String variable) {
    super(SAuthor_.class, forVariable(variable), "null", "author_");
    addMetadata();
  }

  public SAuthor_(String variable, String schema, String table) {
    super(SAuthor_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SAuthor_(String variable, String schema) {
    super(SAuthor_.class, forVariable(variable), schema, "author_");
    addMetadata();
  }

  public SAuthor_(Path<? extends SAuthor_> path) {
    super(path.getType(), path.getMetadata(), "null", "author_");
    addMetadata();
  }

  public SAuthor_(PathMetadata metadata) {
    super(SAuthor_.class, metadata, "null", "author_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
