package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SShowACTS is a Querydsl query type for SShowACTS */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SShowACTS extends com.querydsl.sql.RelationalPathBase<SShowACTS> {

  private static final long serialVersionUID = -330421466;

  public static final SShowACTS ShowACTS = new SShowACTS("Show_ACTS");

  public final StringPath acts = createString("acts");

  public final StringPath actsKey = createString("actsKey");

  public final NumberPath<Long> showID = createNumber("showID", Long.class);

  public final com.querydsl.sql.ForeignKey<SShow_> showACTSShowIDFK =
      createForeignKey(showID, "ID");

  public SShowACTS(String variable) {
    super(SShowACTS.class, forVariable(variable), "null", "Show_ACTS");
    addMetadata();
  }

  public SShowACTS(String variable, String schema, String table) {
    super(SShowACTS.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SShowACTS(String variable, String schema) {
    super(SShowACTS.class, forVariable(variable), schema, "Show_ACTS");
    addMetadata();
  }

  public SShowACTS(Path<? extends SShowACTS> path) {
    super(path.getType(), path.getMetadata(), "null", "Show_ACTS");
    addMetadata();
  }

  public SShowACTS(PathMetadata metadata) {
    super(SShowACTS.class, metadata, "null", "Show_ACTS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        acts, ColumnMetadata.named("ACTS").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        actsKey, ColumnMetadata.named("acts_key").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        showID, ColumnMetadata.named("Show_ID").withIndex(1).ofType(Types.BIGINT).withSize(19));
  }
}
