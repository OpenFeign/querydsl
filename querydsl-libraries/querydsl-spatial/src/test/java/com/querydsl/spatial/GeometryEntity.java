package com.querydsl.spatial;

import com.querydsl.core.annotations.QueryEntity;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.LineString;
import org.geolatte.geom.LinearRing;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.MultiPoint;
import org.geolatte.geom.MultiPolygon;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;

@QueryEntity
public class GeometryEntity {

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
