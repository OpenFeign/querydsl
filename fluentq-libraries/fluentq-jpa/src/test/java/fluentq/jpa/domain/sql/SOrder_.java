package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BooleanPath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SOrder_ is a FluentQ query type for SOrder_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SOrder_ extends fluentq.sql.RelationalPathBase<SOrder_> {

  private static final long serialVersionUID = 747661657;

  public static final SOrder_ order_ = new SOrder_("order_");

  public final NumberPath<Integer> customerId = createNumber("customerId", Integer.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final BooleanPath paid = createBoolean("paid");

  public final fluentq.sql.PrimaryKey<SOrder_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCustomer_> order_CUSTOMERIDFK =
      createForeignKey(customerId, "ID");

  public final fluentq.sql.ForeignKey<SLineItems2> _lineItems2OrderIDFK =
      createInvForeignKey(id, "Order_ID");

  public final fluentq.sql.ForeignKey<SLineItems> _lineItemsOrderIDFK =
      createInvForeignKey(id, "Order_ID");

  public final fluentq.sql.ForeignKey<SOrderDELIVEREDITEMINDICES>
      _orderDELIVEREDITEMINDICESOrderIDFK = createInvForeignKey(id, "Order_ID");

  public final fluentq.sql.ForeignKey<SCustomer_> _customer_CURRENTORDERIDFK =
      createInvForeignKey(id, "CURRENTORDER_ID");

  public final fluentq.sql.ForeignKey<SOrder_item_> _order_item_OrderIDFK =
      createInvForeignKey(id, "Order_ID");

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
