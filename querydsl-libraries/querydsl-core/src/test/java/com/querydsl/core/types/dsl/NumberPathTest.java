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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Operation;
import java.util.List;
import org.junit.Test;

public class NumberPathTest {

  private NumberPath<Byte> bytePath = new NumberPath<Byte>(Byte.class, "bytePath");

  @SuppressWarnings("unchecked")
  @Test
  public void bytePath_in() {
    Operation<?> operation = (Operation<?>) bytePath.in(1, 2, 3);
    Constant<List<Byte>> rightArg = (Constant<List<Byte>>) operation.getArg(1);
    List<Byte> numbers = rightArg.getConstant();
    assertThat(numbers.getFirst()).isEqualTo(Byte.valueOf((byte) 1));
    assertThat(numbers.get(1)).isEqualTo(Byte.valueOf((byte) 2));
    assertThat(numbers.get(2)).isEqualTo(Byte.valueOf((byte) 3));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void bytePath_notIn() {
    Operation<?> operation = (Operation<?>) bytePath.notIn(1, 2, 3);
    Constant<List<Byte>> rightArg = (Constant<List<Byte>>) operation.getArg(1);
    List<Byte> numbers = rightArg.getConstant();
    assertThat(numbers.getFirst()).isEqualTo(Byte.valueOf((byte) 1));
    assertThat(numbers.get(1)).isEqualTo(Byte.valueOf((byte) 2));
    assertThat(numbers.get(2)).isEqualTo(Byte.valueOf((byte) 3));
  }
}
