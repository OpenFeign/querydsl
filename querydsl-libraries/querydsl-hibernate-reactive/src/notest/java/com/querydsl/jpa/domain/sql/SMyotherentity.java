package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SMyotherentity is a Querydsl query type for SMyotherentity */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SMyotherentity extends com.querydsl.sql.RelationalPathBase<SMyotherentity> {

  private static final long serialVersionUID = -19540417;

  public static final SMyotherentity myotherentity = new SMyotherentity("MYOTHERENTITY");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SMyotherentity> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SMyentity> _myentityAttributewithinitproblemIdFk =
      createInvForeignKey(id, "ATTRIBUTEWITHINITPROBLEM_ID");

  public SMyotherentity(String variable) {
    super(SMyotherentity.class, forVariable(variable), "null", "MYOTHERENTITY");
    addMetadata();
  }

  public SMyotherentity(String variable, String schema, String table) {
    super(SMyotherentity.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SMyotherentity(String variable, String schema) {
    super(SMyotherentity.class, forVariable(variable), schema, "MYOTHERENTITY");
    addMetadata();
  }

  public SMyotherentity(Path<? extends SMyotherentity> path) {
    super(path.getType(), path.getMetadata(), "null", "MYOTHERENTITY");
    addMetadata();
  }

  public SMyotherentity(PathMetadata metadata) {
    super(SMyotherentity.class, metadata, "null", "MYOTHERENTITY");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
