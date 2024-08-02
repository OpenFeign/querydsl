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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MethodTypeTest {

  @Test
  public void get() throws SecurityException, NoSuchMethodException {
    var getVal = MethodTypeTest.class.getMethod("getVal");
    var hashCode = Object.class.getMethod("hashCode");
    var size = Collection.class.getMethod("size");
    var toString = Object.class.getMethod("toString");

    assertThat(MethodType.get(ManagedObject.class.getMethod("__mappedPath")))
        .isEqualTo(MethodType.GET_MAPPED_PATH);
    assertThat(MethodType.get(getVal)).isEqualTo(MethodType.GETTER);
    assertThat(MethodType.get(hashCode)).isEqualTo(MethodType.HASH_CODE);
    assertThat(MethodType.get(List.class.getMethod("get", int.class)))
        .isEqualTo(MethodType.LIST_ACCESS);
    assertThat(MethodType.get(Map.class.getMethod("get", Object.class)))
        .isEqualTo(MethodType.MAP_ACCESS);
    assertThat(MethodType.get(size)).isEqualTo(MethodType.SIZE);
    assertThat(MethodType.get(toString)).isEqualTo(MethodType.TO_STRING);
  }

  public String getVal() {
    return "";
  }
}
