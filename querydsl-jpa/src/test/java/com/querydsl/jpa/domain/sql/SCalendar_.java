package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCalendar_ is a Querydsl query type for SCalendar_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCalendar_ extends com.querydsl.sql.RelationalPathBase<SCalendar_> {

  private static final long serialVersionUID = -953714343;

  public static final SCalendar_ calendar_ = new SCalendar_("calendar_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SCalendar_> primary = createPrimaryKey(id);

  public SCalendar_(String variable) {
    super(SCalendar_.class, forVariable(variable), "null", "calendar_");
    addMetadata();
  }

  public SCalendar_(String variable, String schema, String table) {
    super(SCalendar_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCalendar_(String variable, String schema) {
    super(SCalendar_.class, forVariable(variable), schema, "calendar_");
    addMetadata();
  }

  public SCalendar_(Path<? extends SCalendar_> path) {
    super(path.getType(), path.getMetadata(), "null", "calendar_");
    addMetadata();
  }

  public SCalendar_(PathMetadata metadata) {
    super(SCalendar_.class, metadata, "null", "calendar_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
