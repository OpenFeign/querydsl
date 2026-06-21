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
package fluentq.collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fluentq.core.types.dsl.PathInits;
import java.util.Collections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TypeCastTest {

  @Test
  @Disabled
  public void cast() {
    assertThatThrownBy(
            () -> {
              var animal = QAnimal.animal;
              var cat = new QCat(animal.getMetadata(), new PathInits("*"));
              CollQueryFactory.from(animal, Collections.<Animal>emptyList())
                  .from(cat, Collections.<Cat>emptyList());
            })
        .isInstanceOf(IllegalStateException.class);
  }
}
