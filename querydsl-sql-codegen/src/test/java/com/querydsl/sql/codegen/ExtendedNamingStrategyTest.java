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

public class ExtendedNamingStrategyTest {

  private NamingStrategy namingStrategy = new ExtendedNamingStrategy();

  private EntityType entityModel;

  @Before
  public void setUp() {
    entityModel = new EntityType(Types.OBJECT);
    // entityModel.addAnnotation(new TableImpl("OBJECT"));
    entityModel.getData().put("table", "OBJECT");
  }

  @Test
  public void getClassName() {
    assertThat(namingStrategy.getClassName("user_data")).isEqualTo("UserData");
    assertThat(namingStrategy.getClassName("u")).isEqualTo("U");
    assertThat(namingStrategy.getClassName("us")).isEqualTo("Us");
    assertThat(namingStrategy.getClassName("u_")).isEqualTo("U_");
    assertThat(namingStrategy.getClassName("us_")).isEqualTo("Us_");
  }

  @Test
  public void getPropertyName() {
    assertThat(namingStrategy.getPropertyName("while", entityModel)).isEqualTo("whileCol");
    assertThat(namingStrategy.getPropertyName("name", entityModel)).isEqualTo("name");
    assertThat(namingStrategy.getPropertyName("user_id", entityModel)).isEqualTo("userId");
    assertThat(namingStrategy.getPropertyName("accountEvent_id", entityModel))
        .isEqualTo("accountEventId");

    assertThat(namingStrategy.getPropertyName("123abc", entityModel)).isEqualTo("_123abc");
    assertThat(namingStrategy.getPropertyName("123 abc", entityModel)).isEqualTo("_123Abc");
  }

  @Test
  public void getPropertyName_for_column_with_spaces() {
    assertThat(namingStrategy.getPropertyName("user id", entityModel)).isEqualTo("userId");
  }

  @Test
  public void getPropertyNameForInverseForeignKey() {
    assertThat(namingStrategy.getPropertyNameForInverseForeignKey("fk_superior", entityModel))
        .isEqualTo("_superiorFk");
    // fk_order_rows
    assertThat(namingStrategy.getPropertyNameForInverseForeignKey("fk_order_rows", entityModel))
        .isEqualTo("rows");
    // fk_category_events
    assertThat(
            namingStrategy.getPropertyNameForInverseForeignKey("fk_category_events", entityModel))
        .isEqualTo("events");
  }

  @Test
  public void getPropertyNameForForeignKey() {
    assertThat(namingStrategy.getPropertyNameForForeignKey("fk_superior", entityModel))
        .isEqualTo("superiorFk");
    assertThat(namingStrategy.getPropertyNameForForeignKey("FK_SUPERIOR", entityModel))
        .isEqualTo("superiorFk");
    // fk_order_rows
    assertThat(namingStrategy.getPropertyNameForForeignKey("fk_order_rows", entityModel))
        .isEqualTo("order");
    // fk_category_events
    assertThat(namingStrategy.getPropertyNameForForeignKey("fk_category_events", entityModel))
        .isEqualTo("category");
  }

  @Test
  public void getPropertyNameForPrimaryKey() {
    assertThat(namingStrategy.getPropertyNameForPrimaryKey("pk_superior", entityModel))
        .isEqualTo("superiorPk");
    assertThat(namingStrategy.getPropertyNameForPrimaryKey("PK_SUPERIOR", entityModel))
        .isEqualTo("superiorPk");
  }

  @Test
  public void getDefaultVariableName() {
    assertThat(namingStrategy.getDefaultVariableName(entityModel)).isEqualTo("object");
  }
}
