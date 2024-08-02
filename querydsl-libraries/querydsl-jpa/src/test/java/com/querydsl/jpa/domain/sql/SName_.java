package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SName_ is a Querydsl query type for SName_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SName_ extends com.querydsl.sql.RelationalPathBase<SName_> {

  private static final long serialVersionUID = -947134548;

  public static final SName_ name_ = new SName_("name_");

  public final StringPath firstname = createString("firstname");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath lastname = createString("lastname");

  public final StringPath nickname = createString("nickname");

  public final com.querydsl.sql.PrimaryKey<SName_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCustomer_> _customer_NAMEIDFK =
      createInvForeignKey(id, "NAME_ID");

  public SName_(String variable) {
    super(SName_.class, forVariable(variable), "null", "name_");
    addMetadata();
  }

  public SName_(String variable, String schema, String table) {
    super(SName_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SName_(String variable, String schema) {
    super(SName_.class, forVariable(variable), schema, "name_");
    addMetadata();
  }

  public SName_(Path<? extends SName_> path) {
    super(path.getType(), path.getMetadata(), "null", "name_");
    addMetadata();
  }

  public SName_(PathMetadata metadata) {
    super(SName_.class, metadata, "null", "name_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        firstname,
        ColumnMetadata.named("FIRSTNAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        lastname,
        ColumnMetadata.named("LASTNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        nickname,
        ColumnMetadata.named("NICKNAME").withIndex(4).ofType(Types.VARCHAR).withSize(255));
  }
}
