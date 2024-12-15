package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SShow_ is a Querydsl query type for SShow_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SShow_ extends com.querydsl.sql.RelationalPathBase<SShow_> {

  private static final long serialVersionUID = -942305926;

  public static final SShow_ show_ = new SShow_("show_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SShow_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SShow_> show_PARENTIDFK =
      createForeignKey(parentId, "ID");

  public final com.querydsl.sql.ForeignKey<SShowACTS> _showACTSShowIDFK =
      createInvForeignKey(id, "Show_ID");

  public final com.querydsl.sql.ForeignKey<SShow_> _show_PARENTIDFK =
      createInvForeignKey(id, "PARENT_ID");

  public SShow_(String variable) {
    super(SShow_.class, forVariable(variable), "null", "show_");
    addMetadata();
  }

  public SShow_(String variable, String schema, String table) {
    super(SShow_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SShow_(String variable, String schema) {
    super(SShow_.class, forVariable(variable), schema, "show_");
    addMetadata();
  }

  public SShow_(Path<? extends SShow_> path) {
    super(path.getType(), path.getMetadata(), "null", "show_");
    addMetadata();
  }

  public SShow_(PathMetadata metadata) {
    super(SShow_.class, metadata, "null", "show_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        parentId, ColumnMetadata.named("PARENT_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
