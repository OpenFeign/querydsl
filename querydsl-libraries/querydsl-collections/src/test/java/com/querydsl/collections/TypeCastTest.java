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
package com.querydsl.collections;

import com.querydsl.core.types.dsl.PathInits;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;

public class TypeCastTest {

  @Test(expected = IllegalStateException.class)
  @Ignore
  public void cast() {
    var animal = QAnimal.animal;
    var cat = new QCat(animal.getMetadata(), new PathInits("*"));
    CollQueryFactory.from(animal, Collections.<Animal>emptyList())
        .from(cat, Collections.<Cat>emptyList());
  }
}
