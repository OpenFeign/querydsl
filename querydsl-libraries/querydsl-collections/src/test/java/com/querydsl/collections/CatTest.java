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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.SimplePath;
import org.junit.Test;

public class CatTest {

  @Test(expected = NoSuchFieldException.class)
  public void skippedField() throws SecurityException, NoSuchFieldException {
    QCat.class.getField("skippedField");
  }

  @Test
  public void stringAsSimple() throws SecurityException, NoSuchFieldException {
    assertThat(QCat.cat.stringAsSimple.getClass().equals(SimplePath.class)).isTrue();
  }

  @Test
  public void dateAsSimple() {
    assertThat(QCat.cat.dateAsSimple.getClass().equals(SimplePath.class)).isTrue();
  }
}
