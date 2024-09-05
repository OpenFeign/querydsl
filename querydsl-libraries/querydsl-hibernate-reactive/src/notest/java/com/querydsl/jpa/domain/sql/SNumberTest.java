package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNumberTest is a Querydsl query type for SNumberTest */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SNumberTest extends com.querydsl.sql.RelationalPathBase<SNumberTest> {

  private static final long serialVersionUID = -321238525;

  public static final SNumberTest numberTest = new SNumberTest("NUMBER_TEST");

  public final BooleanPath col1 = createBoolean("col1");

  public SNumberTest(String variable) {
    super(SNumberTest.class, forVariable(variable), "null", "NUMBER_TEST");
    addMetadata();
  }

  public SNumberTest(String variable, String schema, String table) {
    super(SNumberTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNumberTest(String variable, String schema) {
    super(SNumberTest.class, forVariable(variable), schema, "NUMBER_TEST");
    addMetadata();
  }

  public SNumberTest(Path<? extends SNumberTest> path) {
    super(path.getType(), path.getMetadata(), "null", "NUMBER_TEST");
    addMetadata();
  }

  public SNumberTest(PathMetadata metadata) {
    super(SNumberTest.class, metadata, "null", "NUMBER_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(col1, ColumnMetadata.named("col1").withIndex(1).ofType(Types.BIT).withSize(1));
  }
}
