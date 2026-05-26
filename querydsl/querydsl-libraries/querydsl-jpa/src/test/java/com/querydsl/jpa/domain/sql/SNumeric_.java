package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNumeric_ is a Querydsl query type for SNumeric_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SNumeric_ extends com.querydsl.sql.RelationalPathBase<SNumeric_> {

  private static final long serialVersionUID = -1874277510;

  public static final SNumeric_ numeric_ = new SNumeric_("numeric_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<java.math.BigInteger> value_ =
      createNumber("value_", java.math.BigInteger.class);

  public final com.querydsl.sql.PrimaryKey<SNumeric_> primary = createPrimaryKey(id);

  public SNumeric_(String variable) {
    super(SNumeric_.class, forVariable(variable), "null", "numeric_");
    addMetadata();
  }

  public SNumeric_(String variable, String schema, String table) {
    super(SNumeric_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNumeric_(String variable, String schema) {
    super(SNumeric_.class, forVariable(variable), schema, "numeric_");
    addMetadata();
  }

  public SNumeric_(Path<? extends SNumeric_> path) {
    super(path.getType(), path.getMetadata(), "null", "numeric_");
    addMetadata();
  }

  public SNumeric_(PathMetadata metadata) {
    super(SNumeric_.class, metadata, "null", "numeric_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        value_, ColumnMetadata.named("value_").withIndex(2).ofType(Types.DECIMAL).withSize(38));
  }
}
