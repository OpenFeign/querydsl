package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DatePath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCatalog_ is a FluentQ query type for SCatalog_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SCatalog_ extends fluentq.sql.RelationalPathBase<SCatalog_> {

  private static final long serialVersionUID = 55977966;

  public static final SCatalog_ catalog_ = new SCatalog_("catalog_");

  public final DatePath<java.sql.Date> effectivedate =
      createDate("effectivedate", java.sql.Date.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final fluentq.sql.PrimaryKey<SCatalog_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCatalog_price_> _catalog_price_CatalogIDFK =
      createInvForeignKey(id, "Catalog_ID");

  public SCatalog_(String variable) {
    super(SCatalog_.class, forVariable(variable), "null", "catalog_");
    addMetadata();
  }

  public SCatalog_(String variable, String schema, String table) {
    super(SCatalog_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCatalog_(String variable, String schema) {
    super(SCatalog_.class, forVariable(variable), schema, "catalog_");
    addMetadata();
  }

  public SCatalog_(Path<? extends SCatalog_> path) {
    super(path.getType(), path.getMetadata(), "null", "catalog_");
    addMetadata();
  }

  public SCatalog_(PathMetadata metadata) {
    super(SCatalog_.class, metadata, "null", "catalog_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        effectivedate,
        ColumnMetadata.named("EFFECTIVEDATE").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
