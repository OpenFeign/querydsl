package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SPlayerScores is a Querydsl query type for SPlayerScores */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPlayerScores extends com.querydsl.sql.RelationalPathBase<SPlayerScores> {

  private static final long serialVersionUID = -810897686;

  public static final SPlayerScores playerScores = new SPlayerScores("player_scores");

  public SPlayerScores(String variable) {
    super(SPlayerScores.class, forVariable(variable), "null", "player_scores");
    addMetadata();
  }

  public SPlayerScores(String variable, String schema, String table) {
    super(SPlayerScores.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPlayerScores(String variable, String schema) {
    super(SPlayerScores.class, forVariable(variable), schema, "player_scores");
    addMetadata();
  }

  public SPlayerScores(Path<? extends SPlayerScores> path) {
    super(path.getType(), path.getMetadata(), "null", "player_scores");
    addMetadata();
  }

  public SPlayerScores(PathMetadata metadata) {
    super(SPlayerScores.class, metadata, "null", "player_scores");
    addMetadata();
  }

  public void addMetadata() {}
}
