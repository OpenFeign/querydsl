package com.querydsl.spatial.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Operator;
import com.querydsl.spatial.SpatialOps;
import org.junit.Test;

public class HibernateSpatialSupportTest {

  @Test
  public void allMapped() {
    var mapping = HibernateSpatialSupport.getSpatialOps();
    for (Operator operator : SpatialOps.values()) {
      assertThat(mapping.containsKey(operator)).as(operator + " missing").isTrue();
    }
  }

  @Test
  public void checkOperationsCorrectlyMapped() {
    var mapping = HibernateSpatialSupport.getSpatialOps();

    assertThat(mapping).containsEntry(SpatialOps.DIMENSION, "dimension({0})");
    assertThat(mapping).containsEntry(SpatialOps.GEOMETRY_TYPE, "geometrytype({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.SRID, "srid({0})");
    assertThat(mapping).containsEntry(SpatialOps.SRID2, "srid2({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.ENVELOPE, "envelope({0})");
    assertThat(mapping).containsEntry(SpatialOps.AS_TEXT, "astext({0})");
    assertThat(mapping).containsEntry(SpatialOps.AS_BINARY, "asbinary({0})");
    assertThat(mapping).containsEntry(SpatialOps.IS_EMPTY, "isempty({0})");
    assertThat(mapping).containsEntry(SpatialOps.IS_SIMPLE, "issimple({0})");
    assertThat(mapping).containsEntry(SpatialOps.BOUNDARY, "boundary({0})");
    assertThat(mapping).containsEntry(SpatialOps.EXTENT, "extent({0})");
    assertThat(mapping).containsEntry(SpatialOps.EQUALS, "equals({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.DISJOINT, "disjoint({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.INTERSECTS, "intersects({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.TOUCHES, "touches({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.CROSSES, "crosses({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.WITHIN, "within({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.CONTAINS, "contains({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.OVERLAPS, "overlaps({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.RELATE, "relate({0}, {1}, {2})");
    assertThat(mapping).containsEntry(SpatialOps.DISTANCE, "distance({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.DISTANCE2, "distance({0}, {1}, {2})");
    assertThat(mapping).containsEntry(SpatialOps.DISTANCE_SPHERE, "distancesphere({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.DISTANCE_SPHEROID, "distancespheroid({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.BUFFER, "buffer({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.BUFFER2, "buffer({0}, {1}, {2})");
    assertThat(mapping).containsEntry(SpatialOps.CONVEXHULL, "convexhull({0})");
    assertThat(mapping).containsEntry(SpatialOps.INTERSECTION, "intersection({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.UNION, "geomunion({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.DIFFERENCE, "difference({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.SYMDIFFERENCE, "symdifference({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.DWITHIN, "dwithin({0}, {1}, {2})");
    assertThat(mapping).containsEntry(SpatialOps.TRANSFORM, "transform({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.WKTTOSQL, "wkttosql({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.WKBTOSQL, "wkbtosql({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.X, "x({0})");
    assertThat(mapping).containsEntry(SpatialOps.X2, "x({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.Y, "y({0})");
    assertThat(mapping).containsEntry(SpatialOps.Y2, "y({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.Z, "y({0})");
    assertThat(mapping).containsEntry(SpatialOps.Z2, "y({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.M, "y({0})");
    assertThat(mapping).containsEntry(SpatialOps.M2, "y({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.START_POINT, "startpoint({0})");
    assertThat(mapping).containsEntry(SpatialOps.END_POINT, "endpoint({0})");
    assertThat(mapping).containsEntry(SpatialOps.IS_RING, "isring({0})");
    assertThat(mapping).containsEntry(SpatialOps.LENGTH, "length({0})");
    assertThat(mapping).containsEntry(SpatialOps.LENGTH2, "length({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.NUM_POINTS, "numpoints({0})");
    assertThat(mapping).containsEntry(SpatialOps.POINTN, "pointn({0})");
    assertThat(mapping).containsEntry(SpatialOps.AREA, "area({0})");
    assertThat(mapping).containsEntry(SpatialOps.AREA2, "area({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.CENTROID, "centroid({0})");
    assertThat(mapping).containsEntry(SpatialOps.POINT_ON_SURFACE, "pointonsurface({0})");
    assertThat(mapping).containsEntry(SpatialOps.EXTERIOR_RING, "exteriorring({0})");
    assertThat(mapping).containsEntry(SpatialOps.EXTERIOR_RING2, "exteriorring({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.INTERIOR_RINGS, "interiorrings({0})");
    assertThat(mapping).containsEntry(SpatialOps.INTERIOR_RINGS2, "interiorrings({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.NUM_INTERIOR_RING, "numinteriorring({0})");
    assertThat(mapping).containsEntry(SpatialOps.INTERIOR_RINGN, "interiorringn({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.GEOMETRIES, "geometries({0})");
    assertThat(mapping).containsEntry(SpatialOps.NUM_SURFACES, "numsurfaces({0})");
    assertThat(mapping).containsEntry(SpatialOps.SURFACE, "surface({0})");
    assertThat(mapping).containsEntry(SpatialOps.NUM_GEOMETRIES, "numgeometries({0})");
    assertThat(mapping).containsEntry(SpatialOps.GEOMETRYN, "geometryn({0})");
    assertThat(mapping).containsEntry(SpatialOps.IS_CLOSED, "isclosed({0})");
    assertThat(mapping).containsEntry(SpatialOps.AS_EWKT, "asewkt({0})");
    assertThat(mapping).containsEntry(SpatialOps.GEOM_FROM_TEXT, "geomfromtext({0})");
    assertThat(mapping).containsEntry(SpatialOps.SET_SRID, "setsrid({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.XMIN, "xmin({0})");
    assertThat(mapping).containsEntry(SpatialOps.XMAX, "xmax({0})");
    assertThat(mapping).containsEntry(SpatialOps.YMIN, "ymin({0})");
    assertThat(mapping).containsEntry(SpatialOps.YMAX, "ymax({0})");
    assertThat(mapping).containsEntry(SpatialOps.COLLECT, "collect({0})");
    assertThat(mapping).containsEntry(SpatialOps.COLLECT2, "collect({0}, {1})");
    assertThat(mapping).containsEntry(SpatialOps.TRANSLATE, "translate({0})");
    assertThat(mapping).containsEntry(SpatialOps.TRANSLATE2, "translate({0}, {1})");
  }
}
