package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SBar_ is a Querydsl query type for SBar_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBar_ extends com.querydsl.sql.RelationalPathBase<SBar_> {

  private static final long serialVersionUID = 1493110580;

  public static final SBar_ bar_ = new SBar_("bar_");

  public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SBar_> primary = createPrimaryKey(id);

  public SBar_(String variable) {
    super(SBar_.class, forVariable(variable), "null", "bar_");
    addMetadata();
  }

  public SBar_(String variable, String schema, String table) {
    super(SBar_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBar_(String variable, String schema) {
    super(SBar_.class, forVariable(variable), schema, "bar_");
    addMetadata();
  }

  public SBar_(Path<? extends SBar_> path) {
    super(path.getType(), path.getMetadata(), "null", "bar_");
    addMetadata();
  }

  public SBar_(PathMetadata metadata) {
    super(SBar_.class, metadata, "null", "bar_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(date, ColumnMetadata.named("DATE").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
