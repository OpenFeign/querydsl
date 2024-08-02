package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import java.util.Arrays;
import javax.annotation.processing.Generated;

/** SBookversion_ is a Querydsl query type for SBookversion_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBookversion_ extends com.querydsl.sql.RelationalPathBase<SBookversion_> {

  private static final long serialVersionUID = -823148296;

  public static final SBookversion_ bookversion_ = new SBookversion_("bookversion_");

  public final NumberPath<Long> bookidIdentity = createNumber("bookidIdentity", Long.class);

  public final StringPath description = createString("description");

  public final NumberPath<Long> libraryIdentity = createNumber("libraryIdentity", Long.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SBookversion_> primary =
      createPrimaryKey(bookidIdentity, libraryIdentity);

  public final com.querydsl.sql.ForeignKey<SBookid_> bookversion_BOOKIDIDENTITYFK =
      createForeignKey(bookidIdentity, "IDENTITY");

  public final com.querydsl.sql.ForeignKey<SLibrary_> bookversion_LIBRARYIDENTITYFK =
      createForeignKey(libraryIdentity, "IDENTITY");

  public final com.querydsl.sql.ForeignKey<SBookBookmarks> _bookBookmarksBOOKIDIDENTITYFK =
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
