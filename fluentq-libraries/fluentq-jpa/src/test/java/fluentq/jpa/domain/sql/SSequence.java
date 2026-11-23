package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SSequence is a FluentQ query type for SSequence */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SSequence extends fluentq.sql.RelationalPathBase<SSequence> {

  private static final long serialVersionUID = 1358560329;

  public static final SSequence sequence = new SSequence("SEQUENCE");

  public final NumberPath<java.math.BigInteger> seqCount =
      createNumber("seqCount", java.math.BigInteger.class);

  public final StringPath seqName = createString("seqName");

  public final fluentq.sql.PrimaryKey<SSequence> primary = createPrimaryKey(seqName);

  public SSequence(String variable) {
    super(SSequence.class, forVariable(variable), "null", "SEQUENCE");
    addMetadata();
  }

  public SSequence(String variable, String schema, String table) {
    super(SSequence.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SSequence(String variable, String schema) {
    super(SSequence.class, forVariable(variable), schema, "SEQUENCE");
    addMetadata();
  }

  public SSequence(Path<? extends SSequence> path) {
    super(path.getType(), path.getMetadata(), "null", "SEQUENCE");
    addMetadata();
  }

  public SSequence(PathMetadata metadata) {
    super(SSequence.class, metadata, "null", "SEQUENCE");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        seqCount,
        ColumnMetadata.named("SEQ_COUNT").withIndex(2).ofType(Types.DECIMAL).withSize(38));
    addMetadata(
        seqName,
        ColumnMetadata.named("SEQ_NAME").withIndex(1).ofType(Types.VARCHAR).withSize(50).notNull());
  }
}
