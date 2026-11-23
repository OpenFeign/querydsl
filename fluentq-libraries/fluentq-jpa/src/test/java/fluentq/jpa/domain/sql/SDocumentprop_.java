package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDocumentprop_ is a FluentQ query type for SDocumentprop_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SDocumentprop_ extends fluentq.sql.RelationalPathBase<SDocumentprop_> {

  private static final long serialVersionUID = 1951170969;

  public static final SDocumentprop_ documentprop_ = new SDocumentprop_("documentprop_");

  public final NumberPath<Double> documentid = createNumber("documentid", Double.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath propname = createString("propname");

  public final StringPath propvalue = createString("propvalue");

  public final StringPath propvaluedetails = createString("propvaluedetails");

  public final fluentq.sql.PrimaryKey<SDocumentprop_> primary = createPrimaryKey(id);

  public SDocumentprop_(String variable) {
    super(SDocumentprop_.class, forVariable(variable), "null", "documentprop_");
    addMetadata();
  }

  public SDocumentprop_(String variable, String schema, String table) {
    super(SDocumentprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDocumentprop_(String variable, String schema) {
    super(SDocumentprop_.class, forVariable(variable), schema, "documentprop_");
    addMetadata();
  }

  public SDocumentprop_(Path<? extends SDocumentprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "documentprop_");
    addMetadata();
  }

  public SDocumentprop_(PathMetadata metadata) {
    super(SDocumentprop_.class, metadata, "null", "documentprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        documentid,
        ColumnMetadata.named("DOCUMENTID").withIndex(2).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        propname,
        ColumnMetadata.named("PROPNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        propvalue,
        ColumnMetadata.named("PROPVALUE").withIndex(4).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        propvaluedetails,
        ColumnMetadata.named("PROPVALUEDETAILS").withIndex(5).ofType(Types.VARCHAR).withSize(255));
  }
}
