package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SInheritedproperties_ is a Querydsl query type for SInheritedproperties_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SInheritedproperties_
    extends com.querydsl.sql.RelationalPathBase<SInheritedproperties_> {

  private static final long serialVersionUID = -515155206;

  public static final SInheritedproperties_ inheritedproperties_ =
      new SInheritedproperties_("inheritedproperties_");

  public final StringPath classproperty = createString("classproperty");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath stringassimple = createString("stringassimple");

  public final StringPath superclassproperty = createString("superclassproperty");

  public final com.querydsl.sql.PrimaryKey<SInheritedproperties_> primary = createPrimaryKey(id);

  public SInheritedproperties_(String variable) {
    super(SInheritedproperties_.class, forVariable(variable), "null", "inheritedproperties_");
    addMetadata();
  }

  public SInheritedproperties_(String variable, String schema, String table) {
    super(SInheritedproperties_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SInheritedproperties_(String variable, String schema) {
    super(SInheritedproperties_.class, forVariable(variable), schema, "inheritedproperties_");
    addMetadata();
  }

  public SInheritedproperties_(Path<? extends SInheritedproperties_> path) {
    super(path.getType(), path.getMetadata(), "null", "inheritedproperties_");
    addMetadata();
  }

  public SInheritedproperties_(PathMetadata metadata) {
    super(SInheritedproperties_.class, metadata, "null", "inheritedproperties_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        classproperty,
        ColumnMetadata.named("CLASSPROPERTY").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        stringassimple,
        ColumnMetadata.named("STRINGASSIMPLE").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        superclassproperty,
        ColumnMetadata.named("SUPERCLASSPROPERTY")
            .withIndex(4)
            .ofType(Types.VARCHAR)
            .withSize(255));
  }
}
