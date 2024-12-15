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

/** SBookBookmarks is a Querydsl query type for SBookBookmarks */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBookBookmarks extends com.querydsl.sql.RelationalPathBase<SBookBookmarks> {

  private static final long serialVersionUID = 1405496716;

  public static final SBookBookmarks bookBookmarks = new SBookBookmarks("book_bookmarks");

  public final NumberPath<Long> bookidIdentity = createNumber("bookidIdentity", Long.class);

  public final NumberPath<Integer> bookMarksORDER = createNumber("bookMarksORDER", Integer.class);

  public final StringPath comment = createString("comment");

  public final NumberPath<Long> libraryIdentity = createNumber("libraryIdentity", Long.class);

  public final NumberPath<Long> page = createNumber("page", Long.class);

  public final com.querydsl.sql.ForeignKey<SBookversion_> bookBookmarksBOOKIDIDENTITYFK =
      createForeignKey(
          Arrays.asList(bookidIdentity, libraryIdentity),
          Arrays.asList("BOOKID_IDENTITY", "LIBRARY_IDENTITY"));

  public SBookBookmarks(String variable) {
    super(SBookBookmarks.class, forVariable(variable), "null", "book_bookmarks");
    addMetadata();
  }

  public SBookBookmarks(String variable, String schema, String table) {
    super(SBookBookmarks.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBookBookmarks(String variable, String schema) {
    super(SBookBookmarks.class, forVariable(variable), schema, "book_bookmarks");
    addMetadata();
  }

  public SBookBookmarks(Path<? extends SBookBookmarks> path) {
    super(path.getType(), path.getMetadata(), "null", "book_bookmarks");
    addMetadata();
  }

  public SBookBookmarks(PathMetadata metadata) {
    super(SBookBookmarks.class, metadata, "null", "book_bookmarks");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        bookidIdentity,
        ColumnMetadata.named("BOOKID_IDENTITY").withIndex(4).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        bookMarksORDER,
        ColumnMetadata.named("bookMarks_ORDER").withIndex(5).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        comment, ColumnMetadata.named("COMMENT").withIndex(1).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        libraryIdentity,
        ColumnMetadata.named("LIBRARY_IDENTITY").withIndex(3).ofType(Types.BIGINT).withSize(19));
    addMetadata(page, ColumnMetadata.named("PAGE").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
