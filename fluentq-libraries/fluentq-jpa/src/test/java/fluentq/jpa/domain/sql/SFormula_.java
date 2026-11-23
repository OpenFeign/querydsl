package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SFormula_ is a FluentQ query type for SFormula_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SFormula_ extends fluentq.sql.RelationalPathBase<SFormula_> {

  private static final long serialVersionUID = 483680321;

  public static final SFormula_ formula_ = new SFormula_("formula_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final NumberPath<Long> parameterId = createNumber("parameterId", Long.class);

  public final fluentq.sql.PrimaryKey<SFormula_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SParameter_> formula_PARAMETERIDFK =
      createForeignKey(parameterId, "ID");

  public SFormula_(String variable) {
    super(SFormula_.class, forVariable(variable), "null", "formula_");
    addMetadata();
  }

  public SFormula_(String variable, String schema, String table) {
    super(SFormula_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SFormula_(String variable, String schema) {
    super(SFormula_.class, forVariable(variable), schema, "formula_");
    addMetadata();
  }

  public SFormula_(Path<? extends SFormula_> path) {
    super(path.getType(), path.getMetadata(), "null", "formula_");
    addMetadata();
  }

  public SFormula_(PathMetadata metadata) {
    super(SFormula_.class, metadata, "null", "formula_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        parameterId,
        ColumnMetadata.named("PARAMETER_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
