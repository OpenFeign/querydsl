package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPlayerSCORES is a Querydsl query type for SPlayerSCORES */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPlayerSCORES extends com.querydsl.sql.RelationalPathBase<SPlayerSCORES> {

  private static final long serialVersionUID = -841435446;

  public static final SPlayerSCORES PlayerSCORES = new SPlayerSCORES("Player_SCORES");

  public final NumberPath<Long> playerID = createNumber("playerID", Long.class);

  public final NumberPath<Integer> scores = createNumber("scores", Integer.class);

  public final com.querydsl.sql.ForeignKey<SPlayer_> playerSCORESPlayerIDFK =
      createForeignKey(playerID, "ID");

  public SPlayerSCORES(String variable) {
    super(SPlayerSCORES.class, forVariable(variable), "null", "Player_SCORES");
    addMetadata();
  }

  public SPlayerSCORES(String variable, String schema, String table) {
    super(SPlayerSCORES.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPlayerSCORES(String variable, String schema) {
    super(SPlayerSCORES.class, forVariable(variable), schema, "Player_SCORES");
    addMetadata();
  }

  public SPlayerSCORES(Path<? extends SPlayerSCORES> path) {
    super(path.getType(), path.getMetadata(), "null", "Player_SCORES");
    addMetadata();
  }

  public SPlayerSCORES(PathMetadata metadata) {
    super(SPlayerSCORES.class, metadata, "null", "Player_SCORES");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        playerID, ColumnMetadata.named("Player_ID").withIndex(1).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        scores, ColumnMetadata.named("SCORES").withIndex(2).ofType(Types.INTEGER).withSize(10));
  }
}
