package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SUserprop_category_ is a FluentQ query type for SUserprop_category_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SUserprop_category_ extends fluentq.sql.RelationalPathBase<SUserprop_category_> {

  private static final long serialVersionUID = 1245464664;

  public static final SUserprop_category_ userprop_category_ =
      new SUserprop_category_("userprop__category_");

  public final NumberPath<Long> childCategoriesID = createNumber("childCategoriesID", Long.class);

  public final NumberPath<Long> userPropID = createNumber("userPropID", Long.class);

  public final fluentq.sql.PrimaryKey<SUserprop_category_> primary =
      createPrimaryKey(userPropID, childCategoriesID);

  public final fluentq.sql.ForeignKey<SUserprop_> userprop_category_UserPropIDFK =
      createForeignKey(userPropID, "ID");

  public final fluentq.sql.ForeignKey<SCategory_> userprop_category_childCategoriesIDFK =
      createForeignKey(childCategoriesID, "ID");

  public SUserprop_category_(String variable) {
    super(SUserprop_category_.class, forVariable(variable), "null", "userprop__category_");
    addMetadata();
  }

  public SUserprop_category_(String variable, String schema, String table) {
    super(SUserprop_category_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUserprop_category_(String variable, String schema) {
    super(SUserprop_category_.class, forVariable(variable), schema, "userprop__category_");
    addMetadata();
  }

  public SUserprop_category_(Path<? extends SUserprop_category_> path) {
    super(path.getType(), path.getMetadata(), "null", "userprop__category_");
    addMetadata();
  }

  public SUserprop_category_(PathMetadata metadata) {
    super(SUserprop_category_.class, metadata, "null", "userprop__category_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        childCategoriesID,
        ColumnMetadata.named("childCategories_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        userPropID,
        ColumnMetadata.named("UserProp_ID")
            .withIndex(1)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
  }
}
