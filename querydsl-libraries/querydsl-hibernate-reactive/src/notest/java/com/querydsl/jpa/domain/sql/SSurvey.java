package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SSurvey is a Querydsl query type for SSurvey */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SSurvey extends com.querydsl.sql.RelationalPathBase<SSurvey> {

  private static final long serialVersionUID = 865381858;

  public static final SSurvey survey = new SSurvey("SURVEY");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final StringPath name2 = createString("name2");

  public final com.querydsl.sql.PrimaryKey<SSurvey> primary = createPrimaryKey(id);

  public SSurvey(String variable) {
    super(SSurvey.class, forVariable(variable), "null", "SURVEY");
    addMetadata();
  }

  public SSurvey(String variable, String schema, String table) {
    super(SSurvey.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SSurvey(String variable, String schema) {
    super(SSurvey.class, forVariable(variable), schema, "SURVEY");
    addMetadata();
  }

  public SSurvey(Path<? extends SSurvey> path) {
    super(path.getType(), path.getMetadata(), "null", "SURVEY");
    addMetadata();
  }

  public SSurvey(PathMetadata metadata) {
    super(SSurvey.class, metadata, "null", "SURVEY");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(30));
    addMetadata(
        name2, ColumnMetadata.named("NAME2").withIndex(3).ofType(Types.VARCHAR).withSize(30));
  }
}
