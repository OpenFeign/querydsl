package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SSimpletypes_ is a Querydsl query type for SSimpletypes_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SSimpletypes_ extends com.querydsl.sql.RelationalPathBase<SSimpletypes_> {

  private static final long serialVersionUID = -971482176;

  public static final SSimpletypes_ simpletypes_ = new SSimpletypes_("simpletypes_");

  public final NumberPath<Byte> bbyte = createNumber("bbyte", Byte.class);

  public final NumberPath<Byte> bbyte2 = createNumber("bbyte2", Byte.class);

  public final NumberPath<java.math.BigInteger> bigdecimal =
      createNumber("bigdecimal", java.math.BigInteger.class);

  public final SimplePath<byte[]> bytearray = createSimple("bytearray", byte[].class);

  public final StringPath cchar = createString("cchar");

  public final StringPath cchar2 = createString("cchar2");

  public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

  public final NumberPath<Double> ddouble = createNumber("ddouble", Double.class);

  public final NumberPath<Double> ddouble2 = createNumber("ddouble2", Double.class);

  public final NumberPath<Float> ffloat = createNumber("ffloat", Float.class);

  public final NumberPath<Float> ffloat2 = createNumber("ffloat2", Float.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Integer> iint = createNumber("iint", Integer.class);

  public final NumberPath<Integer> iint2 = createNumber("iint2", Integer.class);

  public final SimplePath<byte[]> llocale = createSimple("llocale", byte[].class);

  public final NumberPath<Long> llong = createNumber("llong", Long.class);

  public final NumberPath<Long> llong2 = createNumber("llong2", Long.class);

  public final StringPath sstring = createString("sstring");

  public final TimePath<java.sql.Time> time = createTime("time", java.sql.Time.class);

  public final DateTimePath<java.sql.Timestamp> timestamp =
      createDateTime("timestamp", java.sql.Timestamp.class);

  public final com.querydsl.sql.PrimaryKey<SSimpletypes_> primary = createPrimaryKey(id);

  public SSimpletypes_(String variable) {
    super(SSimpletypes_.class, forVariable(variable), "null", "simpletypes_");
    addMetadata();
  }

  public SSimpletypes_(String variable, String schema, String table) {
    super(SSimpletypes_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SSimpletypes_(String variable, String schema) {
    super(SSimpletypes_.class, forVariable(variable), schema, "simpletypes_");
    addMetadata();
  }

  public SSimpletypes_(Path<? extends SSimpletypes_> path) {
    super(path.getType(), path.getMetadata(), "null", "simpletypes_");
    addMetadata();
  }

  public SSimpletypes_(PathMetadata metadata) {
    super(SSimpletypes_.class, metadata, "null", "simpletypes_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        bbyte, ColumnMetadata.named("BBYTE").withIndex(2).ofType(Types.TINYINT).withSize(3));
    addMetadata(
        bbyte2, ColumnMetadata.named("BBYTE2").withIndex(3).ofType(Types.TINYINT).withSize(3));
    addMetadata(
        bigdecimal,
        ColumnMetadata.named("BIGDECIMAL").withIndex(4).ofType(Types.DECIMAL).withSize(38));
    addMetadata(
        bytearray,
        ColumnMetadata.named("BYTEARRAY")
            .withIndex(5)
            .ofType(Types.LONGVARBINARY)
            .withSize(2147483647));
    addMetadata(cchar, ColumnMetadata.named("CCHAR").withIndex(6).ofType(Types.CHAR).withSize(1));
    addMetadata(cchar2, ColumnMetadata.named("CCHAR2").withIndex(7).ofType(Types.CHAR).withSize(1));
    addMetadata(date, ColumnMetadata.named("DATE").withIndex(8).ofType(Types.DATE).withSize(10));
    addMetadata(
        ddouble, ColumnMetadata.named("DDOUBLE").withIndex(9).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        ddouble2, ColumnMetadata.named("DDOUBLE2").withIndex(10).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        ffloat, ColumnMetadata.named("FFLOAT").withIndex(11).ofType(Types.REAL).withSize(12));
    addMetadata(
        ffloat2, ColumnMetadata.named("FFLOAT2").withIndex(12).ofType(Types.REAL).withSize(12));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        iint, ColumnMetadata.named("IINT").withIndex(13).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        iint2, ColumnMetadata.named("IINT2").withIndex(14).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        llocale,
        ColumnMetadata.named("LLOCALE")
            .withIndex(15)
            .ofType(Types.LONGVARBINARY)
            .withSize(2147483647));
    addMetadata(
        llong, ColumnMetadata.named("LLONG").withIndex(16).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        llong2, ColumnMetadata.named("LLONG2").withIndex(17).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        sstring, ColumnMetadata.named("SSTRING").withIndex(18).ofType(Types.VARCHAR).withSize(255));
    addMetadata(time, ColumnMetadata.named("TIME").withIndex(19).ofType(Types.TIME).withSize(8));
    addMetadata(
        timestamp,
        ColumnMetadata.named("TIMESTAMP").withIndex(20).ofType(Types.TIMESTAMP).withSize(19));
  }
}
