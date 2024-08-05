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
import com.querydsl.codegen.Property;
import com.querydsl.codegen.utils.model.Types;
import org.junit.Before;
import org.junit.Test;

public class DefaultNamingStrategyTest {

  private NamingStrategy namingStrategy = new DefaultNamingStrategy();

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

    assertThat(namingStrategy.getClassName("new line")).isEqualTo("NewLine");
  }

  @Test
  public void getPropertyName() {
    assertThat(namingStrategy.getPropertyName("a", entityModel)).isEqualTo("a");
    assertThat(namingStrategy.getPropertyName("while", entityModel)).isEqualTo("whileCol");
    assertThat(namingStrategy.getPropertyName("name", entityModel)).isEqualTo("name");
    assertThat(namingStrategy.getPropertyName("user_id", entityModel)).isEqualTo("userId");
    assertThat(namingStrategy.getPropertyName("accountEvent_id", entityModel))
        .isEqualTo("accountEventId");

    assertThat(namingStrategy.getPropertyName("123abc", entityModel)).isEqualTo("_123abc");
    assertThat(namingStrategy.getPropertyName("123 abc", entityModel)).isEqualTo("_123Abc");

    assertThat(namingStrategy.getPropertyName("#123#abc#def", entityModel)).isEqualTo("_123AbcDef");

    assertThat(namingStrategy.getPropertyName("new line", entityModel)).isEqualTo("newLine");

    assertThat(namingStrategy.getPropertyName("class", entityModel)).isEqualTo("classCol");
    assertThat(namingStrategy.getPropertyName("Class", entityModel)).isEqualTo("classCol");
  }

  @Test
  public void getPropertyName_with_dashes() {
    assertThat(namingStrategy.getPropertyName("A-FOOBAR", entityModel)).isEqualTo("aFoobar");
    assertThat(namingStrategy.getPropertyName("A_FOOBAR", entityModel)).isEqualTo("aFoobar");
  }

  @Test
  public void getPropertyName_for_column_with_spaces() {
    assertThat(namingStrategy.getPropertyName("user id", entityModel)).isEqualTo("userId");
  }

  @Test
  public void getPropertyNameForInverseForeignKey() {
    assertThat(namingStrategy.getPropertyNameForInverseForeignKey("fk_superior", entityModel))
        .isEqualTo("_superiorFk");
  }

  @Test
  public void getPropertyNameForForeignKey() {
    assertThat(namingStrategy.getPropertyNameForForeignKey("fk_superior", entityModel))
        .isEqualTo("superiorFk");
    assertThat(namingStrategy.getPropertyNameForForeignKey("FK_SUPERIOR", entityModel))
        .isEqualTo("superiorFk");

    assertThat(namingStrategy.getPropertyNameForForeignKey("REFFOO_BAR", entityModel))
        .isEqualTo("reffooBar");
    assertThat(namingStrategy.getPropertyNameForForeignKey("REF_FOO_BAR", entityModel))
        .isEqualTo("refFooBar");
    assertThat(namingStrategy.getPropertyNameForForeignKey("REF_FOO_BAR_", entityModel))
        .isEqualTo("refFooBar_");
  }

  @Test
  public void getPropertyNameForPrimaryKey() {
    assertThat(namingStrategy.getPropertyNameForPrimaryKey("pk_superior", entityModel))
        .isEqualTo("superiorPk");
    assertThat(namingStrategy.getPropertyNameForPrimaryKey("PK_SUPERIOR", entityModel))
        .isEqualTo("superiorPk");
  }

  @Test
  public void getPropertyNameForPrimaryKey_clash() {
    entityModel.addProperty(new Property(entityModel, "id", Types.STRING));
    assertThat(namingStrategy.getPropertyNameForPrimaryKey("id", entityModel)).isEqualTo("idPk");
  }

  @Test
  public void getDefaultVariableName() {
    assertThat(namingStrategy.getDefaultVariableName(entityModel)).isEqualTo("object");
  }

  @Test
  public void spaces() {
    assertThat(namingStrategy.getPropertyName("a  b", entityModel)).isEqualTo("a_b");
  }

  @Test
  public void validName() {
    assertThat(namingStrategy.normalizeColumnName("8FRecord")).isEqualTo("8FRecord");
    assertThat(namingStrategy.getPropertyName("8FRecord", entityModel)).isEqualTo("_8FRecord");
  }
}
