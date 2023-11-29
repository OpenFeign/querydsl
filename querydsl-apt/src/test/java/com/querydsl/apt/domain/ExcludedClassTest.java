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

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryExclude;
import org.junit.Test;

@QueryExclude
@QueryEntity
public class ExcludedClassTest {

  @QueryExclude
  @QueryEntity
  public static class InnerClass {}

  @Test(expected = ClassNotFoundException.class)
  public void outerClass() throws ClassNotFoundException {
    Class.forName(getClass().getPackage().getName() + ".Q" + getClass().getSimpleName());
  }

  @Test(expected = ClassNotFoundException.class)
  public void innerClass() throws ClassNotFoundException {
    Class.forName(getClass().getPackage().getName() + ".QExcludedClassTest_InnerClass");
  }
}
