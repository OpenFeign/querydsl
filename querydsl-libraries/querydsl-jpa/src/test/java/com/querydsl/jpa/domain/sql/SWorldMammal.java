package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SWorldMammal is a Querydsl query type for SWorldMammal */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SWorldMammal extends com.querydsl.sql.RelationalPathBase<SWorldMammal> {

  private static final long serialVersionUID = -1315719671;

  public static final SWorldMammal worldMammal = new SWorldMammal("WORLD_MAMMAL");

  public final NumberPath<Long> mammalsID = createNumber("mammalsID", Long.class);

  public final NumberPath<Long> worldID = createNumber("worldID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SWorldMammal> primary =
      createPrimaryKey(worldID, mammalsID);

  public final com.querydsl.sql.ForeignKey<SWorld> wORLDMAMMALWorldIDFK =
      createForeignKey(worldID, "ID");

  public final com.querydsl.sql.ForeignKey<SMammal> wORLDMAMMALMammalsIDFK =
      createForeignKey(mammalsID, "ID");

  public SWorldMammal(String variable) {
    super(SWorldMammal.class, forVariable(variable), "null", "WORLD_MAMMAL");
    addMetadata();
  }

  public SWorldMammal(String variable, String schema, String table) {
    super(SWorldMammal.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SWorldMammal(String variable, String schema) {
    super(SWorldMammal.class, forVariable(variable), schema, "WORLD_MAMMAL");
    addMetadata();
  }

  public SWorldMammal(Path<? extends SWorldMammal> path) {
    super(path.getType(), path.getMetadata(), "null", "WORLD_MAMMAL");
    addMetadata();
  }

  public SWorldMammal(PathMetadata metadata) {
    super(SWorldMammal.class, metadata, "null", "WORLD_MAMMAL");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        mammalsID,
        ColumnMetadata.named("mammals_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        worldID,
        ColumnMetadata.named("World_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
