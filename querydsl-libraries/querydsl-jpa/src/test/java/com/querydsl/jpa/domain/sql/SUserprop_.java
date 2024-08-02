package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SUserprop_ is a Querydsl query type for SUserprop_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SUserprop_ extends com.querydsl.sql.RelationalPathBase<SUserprop_> {

  private static final long serialVersionUID = 634556649;

  public static final SUserprop_ userprop_ = new SUserprop_("userprop_");

  public final StringPath categorydescription = createString("categorydescription");

  public final StringPath categoryname = createString("categoryname");

  public final NumberPath<Double> createdby = createNumber("createdby", Double.class);

  public final DateTimePath<java.sql.Timestamp> creationdate =
      createDateTime("creationdate", java.sql.Timestamp.class);

  public final DateTimePath<java.sql.Timestamp> deletedate =
      createDateTime("deletedate", java.sql.Timestamp.class);

  public final NumberPath<Double> deletedby = createNumber("deletedby", Double.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final DateTimePath<java.sql.Timestamp> modificationdate =
      createDateTime("modificationdate", java.sql.Timestamp.class);

  public final NumberPath<Double> modifiedby = createNumber("modifiedby", Double.class);

  public final com.querydsl.sql.PrimaryKey<SUserprop_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SUser2_userprop_> _user2_userprop_propertiesIDFK =
      createInvForeignKey(id, "properties_ID");

  public final com.querydsl.sql.ForeignKey<SUserprop_category_> _userprop_category_UserPropIDFK =
      createInvForeignKey(id, "UserProp_ID");

  public final com.querydsl.sql.ForeignKey<SUserprop_categoryprop_>
      _userprop_categoryprop_UserPropIDFK = createInvForeignKey(id, "UserProp_ID");

  public SUserprop_(String variable) {
    super(SUserprop_.class, forVariable(variable), "null", "userprop_");
    addMetadata();
  }

  public SUserprop_(String variable, String schema, String table) {
    super(SUserprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUserprop_(String variable, String schema) {
    super(SUserprop_.class, forVariable(variable), schema, "userprop_");
    addMetadata();
  }

  public SUserprop_(Path<? extends SUserprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "userprop_");
    addMetadata();
  }

  public SUserprop_(PathMetadata metadata) {
    super(SUserprop_.class, metadata, "null", "userprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        categorydescription,
        ColumnMetadata.named("CATEGORYDESCRIPTION")
            .withIndex(2)
            .ofType(Types.VARCHAR)
            .withSize(255));
    addMetadata(
        categoryname,
        ColumnMetadata.named("CATEGORYNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        createdby,
        ColumnMetadata.named("CREATEDBY").withIndex(4).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        creationdate,
        ColumnMetadata.named("CREATIONDATE").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        deletedate,
        ColumnMetadata.named("DELETEDATE").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        deletedby,
        ColumnMetadata.named("DELETEDBY").withIndex(7).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        modificationdate,
        ColumnMetadata.named("MODIFICATIONDATE").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        modifiedby,
        ColumnMetadata.named("MODIFIEDBY").withIndex(9).ofType(Types.DOUBLE).withSize(22));
  }
}
