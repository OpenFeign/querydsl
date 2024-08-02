package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SGeneratedKeys is a Querydsl query type for SGeneratedKeys */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SGeneratedKeys extends com.querydsl.sql.RelationalPathBase<SGeneratedKeys> {

  private static final long serialVersionUID = 2097474715;

  public static final SGeneratedKeys generatedKeys = new SGeneratedKeys("GENERATED_KEYS");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SGeneratedKeys> primary = createPrimaryKey(id);

  public SGeneratedKeys(String variable) {
    super(SGeneratedKeys.class, forVariable(variable), "null", "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(String variable, String schema, String table) {
    super(SGeneratedKeys.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SGeneratedKeys(String variable, String schema) {
    super(SGeneratedKeys.class, forVariable(variable), schema, "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(Path<? extends SGeneratedKeys> path) {
    super(path.getType(), path.getMetadata(), "null", "GENERATED_KEYS");
    addMetadata();
  }

  public SGeneratedKeys(PathMetadata metadata) {
    super(SGeneratedKeys.class, metadata, "null", "GENERATED_KEYS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(30));
  }
}
