---
layout: default
title: Querying Spatial
parent: Tutorials
nav_order: 7
---

# Querydsl Spatial

Support for spatial queries is available via the Querydsl Spatial module, which
is an extension module to the SQL module. The Spatial module supports the object
model of Simple Feature Access in queries and object binding.

The [geolatte](http://www.geolatte.org/) project is used for the object model.

![Spatial class diagram]({{ site.baseurl }}/assets/images/spatial.svg)

## Maven Integration

Add the following dependency to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-sql-spatial</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Additionally, add the following database-specific extra dependencies:

```xml
<!-- for PostgreSQL usage -->
<dependency>
  <groupId>org.postgis</groupId>
  <artifactId>postgis-jdbc</artifactId>
  <version>1.3.3</version>
  <scope>provided</scope>
</dependency>

<!-- for Oracle usage -->
<dependency>
  <groupId>oracle</groupId>
  <artifactId>sdoapi</artifactId>
  <version>11.2.0</version>
  <scope>provided</scope>
</dependency>
```

## Code Generation via Maven

The code generation for Querydsl SQL can be set to detect spatial types in
database schemas and use geolatte types via the `spatial` property:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  ...
  <configuration>
    ...
    <spatial>true</spatial>
  </configuration>
</plugin>
```

## Runtime Configuration

Instead of the normal `SQLTemplates` instances, use spatial-enabled instances:

- `GeoDBTemplates` (for H2)
- `MySQLSpatialTemplates`
- `OracleSpatialTemplates` (alpha stage)
- `PostGISTemplates`
- `SQLServer2008SpatialTemplates`
- `TeradataSpatialTemplates`

## Querying

With code generation and runtime configuration set for spatial types, you can
construct queries with spatial operations.

### Filter by Distance

```java
Geometry point = Wkt.fromWkt("Point(2 2)");
query.where(table.geo.distance(point).lt(5.0));
```

In addition to straight distance between geometries, spherical and spheroidal
distance are provided via `distanceSphere` and `distanceSpheroid`.

### Contains

```java
Geometry point = Wkt.fromWkt("Point(2 2)");
query.where(table.geo.contains(point));
```

### Intersection

```java
Geometry geo = query.select(table.geo1.intersection(table.geo2)).fetchOne();
```

### Access to the SPATIAL_REF_SYS Table

Unified access to the `SPATIAL_REF_SYS` standard table is provided via the
`QSpatialRefSys` and `SpatialRefSys` classes. `SPATIAL_REF_SYS` contains data
about the supported spatial reference systems.

```java
QSpatialRefSys spatialRefSys = QSpatialRefSys.spatialRefSys;
List<SpatialRefSys> referenceSystems = query.select(spatialRefSys).from(spatialRefSys).fetch();
```

## Inheritance

If you use only generic geometry types in your database schema, you can use
conversion methods in the object model to convert to more specific types:

```java
GeometryPath<Geometry> geometry = shapes.geometry;
PointPath<Point> point = geometry.asPoint();
NumberExpression<Double> pointX = point.x(); // x() is not available on GeometryExpression/GeometryPath
```
