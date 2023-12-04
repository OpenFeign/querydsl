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
import org.junit.Test;

public class PathFactoryTest {

  private PathFactory pathFactory = new DefaultPathFactory();

  private PathMetadata metadata = PathMetadataFactory.forVariable("var");

  @Test
  public void createArrayPath() {
    Path<String[]> path = pathFactory.createArrayPath(String[].class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createEntityPath() {
    Path<Object> path = pathFactory.createEntityPath(Object.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createSimplePath() {
    Path<Object> path = pathFactory.createSimplePath(Object.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createComparablePath() {
    Path<String> path = pathFactory.createComparablePath(String.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createEnumPath() {
    Path<PropertyType> path = pathFactory.createEnumPath(PropertyType.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createDatePath() {
    Path<Date> path = pathFactory.createDatePath(Date.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createTimePath() {
    Path<Time> path = pathFactory.createTimePath(Time.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createDateTimePath() {
    Path<Timestamp> path = pathFactory.createDateTimePath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createNumberPath() {
    Path<Integer> path = pathFactory.createNumberPath(Integer.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createBooleanPath() {
    Path<Boolean> path = pathFactory.createBooleanPath(metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createStringPath() {
    Path<String> path = pathFactory.createStringPath(metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createListPath() {
    Path<List<Timestamp>> path = pathFactory.createListPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createSetPath() {
    Path<Set<Timestamp>> path = pathFactory.createSetPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createCollectionPath() {
    Path<Collection<Timestamp>> path = pathFactory.createCollectionPath(Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }

  @Test
  public void createMapPath() {
    Path<Map<String, Timestamp>> path =
        pathFactory.createMapPath(String.class, Timestamp.class, metadata);
    assertThat(path).isNotNull();
  }
}
