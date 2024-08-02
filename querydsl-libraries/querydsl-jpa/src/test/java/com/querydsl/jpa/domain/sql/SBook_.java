package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SBook_ is a Querydsl query type for SBook_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBook_ extends com.querydsl.sql.RelationalPathBase<SBook_> {

  private static final long serialVersionUID = -957797618;

  public static final SBook_ book_ = new SBook_("book_");

  public final NumberPath<Long> authorId = createNumber("authorId", Long.class);

  public final StringPath dtype = createString("dtype");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath title = createString("title");

  public final com.querydsl.sql.PrimaryKey<SBook_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SAuthor_> book_AUTHORIDFK =
      createForeignKey(authorId, "ID");

  public SBook_(String variable) {
    super(SBook_.class, forVariable(variable), "null", "book_");
    addMetadata();
  }

  public SBook_(String variable, String schema, String table) {
    super(SBook_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBook_(String variable, String schema) {
    super(SBook_.class, forVariable(variable), schema, "book_");
    addMetadata();
  }

  public SBook_(Path<? extends SBook_> path) {
    super(path.getType(), path.getMetadata(), "null", "book_");
    addMetadata();
  }

  public SBook_(PathMetadata metadata) {
    super(SBook_.class, metadata, "null", "book_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        authorId, ColumnMetadata.named("AUTHOR_ID").withIndex(4).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        dtype, ColumnMetadata.named("DTYPE").withIndex(2).ofType(Types.VARCHAR).withSize(31));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        title, ColumnMetadata.named("TITLE").withIndex(3).ofType(Types.VARCHAR).withSize(255));
  }
}
