package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SOrderDELIVEREDITEMINDICES is a Querydsl query type for SOrderDELIVEREDITEMINDICES */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SOrderDELIVEREDITEMINDICES
    extends com.querydsl.sql.RelationalPathBase<SOrderDELIVEREDITEMINDICES> {

  private static final long serialVersionUID = 1275589366;

  public static final SOrderDELIVEREDITEMINDICES OrderDELIVEREDITEMINDICES =
      new SOrderDELIVEREDITEMINDICES("Order_DELIVEREDITEMINDICES");

  public final NumberPath<Integer> _index = createNumber("_index", Integer.class);

  public final NumberPath<Integer> delivereditemindices =
      createNumber("delivereditemindices", Integer.class);

  public final NumberPath<Long> orderID = createNumber("orderID", Long.class);

  public final com.querydsl.sql.ForeignKey<SOrder_> orderDELIVEREDITEMINDICESOrderIDFK =
      createForeignKey(orderID, "ID");

  public SOrderDELIVEREDITEMINDICES(String variable) {
    super(
        SOrderDELIVEREDITEMINDICES.class,
        forVariable(variable),
        "null",
        "Order_DELIVEREDITEMINDICES");
    addMetadata();
  }

  public SOrderDELIVEREDITEMINDICES(String variable, String schema, String table) {
    super(SOrderDELIVEREDITEMINDICES.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SOrderDELIVEREDITEMINDICES(String variable, String schema) {
    super(
        SOrderDELIVEREDITEMINDICES.class,
        forVariable(variable),
        schema,
        "Order_DELIVEREDITEMINDICES");
    addMetadata();
  }

  public SOrderDELIVEREDITEMINDICES(Path<? extends SOrderDELIVEREDITEMINDICES> path) {
    super(path.getType(), path.getMetadata(), "null", "Order_DELIVEREDITEMINDICES");
    addMetadata();
  }

  public SOrderDELIVEREDITEMINDICES(PathMetadata metadata) {
    super(SOrderDELIVEREDITEMINDICES.class, metadata, "null", "Order_DELIVEREDITEMINDICES");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        _index, ColumnMetadata.named("_index").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        delivereditemindices,
        ColumnMetadata.named("DELIVEREDITEMINDICES")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10));
    addMetadata(
        orderID, ColumnMetadata.named("Order_ID").withIndex(1).ofType(Types.BIGINT).withSize(19));
  }
}
