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

/** SCategory_ is a Querydsl query type for SCategory_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCategory_ extends com.querydsl.sql.RelationalPathBase<SCategory_> {

  private static final long serialVersionUID = 1845227417;

  public static final SCategory_ category_ = new SCategory_("category_");

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

  public final com.querydsl.sql.PrimaryKey<SCategory_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCategory_category_> _category_category_childIdFK =
      createInvForeignKey(id, "childId");

  public final com.querydsl.sql.ForeignKey<SCategory_category_> _category_category_parentIdFK =
      createInvForeignKey(id, "parentId");

  public final com.querydsl.sql.ForeignKey<SCategory_categoryprop_>
      _category_categoryprop_CategoryIDFK = createInvForeignKey(id, "Category_ID");

  public final com.querydsl.sql.ForeignKey<SUserprop_category_>
      _userprop_category_childCategoriesIDFK = createInvForeignKey(id, "childCategories_ID");

  public SCategory_(String variable) {
    super(SCategory_.class, forVariable(variable), "null", "category_");
    addMetadata();
  }

  public SCategory_(String variable, String schema, String table) {
    super(SCategory_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCategory_(String variable, String schema) {
    super(SCategory_.class, forVariable(variable), schema, "category_");
    addMetadata();
  }

  public SCategory_(Path<? extends SCategory_> path) {
    super(path.getType(), path.getMetadata(), "null", "category_");
    addMetadata();
  }

  public SCategory_(PathMetadata metadata) {
    super(SCategory_.class, metadata, "null", "category_");
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
