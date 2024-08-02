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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ArrayExtTest {

  private static final QArrayExtTest_BinaryFile binaryFile = QArrayExtTest_BinaryFile.binaryFile;

  @QueryEntity
  public static class BinaryFile {

    byte[] contentPart;

    List<byte[]> list;

    Map<String, byte[]> map1;

    Map<byte[], String> map2;
  }

  @Test
  public void binaryFile_contentPart() {
    assertThat(binaryFile.contentPart.getClass()).isEqualTo(ArrayPath.class);
    assertThat(binaryFile.contentPart.getType()).isEqualTo(byte[].class);
  }

  @Test
  public void binaryFile_list() {
    assertThat(binaryFile.list.getClass()).isEqualTo(ListPath.class);
    assertThat(binaryFile.list.getType()).isEqualTo(List.class);
    assertThat(binaryFile.list.getParameter(0)).isEqualTo(byte[].class);

    assertThat(binaryFile.list.get(0).getClass()).isEqualTo(SimplePath.class);
    assertThat(binaryFile.list.get(0).getType()).isEqualTo(byte[].class);
  }

  @Test
  public void binaryFile_map1() {
    assertThat(binaryFile.map1.getClass()).isEqualTo(MapPath.class);
    assertThat(binaryFile.map1.getType()).isEqualTo(Map.class);
    assertThat(binaryFile.map1.getParameter(0)).isEqualTo(String.class);
    assertThat(binaryFile.map1.getParameter(1)).isEqualTo(byte[].class);

    assertThat(binaryFile.map1.get("").getClass()).isEqualTo(SimplePath.class);
    assertThat(binaryFile.map1.get("").getType()).isEqualTo(byte[].class);
  }

  @Test
  public void binaryFile_map2() {
    assertThat(binaryFile.map2.getClass()).isEqualTo(MapPath.class);
    assertThat(binaryFile.map2.getType()).isEqualTo(Map.class);
    assertThat(binaryFile.map2.getParameter(0)).isEqualTo(byte[].class);
    assertThat(binaryFile.map2.getParameter(1)).isEqualTo(String.class);

    assertThat(binaryFile.map2.get(new byte[0]).getClass()).isEqualTo(StringPath.class);
    assertThat(binaryFile.map2.get(new byte[0]).getType()).isEqualTo(String.class);
  }
}
