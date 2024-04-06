package com.querydsl.spatial.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Operator;
import com.querydsl.spatial.SpatialOps;
import java.util.Map;
import org.junit.Test;

public class HibernateSpatialSupportTest {

  @Test
  public void allMapped() {
    Map<Operator, String> mapping = HibernateSpatialSupport.getSpatialOps();
    for (Operator operator : SpatialOps.values()) {
      assertThat(mapping.containsKey(operator)).as(operator + " missing").isTrue();
    }
  }

  @Test
  public void checkOperationsCorrectlyMapped() {
    Map<Operator, String> mapping = HibernateSpatialSupport.getSpatialOps();

    assertThat(mapping.get(SpatialOps.DIMENSION)).isEqualTo("dimension({0})");
    assertThat(mapping.get(SpatialOps.GEOMETRY_TYPE)).isEqualTo("geometrytype({0}, {1})");
    assertThat(mapping.get(SpatialOps.SRID)).isEqualTo("srid({0})");
    assertThat(mapping.get(SpatialOps.SRID2)).isEqualTo("srid2({0}, {1})");
    assertThat(mapping.get(SpatialOps.ENVELOPE)).isEqualTo("envelope({0})");
    assertThat(mapping.get(SpatialOps.AS_TEXT)).isEqualTo("astext({0})");
    assertThat(mapping.get(SpatialOps.AS_BINARY)).isEqualTo("asbinary({0})");
    assertThat(mapping.get(SpatialOps.IS_EMPTY)).isEqualTo("isempty({0})");
    assertThat(mapping.get(SpatialOps.IS_SIMPLE)).isEqualTo("issimple({0})");
    assertThat(mapping.get(SpatialOps.BOUNDARY)).isEqualTo("boundary({0})");
    assertThat(mapping.get(SpatialOps.EXTENT)).isEqualTo("extent({0})");
    assertThat(mapping.get(SpatialOps.EQUALS)).isEqualTo("equals({0}, {1})");
    assertThat(mapping.get(SpatialOps.DISJOINT)).isEqualTo("disjoint({0}, {1})");
    assertThat(mapping.get(SpatialOps.INTERSECTS)).isEqualTo("intersects({0}, {1})");
    assertThat(mapping.get(SpatialOps.TOUCHES)).isEqualTo("touches({0}, {1})");
    assertThat(mapping.get(SpatialOps.CROSSES)).isEqualTo("crosses({0}, {1})");
    assertThat(mapping.get(SpatialOps.WITHIN)).isEqualTo("within({0}, {1})");
    assertThat(mapping.get(SpatialOps.CONTAINS)).isEqualTo("contains({0}, {1})");
    assertThat(mapping.get(SpatialOps.OVERLAPS)).isEqualTo("overlaps({0}, {1})");
    assertThat(mapping.get(SpatialOps.RELATE)).isEqualTo("relate({0}, {1}, {2})");
    assertThat(mapping.get(SpatialOps.DISTANCE)).isEqualTo("distance({0}, {1})");
    assertThat(mapping.get(SpatialOps.DISTANCE2)).isEqualTo("distance({0}, {1}, {2})");
    assertThat(mapping.get(SpatialOps.DISTANCE_SPHERE)).isEqualTo("distancesphere({0}, {1})");
    assertThat(mapping.get(SpatialOps.DISTANCE_SPHEROID)).isEqualTo("distancespheroid({0}, {1})");
    assertThat(mapping.get(SpatialOps.BUFFER)).isEqualTo("buffer({0}, {1})");
    assertThat(mapping.get(SpatialOps.BUFFER2)).isEqualTo("buffer({0}, {1}, {2})");
    assertThat(mapping.get(SpatialOps.CONVEXHULL)).isEqualTo("convexhull({0})");
    assertThat(mapping.get(SpatialOps.INTERSECTION)).isEqualTo("intersection({0}, {1})");
    assertThat(mapping.get(SpatialOps.UNION)).isEqualTo("geomunion({0}, {1})");
    assertThat(mapping.get(SpatialOps.DIFFERENCE)).isEqualTo("difference({0}, {1})");
    assertThat(mapping.get(SpatialOps.SYMDIFFERENCE)).isEqualTo("symdifference({0}, {1})");
    assertThat(mapping.get(SpatialOps.DWITHIN)).isEqualTo("dwithin({0}, {1}, {2})");
    assertThat(mapping.get(SpatialOps.TRANSFORM)).isEqualTo("transform({0}, {1})");
    assertThat(mapping.get(SpatialOps.WKTTOSQL)).isEqualTo("wkttosql({0}, {1})");
    assertThat(mapping.get(SpatialOps.WKBTOSQL)).isEqualTo("wkbtosql({0}, {1})");
    assertThat(mapping.get(SpatialOps.X)).isEqualTo("x({0})");
    assertThat(mapping.get(SpatialOps.X2)).isEqualTo("x({0}, {1})");
    assertThat(mapping.get(SpatialOps.Y)).isEqualTo("y({0})");
    assertThat(mapping.get(SpatialOps.Y2)).isEqualTo("y({0}, {1})");
    assertThat(mapping.get(SpatialOps.Z)).isEqualTo("y({0})");
    assertThat(mapping.get(SpatialOps.Z2)).isEqualTo("y({0}, {1})");
    assertThat(mapping.get(SpatialOps.M)).isEqualTo("y({0})");
    assertThat(mapping.get(SpatialOps.M2)).isEqualTo("y({0}, {1})");
    assertThat(mapping.get(SpatialOps.START_POINT)).isEqualTo("startpoint({0})");
    assertThat(mapping.get(SpatialOps.END_POINT)).isEqualTo("endpoint({0})");
    assertThat(mapping.get(SpatialOps.IS_RING)).isEqualTo("isring({0})");
    assertThat(mapping.get(SpatialOps.LENGTH)).isEqualTo("length({0})");
    assertThat(mapping.get(SpatialOps.LENGTH2)).isEqualTo("length({0}, {1})");
    assertThat(mapping.get(SpatialOps.NUM_POINTS)).isEqualTo("numpoints({0})");
    assertThat(mapping.get(SpatialOps.POINTN)).isEqualTo("pointn({0})");
    assertThat(mapping.get(SpatialOps.AREA)).isEqualTo("area({0})");
    assertThat(mapping.get(SpatialOps.AREA2)).isEqualTo("area({0}, {1})");
    assertThat(mapping.get(SpatialOps.CENTROID)).isEqualTo("centroid({0})");
    assertThat(mapping.get(SpatialOps.POINT_ON_SURFACE)).isEqualTo("pointonsurface({0})");
    assertThat(mapping.get(SpatialOps.EXTERIOR_RING)).isEqualTo("exteriorring({0})");
    assertThat(mapping.get(SpatialOps.EXTERIOR_RING2)).isEqualTo("exteriorring({0}, {1})");
    assertThat(mapping.get(SpatialOps.INTERIOR_RINGS)).isEqualTo("interiorrings({0})");
    assertThat(mapping.get(SpatialOps.INTERIOR_RINGS2)).isEqualTo("interiorrings({0}, {1})");
    assertThat(mapping.get(SpatialOps.NUM_INTERIOR_RING)).isEqualTo("numinteriorring({0})");
    assertThat(mapping.get(SpatialOps.INTERIOR_RINGN)).isEqualTo("interiorringn({0}, {1})");
    assertThat(mapping.get(SpatialOps.GEOMETRIES)).isEqualTo("geometries({0})");
    assertThat(mapping.get(SpatialOps.NUM_SURFACES)).isEqualTo("numsurfaces({0})");
    assertThat(mapping.get(SpatialOps.SURFACE)).isEqualTo("surface({0})");
    assertThat(mapping.get(SpatialOps.NUM_GEOMETRIES)).isEqualTo("numgeometries({0})");
    assertThat(mapping.get(SpatialOps.GEOMETRYN)).isEqualTo("geometryn({0})");
    assertThat(mapping.get(SpatialOps.IS_CLOSED)).isEqualTo("isclosed({0})");
    assertThat(mapping.get(SpatialOps.AS_EWKT)).isEqualTo("asewkt({0})");
    assertThat(mapping.get(SpatialOps.GEOM_FROM_TEXT)).isEqualTo("geomfromtext({0})");
    assertThat(mapping.get(SpatialOps.SET_SRID)).isEqualTo("setsrid({0}, {1})");
    assertThat(mapping.get(SpatialOps.XMIN)).isEqualTo("xmin({0})");
    assertThat(mapping.get(SpatialOps.XMAX)).isEqualTo("xmax({0})");
    assertThat(mapping.get(SpatialOps.YMIN)).isEqualTo("ymin({0})");
    assertThat(mapping.get(SpatialOps.YMAX)).isEqualTo("ymax({0})");
    assertThat(mapping.get(SpatialOps.COLLECT)).isEqualTo("collect({0})");
    assertThat(mapping.get(SpatialOps.COLLECT2)).isEqualTo("collect({0}, {1})");
    assertThat(mapping.get(SpatialOps.TRANSLATE)).isEqualTo("translate({0})");
    assertThat(mapping.get(SpatialOps.TRANSLATE2)).isEqualTo("translate({0}, {1})");
  }
}
