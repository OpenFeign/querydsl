package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCategory_category_ is a Querydsl query type for SCategory_category_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCategory_category_ extends com.querydsl.sql.RelationalPathBase<SCategory_category_> {

  private static final long serialVersionUID = 171601832;

  public static final SCategory_category_ category_category_ =
      new SCategory_category_("category__category_");

  public final NumberPath<Long> childId = createNumber("childId", Long.class);

  public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SCategory_category_> primary =
      createPrimaryKey(childId, parentId);

  public final com.querydsl.sql.ForeignKey<SCategory_> category_category_childIdFK =
      createForeignKey(childId, "ID");

  public final com.querydsl.sql.ForeignKey<SCategory_> category_category_parentIdFK =
      createForeignKey(parentId, "ID");

  public SCategory_category_(String variable) {
    super(SCategory_category_.class, forVariable(variable), "null", "category__category_");
    addMetadata();
  }

  public SCategory_category_(String variable, String schema, String table) {
    super(SCategory_category_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCategory_category_(String variable, String schema) {
    super(SCategory_category_.class, forVariable(variable), schema, "category__category_");
    addMetadata();
  }

  public SCategory_category_(Path<? extends SCategory_category_> path) {
    super(path.getType(), path.getMetadata(), "null", "category__category_");
    addMetadata();
  }

  public SCategory_category_(PathMetadata metadata) {
    super(SCategory_category_.class, metadata, "null", "category__category_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        childId,
        ColumnMetadata.named("childId").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        parentId,
        ColumnMetadata.named("parentId").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
