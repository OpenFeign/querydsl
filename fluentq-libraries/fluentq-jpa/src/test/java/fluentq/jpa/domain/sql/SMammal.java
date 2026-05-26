package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SMammal is a FluentQ query type for SMammal */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SMammal extends fluentq.sql.RelationalPathBase<SMammal> {

  private static final long serialVersionUID = 674978791;

  public static final SMammal mammal = new SMammal("MAMMAL");

  public final StringPath dtype = createString("dtype");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final fluentq.sql.PrimaryKey<SMammal> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SHumanHAIRS> _humanHAIRSHumanIDFK =
      createInvForeignKey(id, "Human_ID");

  public final fluentq.sql.ForeignKey<SWorldMammal> _wORLDMAMMALMammalsIDFK =
      createInvForeignKey(id, "mammals_ID");

  public SMammal(String variable) {
    super(SMammal.class, forVariable(variable), "null", "MAMMAL");
    addMetadata();
  }

  public SMammal(String variable, String schema, String table) {
    super(SMammal.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SMammal(String variable, String schema) {
    super(SMammal.class, forVariable(variable), schema, "MAMMAL");
    addMetadata();
  }

  public SMammal(Path<? extends SMammal> path) {
    super(path.getType(), path.getMetadata(), "null", "MAMMAL");
    addMetadata();
  }

  public SMammal(PathMetadata metadata) {
    super(SMammal.class, metadata, "null", "MAMMAL");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        dtype, ColumnMetadata.named("DTYPE").withIndex(2).ofType(Types.VARCHAR).withSize(31));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
