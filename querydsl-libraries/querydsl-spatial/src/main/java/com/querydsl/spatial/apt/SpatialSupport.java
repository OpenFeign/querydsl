/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.spatial.apt;

import com.querydsl.codegen.AbstractModule;
import com.querydsl.codegen.CodegenModule;
import com.querydsl.codegen.Extension;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.model.SimpleType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@code SpatialSupport} provides support for spatial types in code generation
 *
 * @author tiwe
 */
public final class SpatialSupport implements Extension {

  private static void registerTypes(TypeMappings typeMappings) {
    Map<String, String> additions = new HashMap<>();
    additions.put("Geometry", "GeometryPath");
    additions.put("GeometryCollection", "GeometryCollectionPath");
    additions.put("LinearRing", "LinearRingPath");
    additions.put("LineString", "LineStringPath");
    additions.put("MultiLineString", "MultiLineStringPath");
    additions.put("MultiPoint", "MultiPointPath");
    additions.put("MultiPolygon", "MultiPolygonPath");
    additions.put("Point", "PointPath");
    additions.put("Polygon", "PolygonPath");
    for (Map.Entry<String, String> entry : additions.entrySet()) {
      typeMappings.register(
          new SimpleType("org.geolatte.geom." + entry.getKey()),
          new SimpleType("com.querydsl.spatial." + entry.getValue()));
    }
  }

  private static void registerJTSTypes(TypeMappings typeMappings) {
    Map<String, String> additions = new HashMap<>();
    additions.put("Geometry", "JTSGeometryPath");
    additions.put("GeometryCollection", "JTSGeometryCollectionPath");
    additions.put("LinearRing", "JTSLinearRingPath");
    additions.put("LineString", "JTSLineStringPath");
    additions.put("MultiLineString", "JTSMultiLineStringPath");
    additions.put("MultiPoint", "JTSMultiPointPath");
    additions.put("MultiPolygon", "JTSMultiPolygonPath");
    additions.put("Point", "JTSPointPath");
    additions.put("Polygon", "JTSPolygonPath");
    for (Map.Entry<String, String> entry : additions.entrySet()) {
      typeMappings.register(
          new SimpleType("com.vividsolutions.jts.geom." + entry.getKey()),
          new SimpleType("com.querydsl.spatial.jts." + entry.getValue()));
      typeMappings.register(
          new SimpleType("org.locationtech.jts.geom." + entry.getKey()),
          new SimpleType("com.querydsl.spatial.locationtech.jts." + entry.getValue()));
    }
  }

  private static void addImports(AbstractModule module, String packageName) {
    @SuppressWarnings("unchecked")
    Set<String> imports = module.get(Set.class, CodegenModule.IMPORTS);
    if (imports.isEmpty()) {
      imports = Collections.singleton(packageName);
    } else {
      Set<String> old = imports;
      imports = new HashSet<>();
      imports.addAll(old);
      imports.add(packageName);
    }
    module.bind(CodegenModule.IMPORTS, imports);
  }

  /**
   * Register spatial types to the given codegen module
   *
   * @param module module to be customized for spatial support
   */
  public void addSupport(AbstractModule module) {
    registerTypes(module.get(TypeMappings.class));
    addImports(module, "com.querydsl.spatial.path");
    registerJTSTypes(module.get(TypeMappings.class));
    addImports(module, "com.querydsl.spatial.jts.path");
  }
}
