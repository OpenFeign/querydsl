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

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEntity;
import org.junit.Test;

public class EnumTest {

  @QueryEntity
  public enum Gender {
    MALE,
    FEMALE
  }

  @QueryEmbeddable
  public enum Gender2 {
    MALE,
    FEMALE
  }

  @QueryEntity
  public static class Bean {
    Gender gender;
  }

  @Test
  public void enum_as_comparable() {
    assertThat(QEnumTest_Gender.gender.asc()).isNotNull();
  }

  @Test
  public void enumOrdinal_as_comparable() {
    assertThat(QEnumTest_Gender.gender.ordinal().asc()).isNotNull();
  }
}
