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
package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.types.*;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Test;

public class JavaTypeMappingTest {

  private JavaTypeMapping typeMapping = new JavaTypeMapping();

  @Test
  public void getType_with_subtypes() {
    typeMapping.register(new InputStreamType());
    assertThat(typeMapping.getType(InputStream.class)).isNotNull();
    assertThat(typeMapping.getType(FileInputStream.class)).isNotNull();
  }

  @Test
  public void getType_with_interfaces() {
    assertThat(typeMapping.getType(DummyBlob.class).getClass()).isEqualTo(BlobType.class);
  }

  @Test
  public void getType_for_object() {
    assertThat(typeMapping.getType(Object.class).getClass()).isEqualTo(ObjectType.class);
  }

  @Test
  public void getType_for_primitive() {
    assertThat(typeMapping.getType(byte.class).getClass()).isEqualTo(ByteType.class);
    assertThat(typeMapping.getType(short.class).getClass()).isEqualTo(ShortType.class);
    assertThat(typeMapping.getType(int.class).getClass()).isEqualTo(IntegerType.class);
    assertThat(typeMapping.getType(long.class).getClass()).isEqualTo(LongType.class);
    assertThat(typeMapping.getType(float.class).getClass()).isEqualTo(FloatType.class);
    assertThat(typeMapping.getType(double.class).getClass()).isEqualTo(DoubleType.class);
    assertThat(typeMapping.getType(boolean.class).getClass()).isEqualTo(BooleanType.class);
    assertThat(typeMapping.getType(char.class).getClass()).isEqualTo(CharacterType.class);
  }
}
