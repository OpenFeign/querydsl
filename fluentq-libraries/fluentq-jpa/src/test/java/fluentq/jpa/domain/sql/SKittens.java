package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SKittens is a FluentQ query type for SKittens */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SKittens extends fluentq.sql.RelationalPathBase<SKittens> {

  private static final long serialVersionUID = -2089790908;

  public static final SKittens kittens = new SKittens("kittens");

  public final NumberPath<Integer> catId = createNumber("catId", Integer.class);

  public final NumberPath<Integer> ind = createNumber("ind", Integer.class);

  public final NumberPath<Integer> kittenId = createNumber("kittenId", Integer.class);

  public final fluentq.sql.PrimaryKey<SKittens> primary = createPrimaryKey(catId, kittenId);

  public final fluentq.sql.ForeignKey<SAnimal_> kittensCatIdFK = createForeignKey(catId, "ID");

  public final fluentq.sql.ForeignKey<SAnimal_> kittensKittenIdFK =
      createForeignKey(kittenId, "ID");

  public SKittens(String variable) {
    super(SKittens.class, forVariable(variable), "null", "kittens");
    addMetadata();
  }

  public SKittens(String variable, String schema, String table) {
    super(SKittens.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SKittens(String variable, String schema) {
    super(SKittens.class, forVariable(variable), schema, "kittens");
    addMetadata();
  }

  public SKittens(Path<? extends SKittens> path) {
    super(path.getType(), path.getMetadata(), "null", "kittens");
    addMetadata();
  }

  public SKittens(PathMetadata metadata) {
    super(SKittens.class, metadata, "null", "kittens");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        catId,
        ColumnMetadata.named("cat_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(ind, ColumnMetadata.named("ind").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        kittenId,
        ColumnMetadata.named("kitten_id")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
  }
}
