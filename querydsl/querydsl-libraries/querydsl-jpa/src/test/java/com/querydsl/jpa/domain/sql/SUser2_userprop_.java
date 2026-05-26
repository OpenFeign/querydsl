package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SUser2_userprop_ is a Querydsl query type for SUser2_userprop_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SUser2_userprop_ extends com.querydsl.sql.RelationalPathBase<SUser2_userprop_> {

  private static final long serialVersionUID = 770151025;

  public static final SUser2_userprop_ user2_userprop_ = new SUser2_userprop_("user2__userprop_");

  public final NumberPath<Long> propertiesID = createNumber("propertiesID", Long.class);

  public final NumberPath<Long> user2ID = createNumber("user2ID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SUser2_userprop_> primary =
      createPrimaryKey(user2ID, propertiesID);

  public final com.querydsl.sql.ForeignKey<SUser2_> user2_userprop_User2IDFK =
      createForeignKey(user2ID, "ID");

  public final com.querydsl.sql.ForeignKey<SUserprop_> user2_userprop_propertiesIDFK =
      createForeignKey(propertiesID, "ID");

  public SUser2_userprop_(String variable) {
    super(SUser2_userprop_.class, forVariable(variable), "null", "user2__userprop_");
    addMetadata();
  }

  public SUser2_userprop_(String variable, String schema, String table) {
    super(SUser2_userprop_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SUser2_userprop_(String variable, String schema) {
    super(SUser2_userprop_.class, forVariable(variable), schema, "user2__userprop_");
    addMetadata();
  }

  public SUser2_userprop_(Path<? extends SUser2_userprop_> path) {
    super(path.getType(), path.getMetadata(), "null", "user2__userprop_");
    addMetadata();
  }

  public SUser2_userprop_(PathMetadata metadata) {
    super(SUser2_userprop_.class, metadata, "null", "user2__userprop_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        propertiesID,
        ColumnMetadata.named("properties_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        user2ID,
        ColumnMetadata.named("User2_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
