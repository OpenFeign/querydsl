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

import static org.junit.Assert.assertTrue;

import com.querydsl.core.types.dsl.SimplePath;
import org.junit.Test;

public class CatTest {

  @Test(expected = NoSuchFieldException.class)
  public void skippedField() throws SecurityException, NoSuchFieldException {
    QCat.class.getField("skippedField");
  }

  @Test
  public void stringAsSimple() throws SecurityException, NoSuchFieldException {
    assertTrue(QCat.cat.stringAsSimple.getClass().equals(SimplePath.class));
  }

  @Test
  public void dateAsSimple() {
    assertTrue(QCat.cat.dateAsSimple.getClass().equals(SimplePath.class));
  }
}
