package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCategoryprop_ is a Querydsl query type for SCategoryprop_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCategoryprop_ extends com.querydsl.sql.RelationalPathBase<SCategoryprop_> {

  private static final long serialVersionUID = 704482198;

  public static final SCategoryprop_ categoryprop_ = new SCategoryprop_("categoryprop_");

  public final NumberPath<Long> categoryid = createNumber("categoryid", Long.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath propname = createString("propname");

  public final StringPath propvalue = createString("propvalue");

  public final com.querydsl.sql.PrimaryKey<SCategoryprop_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCategory_categoryprop_>
      _category_categoryprop_propertiesIDFK = createInvForeignKey(id, "properties_ID");

  public final com.querydsl.sql.ForeignKey<SUserprop_categoryprop_>
      _userprop_categoryprop_propertiesIDFK = createInvForeignKey(id, "properties_ID");

  public SCategoryprop_(String variable) {
    super(SCategoryprop_.class, forVariable(variable), "null", "categoryprop_");
    addMetadata();
  }

  public SCategoryprop_(String variable, String schema, String table) {
    super(SCategoryprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCategoryprop_(String variable, String schema) {
    super(SCategoryprop_.class, forVariable(variable), schema, "categoryprop_");
    addMetadata();
  }

  public SCategoryprop_(Path<? extends SCategoryprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "categoryprop_");
    addMetadata();
  }

  public SCategoryprop_(PathMetadata metadata) {
    super(SCategoryprop_.class, metadata, "null", "categoryprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        categoryid,
        ColumnMetadata.named("CATEGORYID").withIndex(2).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        propname,
        ColumnMetadata.named("PROPNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        propvalue,
        ColumnMetadata.named("PROPVALUE").withIndex(4).ofType(Types.VARCHAR).withSize(255));
  }
}
