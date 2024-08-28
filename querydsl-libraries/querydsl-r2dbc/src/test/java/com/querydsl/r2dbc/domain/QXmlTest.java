package com.querydsl.r2dbc.domain;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPathBase;
import jakarta.annotation.Generated;
import java.sql.Types;

/** QXmlTest is a Querydsl query type for QXmlTest */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QXmlTest extends RelationalPathBase<QXmlTest> {

  private static final long serialVersionUID = 574759316;

  public static final QXmlTest xmlTest = new QXmlTest("XML_TEST");

  public final StringPath col = createString("COL");

  public QXmlTest(String variable) {
    super(QXmlTest.class, forVariable(variable), "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public QXmlTest(String variable, String schema, String table) {
    super(QXmlTest.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public QXmlTest(Path<? extends QXmlTest> path) {
    super(path.getType(), path.getMetadata(), "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public QXmlTest(PathMetadata metadata) {
    super(QXmlTest.class, metadata, "PUBLIC", "XML_TEST");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        col, ColumnMetadata.named("COL").withIndex(1).ofType(Types.SQLXML).withSize(2147483647));
  }
}
