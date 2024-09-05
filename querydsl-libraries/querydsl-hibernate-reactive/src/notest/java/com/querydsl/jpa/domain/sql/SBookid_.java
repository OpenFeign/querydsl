package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SBookid_ is a Querydsl query type for SBookid_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBookid_ extends com.querydsl.sql.RelationalPathBase<SBookid_> {

  private static final long serialVersionUID = -1320496749;

  public static final SBookid_ bookid_ = new SBookid_("bookid_");

  public final NumberPath<Long> identity = createNumber("identity", Long.class);

  public final com.querydsl.sql.PrimaryKey<SBookid_> primary = createPrimaryKey(identity);

  public final com.querydsl.sql.ForeignKey<SBookversion_> _bookversion_BOOKIDIDENTITYFK =
      createInvForeignKey(identity, "BOOKID_IDENTITY");

  public SBookid_(String variable) {
    super(SBookid_.class, forVariable(variable), "null", "bookid_");
    addMetadata();
  }

  public SBookid_(String variable, String schema, String table) {
    super(SBookid_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBookid_(String variable, String schema) {
    super(SBookid_.class, forVariable(variable), schema, "bookid_");
    addMetadata();
  }

  public SBookid_(Path<? extends SBookid_> path) {
    super(path.getType(), path.getMetadata(), "null", "bookid_");
    addMetadata();
  }

  public SBookid_(PathMetadata metadata) {
    super(SBookid_.class, metadata, "null", "bookid_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        identity,
        ColumnMetadata.named("IDENTITY").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
