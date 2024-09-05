package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SUserprop_categoryprop_ is a Querydsl query type for SUserprop_categoryprop_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SUserprop_categoryprop_
    extends com.querydsl.sql.RelationalPathBase<SUserprop_categoryprop_> {

  private static final long serialVersionUID = -925537067;

  public static final SUserprop_categoryprop_ userprop_categoryprop_ =
      new SUserprop_categoryprop_("userprop__categoryprop_");

  public final NumberPath<Long> propertiesID = createNumber("propertiesID", Long.class);

  public final NumberPath<Long> userPropID = createNumber("userPropID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SUserprop_categoryprop_> primary =
      createPrimaryKey(userPropID, propertiesID);

  public final com.querydsl.sql.ForeignKey<SUserprop_> userprop_categoryprop_UserPropIDFK =
      createForeignKey(userPropID, "ID");

  public final com.querydsl.sql.ForeignKey<SCategoryprop_> userprop_categoryprop_propertiesIDFK =
      createForeignKey(propertiesID, "ID");

  public SUserprop_categoryprop_(String variable) {
    super(SUserprop_categoryprop_.class, forVariable(variable), "null", "userprop__categoryprop_");
    addMetadata();
  }

  public SUserprop_categoryprop_(String variable, String schema, String table) {
    super(SUserprop_categoryprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUserprop_categoryprop_(String variable, String schema) {
    super(SUserprop_categoryprop_.class, forVariable(variable), schema, "userprop__categoryprop_");
    addMetadata();
  }

  public SUserprop_categoryprop_(Path<? extends SUserprop_categoryprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "userprop__categoryprop_");
    addMetadata();
  }

  public SUserprop_categoryprop_(PathMetadata metadata) {
    super(SUserprop_categoryprop_.class, metadata, "null", "userprop__categoryprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        propertiesID,
        ColumnMetadata.named("properties_ID")
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
