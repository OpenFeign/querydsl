package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SParameter_ is a Querydsl query type for SParameter_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SParameter_ extends com.querydsl.sql.RelationalPathBase<SParameter_> {

  private static final long serialVersionUID = 529679454;

  public static final SParameter_ parameter_ = new SParameter_("parameter_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final com.querydsl.sql.PrimaryKey<SParameter_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SFormula_> _formula_PARAMETERIDFK =
      createInvForeignKey(id, "PARAMETER_ID");

  public SParameter_(String variable) {
    super(SParameter_.class, forVariable(variable), "null", "parameter_");
    addMetadata();
  }

  public SParameter_(String variable, String schema, String table) {
    super(SParameter_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SParameter_(String variable, String schema) {
    super(SParameter_.class, forVariable(variable), schema, "parameter_");
    addMetadata();
  }

  public SParameter_(Path<? extends SParameter_> path) {
    super(path.getType(), path.getMetadata(), "null", "parameter_");
    addMetadata();
  }

  public SParameter_(PathMetadata metadata) {
    super(SParameter_.class, metadata, "null", "parameter_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
