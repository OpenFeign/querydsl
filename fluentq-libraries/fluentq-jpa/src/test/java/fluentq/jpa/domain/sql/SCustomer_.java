package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCustomer_ is a FluentQ query type for SCustomer_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SCustomer_ extends fluentq.sql.RelationalPathBase<SCustomer_> {

  private static final long serialVersionUID = 1890945209;

  public static final SCustomer_ customer_ = new SCustomer_("customer_");

  public final NumberPath<Long> currentorderId = createNumber("currentorderId", Long.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final NumberPath<Long> nameId = createNumber("nameId", Long.class);

  public final fluentq.sql.PrimaryKey<SCustomer_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SOrder_> customer_CURRENTORDERIDFK =
      createForeignKey(currentorderId, "ID");

  public final fluentq.sql.ForeignKey<SName_> customer_NAMEIDFK = createForeignKey(nameId, "ID");

  public final fluentq.sql.ForeignKey<SOrder_> _order_CUSTOMERIDFK =
      createInvForeignKey(id, "CUSTOMER_ID");

  public final fluentq.sql.ForeignKey<SStore_customer_> _store_customer_customersIDFK =
      createInvForeignKey(id, "customers_ID");

  public SCustomer_(String variable) {
    super(SCustomer_.class, forVariable(variable), "null", "customer_");
    addMetadata();
  }

  public SCustomer_(String variable, String schema, String table) {
    super(SCustomer_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCustomer_(String variable, String schema) {
    super(SCustomer_.class, forVariable(variable), schema, "customer_");
    addMetadata();
  }

  public SCustomer_(Path<? extends SCustomer_> path) {
    super(path.getType(), path.getMetadata(), "null", "customer_");
    addMetadata();
  }

  public SCustomer_(PathMetadata metadata) {
    super(SCustomer_.class, metadata, "null", "customer_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        currentorderId,
        ColumnMetadata.named("CURRENTORDER_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        nameId, ColumnMetadata.named("NAME_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
  }
}
