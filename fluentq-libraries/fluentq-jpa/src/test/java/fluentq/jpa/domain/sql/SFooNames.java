package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SFooNames is a FluentQ query type for SFooNames */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SFooNames extends fluentq.sql.RelationalPathBase<SFooNames> {

  private static final long serialVersionUID = 368569002;

  public static final SFooNames fooNames = new SFooNames("foo_names");

  public final NumberPath<Integer> fooId = createNumber("fooId", Integer.class);

  public final StringPath names = createString("names");

  public final fluentq.sql.ForeignKey<SFoo_> fooNamesFooIdFK = createForeignKey(fooId, "ID");

  public SFooNames(String variable) {
    super(SFooNames.class, forVariable(variable), "null", "foo_names");
    addMetadata();
  }

  public SFooNames(String variable, String schema, String table) {
    super(SFooNames.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SFooNames(String variable, String schema) {
    super(SFooNames.class, forVariable(variable), schema, "foo_names");
    addMetadata();
  }

  public SFooNames(Path<? extends SFooNames> path) {
    super(path.getType(), path.getMetadata(), "null", "foo_names");
    addMetadata();
  }

  public SFooNames(PathMetadata metadata) {
    super(SFooNames.class, metadata, "null", "foo_names");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        fooId, ColumnMetadata.named("foo_id").withIndex(1).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        names, ColumnMetadata.named("NAMES").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
