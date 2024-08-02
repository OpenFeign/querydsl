package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SMammal is a Querydsl query type for SMammal */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SMammal extends com.querydsl.sql.RelationalPathBase<SMammal> {

  private static final long serialVersionUID = 674978791;

  public static final SMammal mammal = new SMammal("MAMMAL");

  public final StringPath dtype = createString("dtype");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final com.querydsl.sql.PrimaryKey<SMammal> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SHumanHAIRS> _humanHAIRSHumanIDFK =
      createInvForeignKey(id, "Human_ID");

  public final com.querydsl.sql.ForeignKey<SWorldMammal> _wORLDMAMMALMammalsIDFK =
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
