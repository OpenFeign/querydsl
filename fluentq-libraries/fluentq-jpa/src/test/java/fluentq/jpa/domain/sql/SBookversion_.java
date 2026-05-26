package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import java.util.Arrays;
import javax.annotation.processing.Generated;

/** SBookversion_ is a FluentQ query type for SBookversion_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SBookversion_ extends fluentq.sql.RelationalPathBase<SBookversion_> {

  private static final long serialVersionUID = -823148296;

  public static final SBookversion_ bookversion_ = new SBookversion_("bookversion_");

  public final NumberPath<Long> bookidIdentity = createNumber("bookidIdentity", Long.class);

  public final StringPath description = createString("description");

  public final NumberPath<Long> libraryIdentity = createNumber("libraryIdentity", Long.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SBookversion_> primary =
      createPrimaryKey(bookidIdentity, libraryIdentity);

  public final fluentq.sql.ForeignKey<SBookid_> bookversion_BOOKIDIDENTITYFK =
      createForeignKey(bookidIdentity, "IDENTITY");

  public final fluentq.sql.ForeignKey<SLibrary_> bookversion_LIBRARYIDENTITYFK =
      createForeignKey(libraryIdentity, "IDENTITY");

  public final fluentq.sql.ForeignKey<SBookBookmarks> _bookBookmarksBOOKIDIDENTITYFK =
      createInvForeignKey(
          Arrays.asList(bookidIdentity, libraryIdentity),
          Arrays.asList("BOOKID_IDENTITY", "LIBRARY_IDENTITY"));

  public SBookversion_(String variable) {
    super(SBookversion_.class, forVariable(variable), "null", "bookversion_");
    addMetadata();
  }

  public SBookversion_(String variable, String schema, String table) {
    super(SBookversion_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBookversion_(String variable, String schema) {
    super(SBookversion_.class, forVariable(variable), schema, "bookversion_");
    addMetadata();
  }

  public SBookversion_(Path<? extends SBookversion_> path) {
    super(path.getType(), path.getMetadata(), "null", "bookversion_");
    addMetadata();
  }

  public SBookversion_(PathMetadata metadata) {
    super(SBookversion_.class, metadata, "null", "bookversion_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        bookidIdentity,
        ColumnMetadata.named("BOOKID_IDENTITY")
            .withIndex(3)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        description,
        ColumnMetadata.named("DESCRIPTION").withIndex(1).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        libraryIdentity,
        ColumnMetadata.named("LIBRARY_IDENTITY")
            .withIndex(4)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
