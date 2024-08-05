package com.querydsl.sql.spatial;

import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.core.Target.TERADATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;

import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.spatial.GeometryExpression;
import com.querydsl.spatial.GeometryExpressions;
import com.querydsl.spatial.PointExpression;
import com.querydsl.sql.AbstractBaseTest;
import com.querydsl.sql.SQLQuery;
import java.util.ArrayList;
import java.util.List;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.MultiPoint;
import org.geolatte.geom.MultiPolygon;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.ProjectedGeometryOperations;
import org.geolatte.geom.codec.Wkt;
import org.junit.Test;

public class SpatialBase extends AbstractBaseTest {

  private static final QShapes shapes = QShapes.shapes;

  // point 1-5
  // linestring 6-7
  // polygon 8-9
  // multipoint 10-11
  // multilinestring 12-13
  // multipolygon 14-15

  private SQLQuery<?> withPoints() {
    return query().from(shapes).where(shapes.id.between(1, 5));
  }

  private SQLQuery<?> withLineStrings() {
    return query().from(shapes).where(shapes.id.between(6, 7));
  }

  private SQLQuery<?> withPolygons() {
    return query().from(shapes).where(shapes.id.between(8, 9));
  }

  private SQLQuery<?> withMultipoints() {
    return query().from(shapes).where(shapes.id.between(10, 11));
  }

  private SQLQuery<?> withMultiLineStrings() {
    return query().from(shapes).where(shapes.id.between(12, 13));
  }

  private SQLQuery<?> withMultiPolygons() {
    return query().from(shapes).where(shapes.id.between(14, 15));
  }

  @Test
  @IncludeIn(POSTGRESQL)
  public void spatialRefSys() {
    var spatialRefSys = QSpatialRefSys.spatialRefSys;
    query().from(spatialRefSys).select(spatialRefSys).fetch();
  }

  private String normalize(String s) {
    var normalized = s.replace(" ", "").replace("ST_", "").replace("_", "");
    normalized = normalized.substring(normalized.indexOf(';') + 1);
    return normalized.toUpperCase();
  }

  @Test // FIXME, maybe use enum as the type ?!?
  @ExcludeIn(H2)
  public void geometryType() {
    var results =
        query().from(shapes).select(shapes.geometry, shapes.geometry.geometryType()).fetch();
    assertThat(results).isNotEmpty();
    for (Tuple row : results) {
      assertThat(normalize(row.get(shapes.geometry.geometryType())))
          .isEqualTo(normalize(row.get(shapes.geometry).getGeometryType().name()));
    }
  }

  @Test
  public void asText() {
    var results = query().from(shapes).select(shapes.geometry, shapes.geometry.asText()).fetch();
    assertThat(results).isNotEmpty();
    for (Tuple row : results) {
      if (!(row.get(shapes.geometry) instanceof MultiPoint)) {
        assertThat(normalize(row.get(shapes.geometry.asText())))
            .isEqualTo(normalize(Wkt.toWkt(row.get(shapes.geometry))));
      }
    }
  }

  @Test
  @ExcludeIn(H2)
  public void point_x_y() {
    var point = shapes.geometry.asPoint();
    var results = withPoints().select(point, point.x(), point.y()).fetch();
    assertThat(results).isNotEmpty();
    for (Tuple row : results) {
      assertThat(row.get(point.x()))
          .isEqualTo(Double.valueOf(row.get(point).getPosition().getCoordinate(0)));
      assertThat(row.get(point.y()))
          .isEqualTo(Double.valueOf(row.get(point).getPosition().getCoordinate(1)));
    }
  }

  @Test
  @ExcludeIn(MYSQL)
  public void point_distance() {
    var shapes1 = QShapes.shapes;
    var shapes2 = new QShapes("shapes2");
    for (Tuple tuple :
        query()
            .from(shapes1, shapes2)
            .where(shapes1.id.loe(5), shapes2.id.loe(5))
            .select(
                shapes1.geometry.asPoint(),
                shapes2.geometry.asPoint(),
                shapes1.geometry.distance(shapes2.geometry))
            .fetch()) {
      Point point1 = tuple.get(shapes1.geometry.asPoint());
      Point point2 = tuple.get(shapes2.geometry.asPoint());
      Double distance = tuple.get(shapes1.geometry.distance(shapes2.geometry));
      assertThat(distance)
          .isCloseTo(ProjectedGeometryOperations.Default.distance(point1, point2), within(0.0001));
    }
  }

  @Test
  public void point_instances() {
    var results = withPoints().select(shapes).fetch();
    assertThat(results).hasSize(5);
    for (Shapes row : results) {
      assertThat(row.getId()).isNotNull();
      assertThat(row.getGeometry()).isNotNull();
      assertThat(row.getGeometry() instanceof Point).isTrue();
    }
  }

  @Test
  public void lineString_instances() {
    var results = withLineStrings().select(shapes.geometry).fetch();
    assertThat(results).isNotEmpty();
    for (Geometry row : results) {
      assertThat(row).isNotNull();
      assertThat(row instanceof LineString).isTrue();
    }
  }

  @Test
  public void polygon_instances() {
    var results = withPolygons().select(shapes.geometry).fetch();
    assertThat(results).isNotEmpty();
    for (Geometry row : results) {
      assertThat(row).isNotNull();
      assertThat(row instanceof Polygon).isTrue();
    }
  }

  @Test
  public void multiPoint_instances() {
    var results = withMultipoints().select(shapes.geometry).fetch();
    assertThat(results).isNotEmpty();
    for (Geometry row : results) {
      assertThat(row).isNotNull();
      assertThat(row instanceof MultiPoint).isTrue();
    }
  }

  @Test
  public void multiLineString_instances() {
    var results = withMultiLineStrings().select(shapes.geometry).fetch();
    assertThat(results).isNotEmpty();
    for (Geometry row : results) {
      assertThat(row).isNotNull();
      assertThat(row instanceof MultiLineString).isTrue();
    }
  }

  @Test
  public void multiPolygon_instances() {
    var results = withMultiPolygons().select(shapes.geometry).fetch();
    assertThat(results).isNotEmpty();
    for (Geometry row : results) {
      assertThat(row).isNotNull();
      assertThat(row instanceof MultiPolygon).isTrue();
    }
  }

  @Test
  public void point_methods() {
    var point = shapes.geometry.asPoint();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, point.asBinary(), H2);
    add(expressions, point.asText());
    add(expressions, point.boundary(), H2, MYSQL, POSTGRESQL);
    add(expressions, point.convexHull(), H2, MYSQL);
    add(expressions, point.dimension());
    add(expressions, point.envelope(), H2);
    add(expressions, point.geometryType(), H2);
    add(expressions, point.isEmpty());
    add(expressions, point.isSimple());
    add(expressions, point.m(), MYSQL, TERADATA, H2);
    add(expressions, point.srid());
    // TODO add emulations
    add(expressions, point.transform(26986), MYSQL, POSTGRESQL, SQLSERVER, TERADATA, H2);
    // point specific
    add(expressions, point.x(), H2);
    add(expressions, point.y(), H2);
    add(expressions, point.z(), MYSQL, TERADATA, H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      try {
        for (Object row : withPoints().select(expr).fetch()) {
          if (row == null && !logged) {
            System.err.println(expr.toString());
            logged = true;
          }
        }
      } catch (Exception e) {
        fail("Error with expression " + expr, e);
      }
    }
  }

  private List<Expression<?>> createExpressions(
      PointExpression<Point> point1, Expression<Point> point2) {
    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, point1.contains(point2));
    add(expressions, point1.crosses(point2));
    add(expressions, point1.difference(point2), H2, MYSQL, POSTGRESQL);
    add(expressions, point1.disjoint(point2));
    add(expressions, point1.distance(point2), MYSQL);
    add(expressions, point1.distanceSphere(point2), H2, MYSQL, POSTGRESQL, SQLSERVER);
    add(expressions, point1.distanceSpheroid(point2), H2, MYSQL, POSTGRESQL, SQLSERVER);
    add(expressions, point1.eq(point2));
    add(expressions, point1.intersection(point2), H2, MYSQL, POSTGRESQL);
    add(expressions, point1.intersects(point2));
    add(expressions, point1.overlaps(point2));
    add(expressions, point1.symDifference(point2), H2, MYSQL, POSTGRESQL);
    add(expressions, point1.touches(point2));
    add(expressions, point1.union(point2), H2, MYSQL);
    add(expressions, point1.within(point2));
    return expressions;
  }

  @Test
  @ExcludeIn(H2)
  public void point_methods2() {
    var shapes1 = QShapes.shapes;
    var shapes2 = new QShapes("shapes2");

    List<Expression<?>> expressions = new ArrayList<>();
    expressions.addAll(createExpressions(shapes1.geometry.asPoint(), shapes2.geometry.asPoint()));
    expressions.addAll(
        createExpressions(
            shapes1.geometry.asPoint(), ConstantImpl.create((Point) Wkt.fromWkt("Point(2 2)"))));

    for (Expression<?> expr : expressions) {
      var logged = false;
      try {
        for (Object row :
            query()
                .from(shapes1, shapes2)
                .where(shapes1.id.loe(5), shapes2.id.loe(5))
                .select(expr)
                .fetch()) {
          if (row == null && !logged) {
            System.err.println(expr.toString());
            logged = true;
          }
        }
      } catch (Exception e) {
        fail("Error with expression " + expr, e);
      }
    }
  }

  @Test
  public void lineString_methods() {
    var lineString = shapes.geometry.asLineString();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, lineString.asBinary(), H2);
    add(expressions, lineString.asText());
    add(expressions, lineString.boundary(), H2, MYSQL);
    add(expressions, lineString.convexHull(), H2, MYSQL);
    add(expressions, lineString.dimension());
    add(expressions, lineString.envelope(), H2);
    add(expressions, lineString.geometryType(), H2);
    add(expressions, lineString.isEmpty());
    add(expressions, lineString.isSimple());
    // curve specific
    add(expressions, lineString.length(), H2);
    add(expressions, lineString.startPoint(), H2);
    add(expressions, lineString.endPoint(), H2);
    add(expressions, lineString.isClosed(), H2);
    add(expressions, lineString.isRing(), H2, MYSQL);
    // linestring specific
    add(expressions, lineString.numPoints(), H2);
    add(expressions, lineString.pointN(1), H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withLineStrings().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }

  @Test
  public void polygon_methods() {
    var polygon = shapes.geometry.asPolygon();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, polygon.asBinary(), H2);
    add(expressions, polygon.asText());
    add(expressions, polygon.boundary(), H2, MYSQL);
    add(expressions, polygon.convexHull(), H2, MYSQL);
    add(expressions, polygon.dimension());
    add(expressions, polygon.envelope(), H2);
    add(expressions, polygon.geometryType(), H2);
    add(expressions, polygon.isEmpty());
    add(expressions, polygon.isSimple());
    // surface specific
    add(expressions, polygon.area());
    add(expressions, polygon.centroid());
    add(expressions, polygon.pointOnSurface(), H2, MYSQL);
    // polygon specific
    add(expressions, polygon.exteriorRing(), H2);
    add(expressions, polygon.numInteriorRing(), H2);
    add(expressions, polygon.interiorRingN(1), H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withPolygons().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }

  @Test
  public void multiPoint_methods() {
    var multipoint = shapes.geometry.asMultiPoint();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, multipoint.asBinary(), H2);
    add(expressions, multipoint.asText());
    add(expressions, multipoint.boundary(), H2, MYSQL);
    add(expressions, multipoint.convexHull(), H2, MYSQL);
    add(expressions, multipoint.dimension());
    add(expressions, multipoint.envelope(), H2);
    add(expressions, multipoint.geometryType(), H2);
    add(expressions, multipoint.isEmpty());
    add(expressions, multipoint.isSimple());
    // multipoint specific
    add(expressions, multipoint.numGeometries(), H2);
    add(expressions, multipoint.geometryN(1), H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withMultipoints().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }

  @Test
  public void multiLineString_methods() {
    var multilinestring = shapes.geometry.asMultiLineString();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, multilinestring.asBinary(), H2);
    add(expressions, multilinestring.asText());
    add(expressions, multilinestring.boundary(), H2, MYSQL);
    add(expressions, multilinestring.convexHull(), H2, MYSQL);
    add(expressions, multilinestring.dimension());
    add(expressions, multilinestring.envelope(), H2);
    add(expressions, multilinestring.geometryType(), H2);
    add(expressions, multilinestring.isEmpty());
    add(expressions, multilinestring.isSimple());
    // multicurve specific
    add(expressions, multilinestring.isClosed(), H2);
    add(expressions, multilinestring.length(), H2);
    // multilinestring specific
    add(expressions, multilinestring.numGeometries(), H2);
    add(expressions, multilinestring.geometryN(1), H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withMultiLineStrings().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }

  @Test
  public void multiPolygon_methods() {
    var multipolygon = shapes.geometry.asMultiPolygon();

    List<Expression<?>> expressions = new ArrayList<>();
    add(expressions, multipolygon.asBinary(), H2);
    add(expressions, multipolygon.asText());
    add(expressions, multipolygon.boundary(), H2, MYSQL);
    add(expressions, multipolygon.convexHull(), H2, MYSQL);
    add(expressions, multipolygon.dimension());
    add(expressions, multipolygon.envelope(), H2);
    add(expressions, multipolygon.geometryType(), H2);
    add(expressions, multipolygon.isEmpty());
    add(expressions, multipolygon.isSimple());
    // multipolygon specific
    add(expressions, multipolygon.numGeometries(), H2);
    add(expressions, multipolygon.geometryN(1), H2);

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withMultiPolygons().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }

  @Test
  @IncludeIn(Target.POSTGRESQL)
  public void extensions() {
    List<Expression<?>> expressions = new ArrayList<>();
    GeometryExpression<?> expr1 = shapes.geometry;

    expressions.add(GeometryExpressions.asEWKT(expr1));
    expressions.add(GeometryExpressions.fromText(expr1.asText()));
    expressions.add(GeometryExpressions.setSRID(expr1, 4326));
    expressions.add(GeometryExpressions.xmin(expr1));
    expressions.add(GeometryExpressions.xmax(expr1));
    expressions.add(GeometryExpressions.ymin(expr1));
    expressions.add(GeometryExpressions.ymax(expr1));
    expressions.add(GeometryExpressions.dwithin(expr1, expr1, 1));
    expressions.add(GeometryExpressions.collect(expr1, expr1));
    expressions.add(GeometryExpressions.translate(expr1, 1, 1));

    for (Expression<?> expr : expressions) {
      var logged = false;
      for (Object row : withPoints().select(expr).fetch()) {
        if (row == null && !logged) {
          System.err.println(expr.toString());
          logged = true;
        }
      }
    }
  }
}
