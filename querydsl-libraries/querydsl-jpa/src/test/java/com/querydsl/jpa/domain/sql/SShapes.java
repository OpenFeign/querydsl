package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SShapes is a Querydsl query type for SShapes */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SShapes extends com.querydsl.sql.RelationalPathBase<SShapes> {

  private static final long serialVersionUID = 852863866;

  public static final SShapes shapes = new SShapes("SHAPES");

  public final SimplePath<byte[]> geometry = createSimple("geometry", byte[].class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SShapes> primary = createPrimaryKey(id);

  public SShapes(String variable) {
    super(SShapes.class, forVariable(variable), "null", "SHAPES");
    addMetadata();
  }

  public SShapes(String variable, String schema, String table) {
    super(SShapes.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SShapes(String variable, String schema) {
    super(SShapes.class, forVariable(variable), schema, "SHAPES");
    addMetadata();
  }

  public SShapes(Path<? extends SShapes> path) {
    super(path.getType(), path.getMetadata(), "null", "SHAPES");
    addMetadata();
  }

  public SShapes(PathMetadata metadata) {
    super(SShapes.class, metadata, "null", "SHAPES");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        geometry,
        ColumnMetadata.named("GEOMETRY").withIndex(2).ofType(Types.BINARY).withSize(65535));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
