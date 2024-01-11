package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SOrder_ is a Querydsl query type for SOrder_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SOrder_ extends com.querydsl.sql.RelationalPathBase<SOrder_> {

  private static final long serialVersionUID = 747661657;

  public static final SOrder_ order_ = new SOrder_("order_");

  public final NumberPath<Integer> customerId = createNumber("customerId", Integer.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final BooleanPath paid = createBoolean("paid");

  public final com.querydsl.sql.PrimaryKey<SOrder_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCustomer_> order_CUSTOMERIDFK =
      createForeignKey(customerId, "ID");

  public SOrder_(String variable) {
    super(SOrder_.class, forVariable(variable), "null", "order_");
    addMetadata();
  }

  public SOrder_(String variable, String schema, String table) {
    super(SOrder_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SOrder_(String variable, String schema) {
    super(SOrder_.class, forVariable(variable), schema, "order_");
    addMetadata();
  }

  public SOrder_(Path<? extends SOrder_> path) {
    super(path.getType(), path.getMetadata(), "null", "order_");
    addMetadata();
  }

  public SOrder_(PathMetadata metadata) {
    super(SOrder_.class, metadata, "null", "order_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        customerId,
        ColumnMetadata.named("CUSTOMER_ID").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(paid, ColumnMetadata.named("PAID").withIndex(2).ofType(Types.BIT).withSize(1));
  }
}
