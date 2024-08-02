package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDocument_ is a Querydsl query type for SDocument_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SDocument_ extends com.querydsl.sql.RelationalPathBase<SDocument_> {

  private static final long serialVersionUID = 1222926108;

  public static final SDocument_ document_ = new SDocument_("document_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final DatePath<java.sql.Date> validto = createDate("validto", java.sql.Date.class);

  public final com.querydsl.sql.PrimaryKey<SDocument_> primary = createPrimaryKey(id);

  public SDocument_(String variable) {
    super(SDocument_.class, forVariable(variable), "null", "document_");
    addMetadata();
  }

  public SDocument_(String variable, String schema, String table) {
    super(SDocument_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDocument_(String variable, String schema) {
    super(SDocument_.class, forVariable(variable), schema, "document_");
    addMetadata();
  }

  public SDocument_(Path<? extends SDocument_> path) {
    super(path.getType(), path.getMetadata(), "null", "document_");
    addMetadata();
  }

  public SDocument_(PathMetadata metadata) {
    super(SDocument_.class, metadata, "null", "document_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        validto, ColumnMetadata.named("VALIDTO").withIndex(3).ofType(Types.DATE).withSize(10));
  }
}
