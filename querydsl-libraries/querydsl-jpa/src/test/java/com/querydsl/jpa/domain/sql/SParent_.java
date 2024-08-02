package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SParent_ is a Querydsl query type for SParent_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SParent_ extends com.querydsl.sql.RelationalPathBase<SParent_> {

  private static final long serialVersionUID = 2116409197;

  public static final SParent_ parent_ = new SParent_("parent_");

  public final StringPath childname = createString("childname");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SParent_> primary = createPrimaryKey(id);

  public SParent_(String variable) {
    super(SParent_.class, forVariable(variable), "null", "parent_");
    addMetadata();
  }

  public SParent_(String variable, String schema, String table) {
    super(SParent_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SParent_(String variable, String schema) {
    super(SParent_.class, forVariable(variable), schema, "parent_");
    addMetadata();
  }

  public SParent_(Path<? extends SParent_> path) {
    super(path.getType(), path.getMetadata(), "null", "parent_");
    addMetadata();
  }

  public SParent_(PathMetadata metadata) {
    super(SParent_.class, metadata, "null", "parent_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        childname,
        ColumnMetadata.named("CHILDNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
