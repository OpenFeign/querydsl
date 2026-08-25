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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.TypeCategory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class QClassCycleDetectorTest {

  @Test
  void noReferences_returnsEmpty() {
    var a = entity("A");
    var b = entity("B");

    var cycles = QClassCycleDetector.detect(map(a, b));

    assertThat(cycles).isEmpty();
  }

  @Test
  void unidirectionalReference_returnsEmpty() {
    var a = entity("A");
    var b = entity("B");
    reference(a, "b", b);

    var cycles = QClassCycleDetector.detect(map(a, b));

    assertThat(cycles).isEmpty();
  }

  @Test
  void selfReference_isIgnored() {
    var a = entity("A");
    reference(a, "self", a);

    var cycles = QClassCycleDetector.detect(map(a));

    assertThat(cycles).isEmpty();
  }

  @Test
  void referenceToUnknownType_isIgnored() {
    var a = entity("A");
    var external = simpleType("External");
    a.addProperty(new Property(a, "external", external));

    var cycles = QClassCycleDetector.detect(map(a));

    assertThat(cycles).isEmpty();
  }

  @Test
  void twoNodeCycle_isDetected() {
    var a = entity("A");
    var b = entity("B");
    reference(a, "b", b);
    reference(b, "a", a);

    var cycles = QClassCycleDetector.detect(map(a, b));

    assertThat(cycles).containsExactly(List.of("A", "B", "A"));
  }

  @Test
  void threeNodeCycle_isDetected() {
    var a = entity("A");
    var b = entity("B");
    var c = entity("C");
    reference(a, "b", b);
    reference(b, "c", c);
    reference(c, "a", a);

    var cycles = QClassCycleDetector.detect(map(a, b, c));

    assertThat(cycles).containsExactly(List.of("A", "B", "C", "A"));
  }

  private static EntityType entity(String simpleName) {
    return new EntityType(simpleType(simpleName));
  }

  private static SimpleType simpleType(String simpleName) {
    return new SimpleType(
        TypeCategory.ENTITY, "test." + simpleName, "test", simpleName, false, false);
  }

  private static void reference(EntityType from, String propertyName, EntityType to) {
    from.addProperty(new Property(from, propertyName, simpleType(to.getSimpleName())));
  }

  private static Map<String, EntityType> map(EntityType... entities) {
    Map<String, EntityType> result = new LinkedHashMap<>();
    for (EntityType entity : entities) {
      result.put(entity.getFullName(), entity);
    }
    return result;
  }
}
