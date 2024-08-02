package com.querydsl.spatial.jts;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class JTSGeometryPathTest {

  @Test
  public void convert() {
    var geometry = new JTSGeometryPath<>("geometry");
    assertThat(geometry.asCollection()).isEqualTo(new JTSGeometryCollectionPath<>("geometry"));
    assertThat(geometry.asLinearRing()).isEqualTo(new JTSLinearRingPath<>("geometry"));
    assertThat(geometry.asLineString()).isEqualTo(new JTSLineStringPath<>("geometry"));
    assertThat(geometry.asMultiLineString()).isEqualTo(new JTSMultiLineStringPath<>("geometry"));
    assertThat(geometry.asMultiPoint()).isEqualTo(new JTSMultiPointPath<>("geometry"));
    assertThat(geometry.asMultiPolygon()).isEqualTo(new JTSMultiPolygonPath<>("geometry"));
    assertThat(geometry.asPoint()).isEqualTo(new JTSPointPath<>("geometry"));
    assertThat(geometry.asPolygon()).isEqualTo(new JTSPolygonPath<>("geometry"));
  }
}
