package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SEviltype_ is a Querydsl query type for SEviltype_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SEviltype_ extends com.querydsl.sql.RelationalPathBase<SEviltype_> {

  private static final long serialVersionUID = -490303543;

  public static final SEviltype_ eviltype_ = new SEviltype_("eviltype_");

  public final NumberPath<Integer> _asc = createNumber("_asc", Integer.class);

  public final NumberPath<Integer> _desc = createNumber("_desc", Integer.class);

  public final NumberPath<Integer> getclassId = createNumber("getclassId", Integer.class);

  public final NumberPath<Integer> getId = createNumber("getId", Integer.class);

  public final NumberPath<Integer> getmetadataId = createNumber("getmetadataId", Integer.class);

  public final NumberPath<Integer> gettypeId = createNumber("gettypeId", Integer.class);

  public final NumberPath<Integer> hashcodeId = createNumber("hashcodeId", Integer.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final NumberPath<Integer> isnotnullId = createNumber("isnotnullId", Integer.class);

  public final NumberPath<Integer> isnullId = createNumber("isnullId", Integer.class);

  public final NumberPath<Integer> notifyallId = createNumber("notifyallId", Integer.class);

  public final NumberPath<Integer> notifyId = createNumber("notifyId", Integer.class);

  public final NumberPath<Integer> tostringId = createNumber("tostringId", Integer.class);

  public final NumberPath<Integer> waitId = createNumber("waitId", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SEviltype_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_GETCLASSIDFK =
      createForeignKey(getclassId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_GETMETADATAIDFK =
      createForeignKey(getmetadataId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_GETTYPEIDFK =
      createForeignKey(gettypeId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_GETIDFK =
      createForeignKey(getId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_HASHCODEIDFK =
      createForeignKey(hashcodeId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_ISNOTNULLIDFK =
      createForeignKey(isnotnullId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_ISNULLIDFK =
      createForeignKey(isnullId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_NOTIFYALLIDFK =
      createForeignKey(notifyallId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_NOTIFYIDFK =
      createForeignKey(notifyId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_TOSTRINGIDFK =
      createForeignKey(tostringId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_WAITIDFK =
      createForeignKey(waitId, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_AscFK =
      createForeignKey(_asc, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> eviltype_DescFK =
      createForeignKey(_desc, "ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_GETCLASSIDFK =
      createInvForeignKey(id, "GETCLASS_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_GETMETADATAIDFK =
      createInvForeignKey(id, "GETMETADATA_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_GETTYPEIDFK =
      createInvForeignKey(id, "GETTYPE_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_GETIDFK =
      createInvForeignKey(id, "GET_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_HASHCODEIDFK =
      createInvForeignKey(id, "HASHCODE_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_ISNOTNULLIDFK =
      createInvForeignKey(id, "ISNOTNULL_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_ISNULLIDFK =
      createInvForeignKey(id, "ISNULL_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_NOTIFYALLIDFK =
      createInvForeignKey(id, "NOTIFYALL_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_NOTIFYIDFK =
      createInvForeignKey(id, "NOTIFY_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_TOSTRINGIDFK =
      createInvForeignKey(id, "TOSTRING_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_WAITIDFK =
      createInvForeignKey(id, "WAIT_ID");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_AscFK =
      createInvForeignKey(id, "_asc");

  public final com.querydsl.sql.ForeignKey<SEviltype_> _eviltype_DescFK =
      createInvForeignKey(id, "_desc");

  public SEviltype_(String variable) {
    super(SEviltype_.class, forVariable(variable), "null", "eviltype_");
    addMetadata();
  }

  public SEviltype_(String variable, String schema, String table) {
    super(SEviltype_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEviltype_(String variable, String schema) {
    super(SEviltype_.class, forVariable(variable), schema, "eviltype_");
    addMetadata();
  }

  public SEviltype_(Path<? extends SEviltype_> path) {
    super(path.getType(), path.getMetadata(), "null", "eviltype_");
    addMetadata();
  }

  public SEviltype_(PathMetadata metadata) {
    super(SEviltype_.class, metadata, "null", "eviltype_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(_asc, ColumnMetadata.named("_asc").withIndex(2).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        _desc, ColumnMetadata.named("_desc").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        getclassId,
        ColumnMetadata.named("GETCLASS_ID").withIndex(5).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        getId, ColumnMetadata.named("GET_ID").withIndex(4).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        getmetadataId,
        ColumnMetadata.named("GETMETADATA_ID").withIndex(6).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        gettypeId,
        ColumnMetadata.named("GETTYPE_ID").withIndex(7).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        hashcodeId,
        ColumnMetadata.named("HASHCODE_ID").withIndex(8).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        isnotnullId,
        ColumnMetadata.named("ISNOTNULL_ID").withIndex(9).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        isnullId,
        ColumnMetadata.named("ISNULL_ID").withIndex(10).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        notifyallId,
        ColumnMetadata.named("NOTIFYALL_ID").withIndex(12).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        notifyId,
        ColumnMetadata.named("NOTIFY_ID").withIndex(11).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        tostringId,
        ColumnMetadata.named("TOSTRING_ID").withIndex(13).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        waitId, ColumnMetadata.named("WAIT_ID").withIndex(14).ofType(Types.INTEGER).withSize(10));
  }
}
