package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SFooNames is a Querydsl query type for SFooNames */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SFooNames extends com.querydsl.sql.RelationalPathBase<SFooNames> {

  private static final long serialVersionUID = 368569002;

  public static final SFooNames fooNames = new SFooNames("foo_names");

  public final NumberPath<Integer> fooId = createNumber("fooId", Integer.class);

  public final StringPath names = createString("names");

  public final com.querydsl.sql.ForeignKey<SFoo_> fooNamesFooIdFK = createForeignKey(fooId, "ID");

  public SFooNames(String variable) {
    super(SFooNames.class, forVariable(variable), "null", "foo_names");
    addMetadata();
  }

  public SFooNames(String variable, String schema, String table) {
    super(SFooNames.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SFooNames(String variable, String schema) {
    super(SFooNames.class, forVariable(variable), schema, "foo_names");
    addMetadata();
  }

  public SFooNames(Path<? extends SFooNames> path) {
    super(path.getType(), path.getMetadata(), "null", "foo_names");
    addMetadata();
  }

  public SFooNames(PathMetadata metadata) {
    super(SFooNames.class, metadata, "null", "foo_names");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        fooId, ColumnMetadata.named("foo_id").withIndex(1).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        names, ColumnMetadata.named("NAMES").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
