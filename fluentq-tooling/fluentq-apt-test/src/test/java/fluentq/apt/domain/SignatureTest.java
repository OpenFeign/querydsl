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

import fluentq.core.annotations.QuerySupertype;
import fluentq.core.types.dsl.EntityPathBase;
import java.io.Serializable;
import org.junit.Test;

public class SignatureTest {

  @QuerySupertype
  public abstract static class APropertyChangeSupported
      implements Comparable<Object>, Cloneable, Serializable {}

  @QuerySupertype
  public abstract static class AValueObject extends APropertyChangeSupported
      implements Comparable<Object>, Cloneable, Serializable {}

  @Test
  public void aPropertyChangeSupported() {
    assertThat(QSignatureTest_APropertyChangeSupported.class.getSuperclass())
        .isEqualTo(EntityPathBase.class);
  }

  @Test
  public void aValueObject() {
    assertThat(QSignatureTest_AValueObject.class.getSuperclass()).isEqualTo(EntityPathBase.class);
  }
}
