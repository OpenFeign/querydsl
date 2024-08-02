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

/** SDocument2_ is a Querydsl query type for SDocument2_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SDocument2_ extends com.querydsl.sql.RelationalPathBase<SDocument2_> {

  private static final long serialVersionUID = -743997616;

  public static final SDocument2_ document2_ = new SDocument2_("document2_");

  public final NumberPath<Double> createdby = createNumber("createdby", Double.class);

  public final DateTimePath<java.sql.Timestamp> creationdate =
      createDateTime("creationdate", java.sql.Timestamp.class);

  public final NumberPath<Double> deletedby = createNumber("deletedby", Double.class);

  public final DateTimePath<java.sql.Timestamp> deleteddate =
      createDateTime("deleteddate", java.sql.Timestamp.class);

  public final StringPath documentbody = createString("documentbody");

  public final NumberPath<Double> documentstatus = createNumber("documentstatus", Double.class);

  public final StringPath documentsummary = createString("documentsummary");

  public final StringPath documenttitle = createString("documenttitle");

  public final NumberPath<Double> documentversion = createNumber("documentversion", Double.class);

  public final NumberPath<Double> entryid = createNumber("entryid", Double.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final DateTimePath<java.sql.Timestamp> modificationdate =
      createDateTime("modificationdate", java.sql.Timestamp.class);

  public final NumberPath<Double> modifiedby = createNumber("modifiedby", Double.class);

  public final com.querydsl.sql.PrimaryKey<SDocument2_> primary = createPrimaryKey(id);

  public SDocument2_(String variable) {
    super(SDocument2_.class, forVariable(variable), "null", "document2_");
    addMetadata();
  }

  public SDocument2_(String variable, String schema, String table) {
    super(SDocument2_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDocument2_(String variable, String schema) {
    super(SDocument2_.class, forVariable(variable), schema, "document2_");
    addMetadata();
  }

  public SDocument2_(Path<? extends SDocument2_> path) {
    super(path.getType(), path.getMetadata(), "null", "document2_");
    addMetadata();
  }

  public SDocument2_(PathMetadata metadata) {
    super(SDocument2_.class, metadata, "null", "document2_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        createdby,
        ColumnMetadata.named("CREATEDBY").withIndex(2).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        creationdate,
        ColumnMetadata.named("CREATIONDATE").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        deletedby,
        ColumnMetadata.named("DELETEDBY").withIndex(4).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        deleteddate,
        ColumnMetadata.named("DELETEDDATE").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        documentbody,
        ColumnMetadata.named("DOCUMENTBODY").withIndex(6).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        documentstatus,
        ColumnMetadata.named("DOCUMENTSTATUS").withIndex(7).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        documentsummary,
        ColumnMetadata.named("DOCUMENTSUMMARY").withIndex(8).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        documenttitle,
        ColumnMetadata.named("DOCUMENTTITLE").withIndex(9).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        documentversion,
        ColumnMetadata.named("DOCUMENTVERSION").withIndex(10).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        entryid, ColumnMetadata.named("ENTRYID").withIndex(11).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        modificationdate,
        ColumnMetadata.named("MODIFICATIONDATE")
            .withIndex(12)
            .ofType(Types.TIMESTAMP)
            .withSize(19));
    addMetadata(
        modifiedby,
        ColumnMetadata.named("MODIFIEDBY").withIndex(13).ofType(Types.DOUBLE).withSize(22));
  }
}
