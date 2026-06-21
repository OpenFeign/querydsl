/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import fluentq.core.domain.MyEmbeddable;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.PathInits;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
  @Disabled
  public void mapped_superClass_constructors() throws SecurityException, NoSuchMethodException {
    assertThat(
            QEmbeddable2Test_SomeMappedSuperClassHavingMyEmbeddable.class.getConstructor(
                Class.class, PathMetadata.class, PathInits.class))
        .isNotNull();
  }

  @Test
  @Disabled
  public void entity_constructors() throws SecurityException, NoSuchMethodException {
    assertThat(
            QEmbeddable2Test_SomeEntityClassHavingMyEmbeddable.class.getConstructor(
                Class.class, PathMetadata.class, PathInits.class))
        .isNotNull();
  }
}
