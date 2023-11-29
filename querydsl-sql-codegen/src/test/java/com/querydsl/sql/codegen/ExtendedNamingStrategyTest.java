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

import static org.junit.Assert.assertEquals;

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
    assertEquals("UserData", namingStrategy.getClassName("user_data"));
    assertEquals("U", namingStrategy.getClassName("u"));
    assertEquals("Us", namingStrategy.getClassName("us"));
    assertEquals("U_", namingStrategy.getClassName("u_"));
    assertEquals("Us_", namingStrategy.getClassName("us_"));
  }

  @Test
  public void getPropertyName() {
    assertEquals("whileCol", namingStrategy.getPropertyName("while", entityModel));
    assertEquals("name", namingStrategy.getPropertyName("name", entityModel));
    assertEquals("userId", namingStrategy.getPropertyName("user_id", entityModel));
    assertEquals("accountEventId", namingStrategy.getPropertyName("accountEvent_id", entityModel));

    assertEquals("_123abc", namingStrategy.getPropertyName("123abc", entityModel));
    assertEquals("_123Abc", namingStrategy.getPropertyName("123 abc", entityModel));
  }

  @Test
  public void getPropertyName_for_column_with_spaces() {
    assertEquals("userId", namingStrategy.getPropertyName("user id", entityModel));
  }

  @Test
  public void getPropertyNameForInverseForeignKey() {
    assertEquals(
        "_superiorFk",
        namingStrategy.getPropertyNameForInverseForeignKey("fk_superior", entityModel));
    // fk_order_rows
    assertEquals(
        "rows", namingStrategy.getPropertyNameForInverseForeignKey("fk_order_rows", entityModel));
    // fk_category_events
    assertEquals(
        "events",
        namingStrategy.getPropertyNameForInverseForeignKey("fk_category_events", entityModel));
  }

  @Test
  public void getPropertyNameForForeignKey() {
    assertEquals(
        "superiorFk", namingStrategy.getPropertyNameForForeignKey("fk_superior", entityModel));
    assertEquals(
        "superiorFk", namingStrategy.getPropertyNameForForeignKey("FK_SUPERIOR", entityModel));
    // fk_order_rows
    assertEquals(
        "order", namingStrategy.getPropertyNameForForeignKey("fk_order_rows", entityModel));
    // fk_category_events
    assertEquals(
        "category", namingStrategy.getPropertyNameForForeignKey("fk_category_events", entityModel));
  }

  @Test
  public void getPropertyNameForPrimaryKey() {
    assertEquals(
        "superiorPk", namingStrategy.getPropertyNameForPrimaryKey("pk_superior", entityModel));
    assertEquals(
        "superiorPk", namingStrategy.getPropertyNameForPrimaryKey("PK_SUPERIOR", entityModel));
  }

  @Test
  public void getDefaultVariableName() {
    assertEquals("object", namingStrategy.getDefaultVariableName(entityModel));
  }
}
