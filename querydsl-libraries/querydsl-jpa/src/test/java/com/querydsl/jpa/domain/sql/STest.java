package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** STest is a Querydsl query type for STest */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class STest extends com.querydsl.sql.RelationalPathBase<STest> {

  private static final long serialVersionUID = 1493650714;

  public static final STest test = new STest("TEST");

  public final StringPath name = createString("name");

  public STest(String variable) {
    super(STest.class, forVariable(variable), "null", "TEST");
    addMetadata();
  }

  public STest(String variable, String schema, String table) {
    super(STest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public STest(String variable, String schema) {
    super(STest.class, forVariable(variable), schema, "TEST");
    addMetadata();
  }

  public STest(Path<? extends STest> path) {
    super(path.getType(), path.getMetadata(), "null", "TEST");
    addMetadata();
  }

  public STest(PathMetadata metadata) {
    super(STest.class, metadata, "null", "TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(255));
  }
}
