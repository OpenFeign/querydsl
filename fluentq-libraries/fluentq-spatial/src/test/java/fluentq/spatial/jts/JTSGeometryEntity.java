package fluentq.spatial.jts;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import fluentq.core.annotations.QueryEntity;

@QueryEntity
public class JTSGeometryEntity {

  Geometry geometry;

  GeometryCollection geometryCollection;

  LinearRing linearRing;

  LineString lineString;

  MultiLineString multiLineString;

  MultiPoint multiPoint;

  MultiPolygon multiPolygon;

  Point point;

  Polygon polygon;
}
