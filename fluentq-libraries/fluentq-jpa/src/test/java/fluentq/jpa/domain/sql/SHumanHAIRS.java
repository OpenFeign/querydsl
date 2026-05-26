package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SHumanHAIRS is a FluentQ query type for SHumanHAIRS */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SHumanHAIRS extends fluentq.sql.RelationalPathBase<SHumanHAIRS> {

  private static final long serialVersionUID = 188619308;

  public static final SHumanHAIRS HumanHAIRS = new SHumanHAIRS("Human_HAIRS");

  public final NumberPath<Integer> hairs = createNumber("hairs", Integer.class);

  public final NumberPath<Long> humanID = createNumber("humanID", Long.class);

  public final fluentq.sql.ForeignKey<SMammal> humanHAIRSHumanIDFK =
      createForeignKey(humanID, "ID");

  public SHumanHAIRS(String variable) {
    super(SHumanHAIRS.class, forVariable(variable), "null", "Human_HAIRS");
    addMetadata();
  }

  public SHumanHAIRS(String variable, String schema, String table) {
    super(SHumanHAIRS.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SHumanHAIRS(String variable, String schema) {
    super(SHumanHAIRS.class, forVariable(variable), schema, "Human_HAIRS");
    addMetadata();
  }

  public SHumanHAIRS(Path<? extends SHumanHAIRS> path) {
    super(path.getType(), path.getMetadata(), "null", "Human_HAIRS");
    addMetadata();
  }

  public SHumanHAIRS(PathMetadata metadata) {
    super(SHumanHAIRS.class, metadata, "null", "Human_HAIRS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        hairs, ColumnMetadata.named("HAIRS").withIndex(2).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        humanID, ColumnMetadata.named("Human_ID").withIndex(1).ofType(Types.BIGINT).withSize(19));
  }
}
