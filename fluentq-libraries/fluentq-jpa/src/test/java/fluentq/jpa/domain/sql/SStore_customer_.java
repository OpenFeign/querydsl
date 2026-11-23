package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SStore_customer_ is a FluentQ query type for SStore_customer_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SStore_customer_ extends fluentq.sql.RelationalPathBase<SStore_customer_> {

  private static final long serialVersionUID = -1583391525;

  public static final SStore_customer_ store_customer_ = new SStore_customer_("store__customer_");

  public final NumberPath<Integer> customersID = createNumber("customersID", Integer.class);

  public final NumberPath<Long> storeID = createNumber("storeID", Long.class);

  public final fluentq.sql.PrimaryKey<SStore_customer_> primary =
      createPrimaryKey(storeID, customersID);

  public final fluentq.sql.ForeignKey<SStore_> store_customer_StoreIDFK =
      createForeignKey(storeID, "ID");

  public final fluentq.sql.ForeignKey<SCustomer_> store_customer_customersIDFK =
      createForeignKey(customersID, "ID");

  public SStore_customer_(String variable) {
    super(SStore_customer_.class, forVariable(variable), "null", "store__customer_");
    addMetadata();
  }

  public SStore_customer_(String variable, String schema, String table) {
    super(SStore_customer_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SStore_customer_(String variable, String schema) {
    super(SStore_customer_.class, forVariable(variable), schema, "store__customer_");
    addMetadata();
  }

  public SStore_customer_(Path<? extends SStore_customer_> path) {
    super(path.getType(), path.getMetadata(), "null", "store__customer_");
    addMetadata();
  }

  public SStore_customer_(PathMetadata metadata) {
    super(SStore_customer_.class, metadata, "null", "store__customer_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        customersID,
        ColumnMetadata.named("customers_ID")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
    addMetadata(
        storeID,
        ColumnMetadata.named("Store_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
