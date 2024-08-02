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

/** SUser2_ is a Querydsl query type for SUser2_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SUser2_ extends com.querydsl.sql.RelationalPathBase<SUser2_> {

  private static final long serialVersionUID = 920400384;

  public static final SUser2_ user2_ = new SUser2_("user2_");

  public final NumberPath<Double> createdby = createNumber("createdby", Double.class);

  public final DateTimePath<java.sql.Timestamp> creationdate =
      createDateTime("creationdate", java.sql.Timestamp.class);

  public final DateTimePath<java.sql.Timestamp> deletedate =
      createDateTime("deletedate", java.sql.Timestamp.class);

  public final NumberPath<Double> deletedby = createNumber("deletedby", Double.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final DateTimePath<java.sql.Timestamp> modificationdate =
      createDateTime("modificationdate", java.sql.Timestamp.class);

  public final NumberPath<Double> modifiedby = createNumber("modifiedby", Double.class);

  public final StringPath useremail = createString("useremail");

  public final StringPath userfirstname = createString("userfirstname");

  public final StringPath userlastname = createString("userlastname");

  public final StringPath username = createString("username");

  public final StringPath userpassword = createString("userpassword");

  public final com.querydsl.sql.PrimaryKey<SUser2_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SUser2_userprop_> _user2_userprop_User2IDFK =
      createInvForeignKey(id, "User2_ID");

  public SUser2_(String variable) {
    super(SUser2_.class, forVariable(variable), "null", "user2_");
    addMetadata();
  }

  public SUser2_(String variable, String schema, String table) {
    super(SUser2_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUser2_(String variable, String schema) {
    super(SUser2_.class, forVariable(variable), schema, "user2_");
    addMetadata();
  }

  public SUser2_(Path<? extends SUser2_> path) {
    super(path.getType(), path.getMetadata(), "null", "user2_");
    addMetadata();
  }

  public SUser2_(PathMetadata metadata) {
    super(SUser2_.class, metadata, "null", "user2_");
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
        deletedate,
        ColumnMetadata.named("DELETEDATE").withIndex(4).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        deletedby,
        ColumnMetadata.named("DELETEDBY").withIndex(5).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        modificationdate,
        ColumnMetadata.named("MODIFICATIONDATE").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        modifiedby,
        ColumnMetadata.named("MODIFIEDBY").withIndex(7).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        useremail,
        ColumnMetadata.named("USEREMAIL").withIndex(8).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        userfirstname,
        ColumnMetadata.named("USERFIRSTNAME").withIndex(9).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        userlastname,
        ColumnMetadata.named("USERLASTNAME").withIndex(10).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        username,
        ColumnMetadata.named("USERNAME").withIndex(11).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        userpassword,
        ColumnMetadata.named("USERPASSWORD").withIndex(12).ofType(Types.VARCHAR).withSize(255));
  }
}
