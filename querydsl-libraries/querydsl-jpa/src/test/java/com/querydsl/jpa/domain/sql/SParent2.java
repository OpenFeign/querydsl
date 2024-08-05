package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SParent2 is a Querydsl query type for SParent2 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SParent2 extends com.querydsl.sql.RelationalPathBase<SParent2> {

  private static final long serialVersionUID = 2116409152;

  public static final SParent2 parent2 = new SParent2("PARENT2");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SParent2> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SChild2> _child2ParentIdFk =
      createInvForeignKey(id, "PARENT_ID");

  public SParent2(String variable) {
    super(SParent2.class, forVariable(variable), "null", "PARENT2");
    addMetadata();
  }

  public SParent2(String variable, String schema, String table) {
    super(SParent2.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SParent2(String variable, String schema) {
    super(SParent2.class, forVariable(variable), schema, "PARENT2");
    addMetadata();
  }

  public SParent2(Path<? extends SParent2> path) {
    super(path.getType(), path.getMetadata(), "null", "PARENT2");
    addMetadata();
  }

  public SParent2(PathMetadata metadata) {
    super(SParent2.class, metadata, "null", "PARENT2");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
