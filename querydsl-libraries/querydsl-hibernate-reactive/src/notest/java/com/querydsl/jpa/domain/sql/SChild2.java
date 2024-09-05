package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SChild2 is a Querydsl query type for SChild2 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SChild2 extends com.querydsl.sql.RelationalPathBase<SChild2> {

  private static final long serialVersionUID = 395031838;

  public static final SChild2 child2 = new SChild2("CHILD2");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final NumberPath<Integer> parentId = createNumber("parentId", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SChild2> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SParent2> child2ParentIdFk =
      createForeignKey(parentId, "ID");

  public SChild2(String variable) {
    super(SChild2.class, forVariable(variable), "null", "CHILD2");
    addMetadata();
  }

  public SChild2(String variable, String schema, String table) {
    super(SChild2.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SChild2(String variable, String schema) {
    super(SChild2.class, forVariable(variable), schema, "CHILD2");
    addMetadata();
  }

  public SChild2(Path<? extends SChild2> path) {
    super(path.getType(), path.getMetadata(), "null", "CHILD2");
    addMetadata();
  }

  public SChild2(PathMetadata metadata) {
    super(SChild2.class, metadata, "null", "CHILD2");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        parentId,
        ColumnMetadata.named("PARENT_ID").withIndex(2).ofType(Types.INTEGER).withSize(10));
  }
}
