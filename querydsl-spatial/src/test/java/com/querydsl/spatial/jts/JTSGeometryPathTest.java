package com.querydsl.spatial.jts;

import static org.assertj.core.api.Assertions.assertThat;

import com.vividsolutions.jts.geom.*;
import org.junit.Test;

public class JTSGeometryPathTest {

  @Test
  public void convert() {
    JTSGeometryPath<Geometry> geometry = new JTSGeometryPath<Geometry>("geometry");
    assertThat(geometry.asCollection())
        .isEqualTo(new JTSGeometryCollectionPath<GeometryCollection>("geometry"));
    assertThat(geometry.asLinearRing()).isEqualTo(new JTSLinearRingPath<LinearRing>("geometry"));
    assertThat(geometry.asLineString()).isEqualTo(new JTSLineStringPath<LineString>("geometry"));
    assertThat(geometry.asMultiLineString())
        .isEqualTo(new JTSMultiLineStringPath<MultiLineString>("geometry"));
    assertThat(geometry.asMultiPoint()).isEqualTo(new JTSMultiPointPath<MultiPoint>("geometry"));
    assertThat(geometry.asMultiPolygon())
        .isEqualTo(new JTSMultiPolygonPath<MultiPolygon>("geometry"));
    assertThat(geometry.asPoint()).isEqualTo(new JTSPointPath<Point>("geometry"));
    assertThat(geometry.asPolygon()).isEqualTo(new JTSPolygonPath<Polygon>("geometry"));
  }
}
