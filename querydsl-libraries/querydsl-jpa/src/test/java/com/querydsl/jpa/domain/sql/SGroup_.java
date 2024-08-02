package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SGroup_ is a Querydsl query type for SGroup_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SGroup_ extends com.querydsl.sql.RelationalPathBase<SGroup_> {

  private static final long serialVersionUID = 518971464;

  public static final SGroup_ group_ = new SGroup_("group_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SGroup_> primary = createPrimaryKey(id);

  public SGroup_(String variable) {
    super(SGroup_.class, forVariable(variable), "null", "group_");
    addMetadata();
  }

  public SGroup_(String variable, String schema, String table) {
    super(SGroup_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SGroup_(String variable, String schema) {
    super(SGroup_.class, forVariable(variable), schema, "group_");
    addMetadata();
  }

  public SGroup_(Path<? extends SGroup_> path) {
    super(path.getType(), path.getMetadata(), "null", "group_");
    addMetadata();
  }

  public SGroup_(PathMetadata metadata) {
    super(SGroup_.class, metadata, "null", "group_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
