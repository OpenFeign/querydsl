package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCategory_categoryprop_ is a Querydsl query type for SCategory_categoryprop_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCategory_categoryprop_
    extends com.querydsl.sql.RelationalPathBase<SCategory_categoryprop_> {

  private static final long serialVersionUID = -2083558363;

  public static final SCategory_categoryprop_ category_categoryprop_ =
      new SCategory_categoryprop_("category__categoryprop_");

  public final NumberPath<Long> categoryID = createNumber("categoryID", Long.class);

  public final NumberPath<Long> propertiesID = createNumber("propertiesID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SCategory_categoryprop_> primary =
      createPrimaryKey(categoryID, propertiesID);

  public final com.querydsl.sql.ForeignKey<SCategory_> category_categoryprop_CategoryIDFK =
      createForeignKey(categoryID, "ID");

  public final com.querydsl.sql.ForeignKey<SCategoryprop_> category_categoryprop_propertiesIDFK =
      createForeignKey(propertiesID, "ID");

  public SCategory_categoryprop_(String variable) {
    super(SCategory_categoryprop_.class, forVariable(variable), "null", "category__categoryprop_");
    addMetadata();
  }

  public SCategory_categoryprop_(String variable, String schema, String table) {
    super(SCategory_categoryprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCategory_categoryprop_(String variable, String schema) {
    super(SCategory_categoryprop_.class, forVariable(variable), schema, "category__categoryprop_");
    addMetadata();
  }

  public SCategory_categoryprop_(Path<? extends SCategory_categoryprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "category__categoryprop_");
    addMetadata();
  }

  public SCategory_categoryprop_(PathMetadata metadata) {
    super(SCategory_categoryprop_.class, metadata, "null", "category__categoryprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        categoryID,
        ColumnMetadata.named("Category_ID")
            .withIndex(1)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        propertiesID,
        ColumnMetadata.named("properties_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
  }
}
