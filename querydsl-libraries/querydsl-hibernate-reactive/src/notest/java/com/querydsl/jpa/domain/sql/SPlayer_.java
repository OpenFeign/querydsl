package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPlayer_ is a Querydsl query type for SPlayer_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPlayer_ extends com.querydsl.sql.RelationalPathBase<SPlayer_> {

  private static final long serialVersionUID = -1878750186;

  public static final SPlayer_ player_ = new SPlayer_("player_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final com.querydsl.sql.PrimaryKey<SPlayer_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SPlayerSCORES> _playerSCORESPlayerIDFK =
      createInvForeignKey(id, "Player_ID");

  public SPlayer_(String variable) {
    super(SPlayer_.class, forVariable(variable), "null", "player_");
    addMetadata();
  }

  public SPlayer_(String variable, String schema, String table) {
    super(SPlayer_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPlayer_(String variable, String schema) {
    super(SPlayer_.class, forVariable(variable), schema, "player_");
    addMetadata();
  }

  public SPlayer_(Path<? extends SPlayer_> path) {
    super(path.getType(), path.getMetadata(), "null", "player_");
    addMetadata();
  }

  public SPlayer_(PathMetadata metadata) {
    super(SPlayer_.class, metadata, "null", "player_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
