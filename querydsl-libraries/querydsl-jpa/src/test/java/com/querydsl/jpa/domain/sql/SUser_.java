package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SUser_ is a Querydsl query type for SUser_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SUser_ extends com.querydsl.sql.RelationalPathBase<SUser_> {

  private static final long serialVersionUID = -940140948;

  public static final SUser_ user_ = new SUser_("user_");

  public final NumberPath<Integer> companyId = createNumber("companyId", Integer.class);

  public final StringPath firstname = createString("firstname");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath lastname = createString("lastname");

  public final StringPath username = createString("username");

  public final com.querydsl.sql.PrimaryKey<SUser_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCompany_> user_COMPANYIDFK =
      createForeignKey(companyId, "ID");

  public final com.querydsl.sql.ForeignKey<SEmployee_> _employee_USERIDFK =
      createInvForeignKey(id, "USER_ID");

  public SUser_(String variable) {
    super(SUser_.class, forVariable(variable), "null", "user_");
    addMetadata();
  }

  public SUser_(String variable, String schema, String table) {
    super(SUser_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUser_(String variable, String schema) {
    super(SUser_.class, forVariable(variable), schema, "user_");
    addMetadata();
  }

  public SUser_(Path<? extends SUser_> path) {
    super(path.getType(), path.getMetadata(), "null", "user_");
    addMetadata();
  }

  public SUser_(PathMetadata metadata) {
    super(SUser_.class, metadata, "null", "user_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        companyId,
        ColumnMetadata.named("COMPANY_ID").withIndex(5).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        firstname,
        ColumnMetadata.named("FIRSTNAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        lastname,
        ColumnMetadata.named("LASTNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        username,
        ColumnMetadata.named("USERNAME").withIndex(4).ofType(Types.VARCHAR).withSize(255));
  }
}
