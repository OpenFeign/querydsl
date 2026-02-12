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
package com.querydsl.core.alias;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PathFactoryTest {

  private PathFactory pathFactory = new DefaultPathFactory();

  private PathMetadata metadata = PathMetadataFactory.forVariable("var");

  @Test
  void createArrayPath() {
    Path<String[]> path = pathFactory.createArrayPath(String[].class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createEntityPath() {
    Path<Object> path = pathFactory.createEntityPath(Object.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createSimplePath() {
    Path<Object> path = pathFactory.createSimplePath(Object.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createComparablePath() {
    Path<String> path = pathFactory.createComparablePath(String.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createEnumPath() {
    Path<PropertyType> path = pathFactory.createEnumPath(PropertyType.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createDatePath() {
    Path<Date> path = pathFactory.createDatePath(Date.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createTimePath() {
    Path<Time> path = pathFactory.createTimePath(Time.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createDateTimePath() {
    Path<Timestamp> path = pathFactory.createDateTimePath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createNumberPath() {
    Path<Integer> path = pathFactory.createNumberPath(Integer.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createBooleanPath() {
    var path = pathFactory.createBooleanPath(metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createStringPath() {
    var path = pathFactory.createStringPath(metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createListPath() {
    Path<List<Timestamp>> path = pathFactory.createListPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createSetPath() {
    Path<Set<Timestamp>> path = pathFactory.createSetPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createCollectionPath() {
    Path<Collection<Timestamp>> path = pathFactory.createCollectionPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  void createMapPath() {
    Path<Map<String, Timestamp>> path =
        pathFactory.createMapPath(String.class, Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }
}
