package com.querydsl.spatial;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GeometryPathTest {

  @Test
  public void convert() {
    var geometry = new GeometryPath<>("geometry");
    assertThat(geometry.asCollection()).isEqualTo(new GeometryCollectionPath<>("geometry"));
    assertThat(geometry.asLinearRing()).isEqualTo(new LinearRingPath<>("geometry"));
    assertThat(geometry.asLineString()).isEqualTo(new LineStringPath<>("geometry"));
    assertThat(geometry.asMultiLineString()).isEqualTo(new MultiLineStringPath<>("geometry"));
    assertThat(geometry.asMultiPoint()).isEqualTo(new MultiPointPath<>("geometry"));
    assertThat(geometry.asMultiPolygon()).isEqualTo(new MultiPolygonPath<>("geometry"));
    assertThat(geometry.asPoint()).isEqualTo(new PointPath<>("geometry"));
    assertThat(geometry.asPolygon()).isEqualTo(new PolygonPath<>("geometry"));
  }
}
