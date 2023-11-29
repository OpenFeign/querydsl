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
package com.querydsl.apt.domain;

import static org.junit.Assert.assertNotNull;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import com.querydsl.core.domain.MyEmbeddable;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.Ignore;
import org.junit.Test;

public class Embeddable2Test {

  @QuerySupertype
  public abstract static class SomeMappedSuperClassHavingMyEmbeddable {

    @QueryEmbedded MyEmbeddable embeddable;
  }

  @QueryEntity
  public abstract static class SomeEntityClassHavingMyEmbeddable {

    @QueryEmbedded MyEmbeddable embeddable;
  }

  @QueryEntity
  public static class SomeEntity extends SomeMappedSuperClassHavingMyEmbeddable {}

  @Test
  @Ignore
  public void mapped_superClass_constructors() throws SecurityException, NoSuchMethodException {
    assertNotNull(
        QEmbeddable2Test_SomeMappedSuperClassHavingMyEmbeddable.class.getConstructor(
            Class.class, PathMetadata.class, PathInits.class));
  }

  @Test
  @Ignore
  public void entity_constructors() throws SecurityException, NoSuchMethodException {
    assertNotNull(
        QEmbeddable2Test_SomeEntityClassHavingMyEmbeddable.class.getConstructor(
            Class.class, PathMetadata.class, PathInits.class));
  }
}
