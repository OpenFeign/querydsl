package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPerson_ is a Querydsl query type for SPerson_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPerson_ extends com.querydsl.sql.RelationalPathBase<SPerson_> {

  private static final long serialVersionUID = -2063623646;

  public static final SPerson_ person_ = new SPerson_("person_");

  public final DatePath<java.sql.Date> birthday = createDate("birthday", java.sql.Date.class);

  public final NumberPath<Long> i = createNumber("i", Long.class);

  public final StringPath name = createString("name");

  public final NumberPath<Long> nationalityId = createNumber("nationalityId", Long.class);

  public final NumberPath<Long> pidId = createNumber("pidId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SPerson_> primary = createPrimaryKey(i);

  public final com.querydsl.sql.ForeignKey<SNationality_> person_NATIONALITYIDFK =
      createForeignKey(nationalityId, "ID");

  public final com.querydsl.sql.ForeignKey<SPersonid_> person_PIDIDFK =
      createForeignKey(pidId, "ID");

  public final com.querydsl.sql.ForeignKey<SAccount_> _account_OWNERIFK =
      createInvForeignKey(i, "OWNER_I");

  public SPerson_(String variable) {
    super(SPerson_.class, forVariable(variable), "null", "person_");
    addMetadata();
  }

  public SPerson_(String variable, String schema, String table) {
    super(SPerson_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPerson_(String variable, String schema) {
    super(SPerson_.class, forVariable(variable), schema, "person_");
    addMetadata();
  }

  public SPerson_(Path<? extends SPerson_> path) {
    super(path.getType(), path.getMetadata(), "null", "person_");
    addMetadata();
  }

  public SPerson_(PathMetadata metadata) {
    super(SPerson_.class, metadata, "null", "person_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        birthday, ColumnMetadata.named("BIRTHDAY").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        i, ColumnMetadata.named("I").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        nationalityId,
        ColumnMetadata.named("NATIONALITY_ID").withIndex(4).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        pidId, ColumnMetadata.named("PID_ID").withIndex(5).ofType(Types.BIGINT).withSize(19));
  }
}
