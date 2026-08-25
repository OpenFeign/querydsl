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
package com.querydsl.apt;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class QClassCycleDetector {

  private final Map<String, EntityType> typeMap;
  private final Deque<String> path = new ArrayDeque<>();
  private final Set<String> inStack = new HashSet<>();
  private final Set<String> globalVisited = new HashSet<>();
  private final List<List<String>> cycles = new ArrayList<>();

  private QClassCycleDetector(Map<String, EntityType> typeMap) {
    this.typeMap = typeMap;
  }

  static List<List<String>> detect(Map<String, EntityType> typeMap) {
    var detector = new QClassCycleDetector(typeMap);
    for (EntityType start : typeMap.values()) {
      if (!detector.globalVisited.contains(start.getFullName())) {
        detector.visit(start);
      }
    }
    return detector.cycles;
  }

  private void visit(EntityType current) {
    String currentName = current.getFullName();
    globalVisited.add(currentName);
    inStack.add(currentName);
    path.addLast(current.getSimpleName());

    for (Property property : current.getProperties()) {
      String neighborName = property.getType().getFullName();
      if (neighborName.equals(currentName)) continue;

      EntityType neighbor = typeMap.get(neighborName);
      if (neighbor == null) continue;

      if (inStack.contains(neighborName)) {
        List<String> cycle = new ArrayList<>(path);
        cycle.add(neighbor.getSimpleName());
        cycles.add(cycle);
      } else if (!globalVisited.contains(neighborName)) {
        visit(neighbor);
      }
    }

    path.removeLast();
    inStack.remove(currentName);
  }
}
