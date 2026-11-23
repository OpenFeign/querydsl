package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SEntity1 is a FluentQ query type for SEntity1 */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SEntity1 extends fluentq.sql.RelationalPathBase<SEntity1> {

  private static final long serialVersionUID = 1317954342;

  public static final SEntity1 entity1 = new SEntity1("ENTITY1");

  public final StringPath dtype = createString("dtype");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath property = createString("property");

  public final StringPath property2 = createString("property2");

  public final fluentq.sql.PrimaryKey<SEntity1> primary = createPrimaryKey(id);

  public SEntity1(String variable) {
    super(SEntity1.class, forVariable(variable), "null", "ENTITY1");
    addMetadata();
  }

  public SEntity1(String variable, String schema, String table) {
    super(SEntity1.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEntity1(String variable, String schema) {
    super(SEntity1.class, forVariable(variable), schema, "ENTITY1");
    addMetadata();
  }

  public SEntity1(Path<? extends SEntity1> path) {
    super(path.getType(), path.getMetadata(), "null", "ENTITY1");
    addMetadata();
  }

  public SEntity1(PathMetadata metadata) {
    super(SEntity1.class, metadata, "null", "ENTITY1");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        dtype, ColumnMetadata.named("DTYPE").withIndex(2).ofType(Types.VARCHAR).withSize(31));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        property,
        ColumnMetadata.named("PROPERTY").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        property2,
        ColumnMetadata.named("PROPERTY2").withIndex(4).ofType(Types.VARCHAR).withSize(255));
  }
}
