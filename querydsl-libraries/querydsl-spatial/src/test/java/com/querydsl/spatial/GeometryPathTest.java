package com.querydsl.spatial;

import static org.assertj.core.api.Assertions.assertThat;

import org.geolatte.geom.*;
import org.junit.Test;

public class GeometryPathTest {

  @Test
  public void convert() {
    GeometryPath<Geometry> geometry = new GeometryPath<Geometry>("geometry");
    assertThat(geometry.asCollection())
        .isEqualTo(new GeometryCollectionPath<GeometryCollection>("geometry"));
    assertThat(geometry.asLinearRing()).isEqualTo(new LinearRingPath<LinearRing>("geometry"));
    assertThat(geometry.asLineString()).isEqualTo(new LineStringPath<LineString>("geometry"));
    assertThat(geometry.asMultiLineString())
        .isEqualTo(new MultiLineStringPath<MultiLineString>("geometry"));
    assertThat(geometry.asMultiPoint()).isEqualTo(new MultiPointPath<MultiPoint>("geometry"));
    assertThat(geometry.asMultiPolygon()).isEqualTo(new MultiPolygonPath<MultiPolygon>("geometry"));
    assertThat(geometry.asPoint()).isEqualTo(new PointPath<Point>("geometry"));
    assertThat(geometry.asPolygon()).isEqualTo(new PolygonPath<Polygon>("geometry"));
  }
}
