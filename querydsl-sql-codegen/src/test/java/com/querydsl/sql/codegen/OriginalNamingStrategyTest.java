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
package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.utils.model.Types;
import org.junit.Before;
import org.junit.Test;

public class OriginalNamingStrategyTest {

  private NamingStrategy namingStrategy = new OriginalNamingStrategy();

  private EntityType entityModel;

  @Before
  public void setUp() {
    entityModel = new EntityType(Types.OBJECT);
    // entityModel.addAnnotation(new TableImpl("OBJECT"));
    entityModel.getData().put("table", "OBJECT");
  }

  @Test
  public void getClassName() {
    assertThat(namingStrategy.getClassName("user_data")).isEqualTo("user_data");
    assertThat(namingStrategy.getClassName("u")).isEqualTo("u");
    assertThat(namingStrategy.getClassName("us")).isEqualTo("us");
    assertThat(namingStrategy.getClassName("u_")).isEqualTo("u_");
    assertThat(namingStrategy.getClassName("us_")).isEqualTo("us_");

    assertThat(namingStrategy.getClassName("new line")).isEqualTo("new_line");
  }

  @Test
  public void getPropertyName() {
    assertThat(namingStrategy.getPropertyName("while", entityModel)).isEqualTo("while_col");
    assertThat(namingStrategy.getPropertyName("name", entityModel)).isEqualTo("name");
    assertThat(namingStrategy.getPropertyName("user_id", entityModel)).isEqualTo("user_id");
    assertThat(namingStrategy.getPropertyName("accountEvent_id", entityModel))
        .isEqualTo("accountEvent_id");

    assertThat(namingStrategy.getPropertyName("123abc", entityModel)).isEqualTo("_123abc");
    assertThat(namingStrategy.getPropertyName("123 abc", entityModel)).isEqualTo("_123_abc");

    assertThat(namingStrategy.getPropertyName("#123#abc#def", entityModel))
        .isEqualTo("_123_abc_def");

    assertThat(namingStrategy.getPropertyName("new line", entityModel)).isEqualTo("new_line");
  }

  @Test
  public void getPropertyName_with_dashes() {
    assertThat(namingStrategy.getPropertyName("A-FOOBAR", entityModel)).isEqualTo("A_FOOBAR");
    assertThat(namingStrategy.getPropertyName("A_FOOBAR", entityModel)).isEqualTo("A_FOOBAR");
  }

  @Test
  public void getPropertyNameForInverseForeignKey() {
    assertThat(namingStrategy.getPropertyNameForInverseForeignKey("fk_superior", entityModel))
        .isEqualTo("_fk_superior");
  }

  @Test
  public void getPropertyNameForForeignKey() {
    assertThat(namingStrategy.getPropertyNameForForeignKey("fk_superior", entityModel))
        .isEqualTo("fk_superior");
    assertThat(namingStrategy.getPropertyNameForForeignKey("FK_SUPERIOR", entityModel))
        .isEqualTo("FK_SUPERIOR");
  }

  @Test
  public void getDefaultVariableName() {
    assertThat(namingStrategy.getDefaultVariableName(entityModel)).isEqualTo("OBJECT");
  }
}
