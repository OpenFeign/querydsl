package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPlayerSCORES is a FluentQ query type for SPlayerSCORES */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SPlayerSCORES extends fluentq.sql.RelationalPathBase<SPlayerSCORES> {

  private static final long serialVersionUID = -841435446;

  public static final SPlayerSCORES PlayerSCORES = new SPlayerSCORES("Player_SCORES");

  public final NumberPath<Long> playerID = createNumber("playerID", Long.class);

  public final NumberPath<Integer> scores = createNumber("scores", Integer.class);

  public final fluentq.sql.ForeignKey<SPlayer_> playerSCORESPlayerIDFK =
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
