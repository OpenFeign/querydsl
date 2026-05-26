package fluentq.sql.spatial;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.spatial.GeometryPath;
import fluentq.sql.ColumnMetadata;
import jakarta.annotation.Generated;
import org.geolatte.geom.Geometry;

/** QShapes is a FluentQ query type for QShapes */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class QShapes extends RelationalPathSpatial<Shapes> {

  private static final long serialVersionUID = 563213127;

  public static final QShapes shapes = new QShapes("SHAPES");

  public final GeometryPath<Geometry> geometry = createGeometry("geometry", Geometry.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final fluentq.sql.PrimaryKey<Shapes> shapesPkey = createPrimaryKey(id);

  public QShapes(String variable) {
    super(Shapes.class, forVariable(variable), "PUBLIC", "SHAPES");
    addMetadata();
  }

  public QShapes(String variable, String schema, String table) {
    super(Shapes.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public QShapes(Path<? extends Shapes> path) {
    super(path.getType(), path.getMetadata(), "PUBLIC", "SHAPES");
    addMetadata();
  }

  public QShapes(PathMetadata metadata) {
    super(Shapes.class, metadata, "PUBLIC", "SHAPES");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(geometry, ColumnMetadata.named("GEOMETRY").ofType(1111).withSize(2147483647));
    addMetadata(id, ColumnMetadata.named("ID").ofType(4).withSize(10).notNull());
  }
}
