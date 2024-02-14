package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SAnimal is a Querydsl query type for SAnimal */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SAnimal extends com.querydsl.sql.RelationalPathBase<SAnimal> {

  private static final long serialVersionUID = 343315588;

  public static final SAnimal animal = new SAnimal("animal");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath dtype = createString("dtype");

  public final BooleanPath alive = createBoolean("alive");

  public final DateTimePath<java.sql.Timestamp> birthdate =
      createDateTime("birthdate", java.sql.Timestamp.class);

  public final NumberPath<Double> bodyweight = createNumber("bodyweight", Double.class);

  public final NumberPath<Integer> color = createNumber("color", Integer.class);

  public final DatePath<java.sql.Date> datefield = createDate("datefield", java.sql.Date.class);

  public final NumberPath<Float> floatproperty = createNumber("floatproperty", Float.class);

  public final StringPath name = createString("name");

  public final TimePath<java.sql.Time> timefield = createTime("timefield", java.sql.Time.class);

  public final NumberPath<Integer> toes = createNumber("toes", Integer.class);

  public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

  public final NumberPath<Integer> breed = createNumber("breed", Integer.class);

  public final NumberPath<Integer> eyecolor = createNumber("eyecolor", Integer.class);

  public final NumberPath<Integer> mateId = createNumber("mateId", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SAnimal> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SAnimal> animalMATEIDFK = createForeignKey(mateId, "ID");

  public final com.querydsl.sql.ForeignKey<SAnimal> _animalMATEIDFK =
      createInvForeignKey(id, "MATE_ID");

  public SAnimal(String variable) {
    super(SAnimal.class, forVariable(variable), "null", "animal");
    addMetadata();
  }

  public SAnimal(String variable, String schema, String table) {
    super(SAnimal.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SAnimal(String variable, String schema) {
    super(SAnimal.class, forVariable(variable), schema, "animal");
    addMetadata();
  }

  public SAnimal(Path<? extends SAnimal> path) {
    super(path.getType(), path.getMetadata(), "null", "animal");
    addMetadata();
  }

  public SAnimal(PathMetadata metadata) {
    super(SAnimal.class, metadata, "null", "animal");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        dtype, ColumnMetadata.named("DTYPE").withIndex(2).ofType(Types.VARCHAR).withSize(31));
    addMetadata(alive, ColumnMetadata.named("ALIVE").withIndex(3).ofType(Types.BIT).withSize(1));
    addMetadata(
        birthdate,
        ColumnMetadata.named("BIRTHDATE").withIndex(4).ofType(Types.TIMESTAMP).withSize(19));
    addMetadata(
        bodyweight,
        ColumnMetadata.named("BODYWEIGHT").withIndex(5).ofType(Types.DOUBLE).withSize(22));
    addMetadata(
        color, ColumnMetadata.named("COLOR").withIndex(6).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        datefield, ColumnMetadata.named("DATEFIELD").withIndex(7).ofType(Types.DATE).withSize(10));
    addMetadata(
        floatproperty,
        ColumnMetadata.named("FLOATPROPERTY").withIndex(8).ofType(Types.REAL).withSize(12));
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(9).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        timefield, ColumnMetadata.named("TIMEFIELD").withIndex(10).ofType(Types.TIME).withSize(8));
    addMetadata(
        toes, ColumnMetadata.named("TOES").withIndex(11).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        weight, ColumnMetadata.named("WEIGHT").withIndex(12).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        breed, ColumnMetadata.named("BREED").withIndex(13).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        eyecolor,
        ColumnMetadata.named("EYECOLOR").withIndex(14).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        mateId, ColumnMetadata.named("MATE_ID").withIndex(15).ofType(Types.INTEGER).withSize(10));
  }
}
