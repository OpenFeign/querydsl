package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SMyentity is a Querydsl query type for SMyentity */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SMyentity extends com.querydsl.sql.RelationalPathBase<SMyentity> {

  private static final long serialVersionUID = -287734505;

  public static final SMyentity myentity = new SMyentity("MYENTITY");

  public final NumberPath<Integer> attributewithinitproblemId =
      createNumber("attributewithinitproblemId", Integer.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SMyentity> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SMyotherentity> myentityAttributewithinitproblemIdFk =
      createForeignKey(attributewithinitproblemId, "ID");

  public SMyentity(String variable) {
    super(SMyentity.class, forVariable(variable), "null", "MYENTITY");
    addMetadata();
  }

  public SMyentity(String variable, String schema, String table) {
    super(SMyentity.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SMyentity(String variable, String schema) {
    super(SMyentity.class, forVariable(variable), schema, "MYENTITY");
    addMetadata();
  }

  public SMyentity(Path<? extends SMyentity> path) {
    super(path.getType(), path.getMetadata(), "null", "MYENTITY");
    addMetadata();
  }

  public SMyentity(PathMetadata metadata) {
    super(SMyentity.class, metadata, "null", "MYENTITY");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        attributewithinitproblemId,
        ColumnMetadata.named("ATTRIBUTEWITHINITPROBLEM_ID")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
