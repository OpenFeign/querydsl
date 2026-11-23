package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SKittensSet is a FluentQ query type for SKittensSet */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SKittensSet extends fluentq.sql.RelationalPathBase<SKittensSet> {

  private static final long serialVersionUID = -1409901698;

  public static final SKittensSet kittensSet = new SKittensSet("kittens_set");

  public final NumberPath<Integer> catId = createNumber("catId", Integer.class);

  public final NumberPath<Integer> kittenId = createNumber("kittenId", Integer.class);

  public final fluentq.sql.PrimaryKey<SKittensSet> primary = createPrimaryKey(catId, kittenId);

  public final fluentq.sql.ForeignKey<SAnimal_> kittensSetCatIdFK = createForeignKey(catId, "ID");

  public final fluentq.sql.ForeignKey<SAnimal_> kittensSetKittenIdFK =
      createForeignKey(kittenId, "ID");

  public SKittensSet(String variable) {
    super(SKittensSet.class, forVariable(variable), "null", "kittens_set");
    addMetadata();
  }

  public SKittensSet(String variable, String schema, String table) {
    super(SKittensSet.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SKittensSet(String variable, String schema) {
    super(SKittensSet.class, forVariable(variable), schema, "kittens_set");
    addMetadata();
  }

  public SKittensSet(Path<? extends SKittensSet> path) {
    super(path.getType(), path.getMetadata(), "null", "kittens_set");
    addMetadata();
  }

  public SKittensSet(PathMetadata metadata) {
    super(SKittensSet.class, metadata, "null", "kittens_set");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        catId,
        ColumnMetadata.named("cat_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        kittenId,
        ColumnMetadata.named("kitten_id")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
  }
}
