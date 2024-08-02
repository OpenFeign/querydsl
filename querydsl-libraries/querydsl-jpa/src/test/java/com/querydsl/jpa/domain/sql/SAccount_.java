package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SAccount_ is a Querydsl query type for SAccount_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SAccount_ extends com.querydsl.sql.RelationalPathBase<SAccount_> {

  private static final long serialVersionUID = -2128134054;

  public static final SAccount_ account_ = new SAccount_("account_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> ownerI = createNumber("ownerI", Long.class);

  public final StringPath somedata = createString("somedata");

  public final com.querydsl.sql.PrimaryKey<SAccount_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SPerson_> account_OWNERIFK =
      createForeignKey(ownerI, "I");

  public SAccount_(String variable) {
    super(SAccount_.class, forVariable(variable), "null", "account_");
    addMetadata();
  }

  public SAccount_(String variable, String schema, String table) {
    super(SAccount_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SAccount_(String variable, String schema) {
    super(SAccount_.class, forVariable(variable), schema, "account_");
    addMetadata();
  }

  public SAccount_(Path<? extends SAccount_> path) {
    super(path.getType(), path.getMetadata(), "null", "account_");
    addMetadata();
  }

  public SAccount_(PathMetadata metadata) {
    super(SAccount_.class, metadata, "null", "account_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        ownerI, ColumnMetadata.named("OWNER_I").withIndex(3).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        somedata,
        ColumnMetadata.named("SOMEDATA").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
