package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SWorld is a Querydsl query type for SWorld */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SWorld extends com.querydsl.sql.RelationalPathBase<SWorld> {

  private static final long serialVersionUID = -938400758;

  public static final SWorld world = new SWorld("WORLD");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final com.querydsl.sql.PrimaryKey<SWorld> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SWorldMammal> _wORLDMAMMALWorldIDFK =
      createInvForeignKey(id, "World_ID");

  public SWorld(String variable) {
    super(SWorld.class, forVariable(variable), "null", "WORLD");
    addMetadata();
  }

  public SWorld(String variable, String schema, String table) {
    super(SWorld.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SWorld(String variable, String schema) {
    super(SWorld.class, forVariable(variable), schema, "WORLD");
    addMetadata();
  }

  public SWorld(Path<? extends SWorld> path) {
    super(path.getType(), path.getMetadata(), "null", "WORLD");
    addMetadata();
  }

  public SWorld(PathMetadata metadata) {
    super(SWorld.class, metadata, "null", "WORLD");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
