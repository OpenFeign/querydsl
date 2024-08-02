package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SXmlTest is a Querydsl query type for SXmlTest */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SXmlTest extends com.querydsl.sql.RelationalPathBase<SXmlTest> {

  private static final long serialVersionUID = 963997633;

  public static final SXmlTest xmlTest = new SXmlTest("XML_TEST");

  public final StringPath col = createString("col");

  public SXmlTest(String variable) {
    super(SXmlTest.class, forVariable(variable), "null", "XML_TEST");
    addMetadata();
  }

  public SXmlTest(String variable, String schema, String table) {
    super(SXmlTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SXmlTest(String variable, String schema) {
    super(SXmlTest.class, forVariable(variable), schema, "XML_TEST");
    addMetadata();
  }

  public SXmlTest(Path<? extends SXmlTest> path) {
    super(path.getType(), path.getMetadata(), "null", "XML_TEST");
    addMetadata();
  }

  public SXmlTest(PathMetadata metadata) {
    super(SXmlTest.class, metadata, "null", "XML_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(col, ColumnMetadata.named("COL").withIndex(1).ofType(Types.VARCHAR).withSize(128));
  }
}
