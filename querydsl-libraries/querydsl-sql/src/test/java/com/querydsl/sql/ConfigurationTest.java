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
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.alias.Gender;
import com.querydsl.sql.domain.QSurvey;
import com.querydsl.sql.namemapping.ChainedNameMapping;
import com.querydsl.sql.namemapping.ChangeLetterCaseNameMapping;
import com.querydsl.sql.namemapping.ChangeLetterCaseNameMapping.LetterCase;
import com.querydsl.sql.namemapping.NameMapping;
import com.querydsl.sql.namemapping.PreConfiguredNameMapping;
import com.querydsl.sql.types.EnumByNameType;
import com.querydsl.sql.types.InputStreamType;
import com.querydsl.sql.types.Null;
import com.querydsl.sql.types.StringType;
import com.querydsl.sql.types.UtilDateType;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;
import org.easymock.EasyMock;
import org.junit.Test;

public class ConfigurationTest {

  @Test
  public void various() {
    var configuration = new Configuration(new H2Templates());
    //        configuration.setJavaType(Types.DATE, java.util.Date.class);
    configuration.register(new UtilDateType());
    configuration.register("person", "secureId", new EncryptedString());
    configuration.register("person", "gender", new EnumByNameType<>(Gender.class));
    configuration.register(new StringType());
    assertThat(configuration.getJavaType(java.sql.Types.VARCHAR, null, 0, 0, "person", "gender"))
        .isEqualTo(Gender.class);
  }

  @Test
  public void custom_type() {
    var configuration = new Configuration(new H2Templates());
    //        configuration.setJavaType(Types.BLOB, InputStream.class);
    configuration.register(new InputStreamType());
    assertThat(configuration.getJavaType(Types.BLOB, null, 0, 0, "", ""))
        .isEqualTo(InputStream.class);
  }

  @Test
  public void set_null() throws SQLException {
    var configuration = new Configuration(new H2Templates());
    //        configuration.register(new UntypedNullType());
    configuration.register("SURVEY", "NAME", new EncryptedString());
    var stmt = EasyMock.<PreparedStatement>createNiceMock(PreparedStatement.class);
    configuration.set(stmt, QSurvey.survey.name, 0, Null.DEFAULT);
  }

  @Test
  public void get_schema() {
    var configuration = new Configuration(new H2Templates());
    configuration.registerSchemaOverride("public", "pub");
    configuration.registerTableOverride("employee", "emp");
    configuration.registerTableOverride("public", "employee", "employees");

    assertThat(configuration.getOverride(new SchemaAndTable("public", "")).getSchema())
        .isEqualTo("pub");
    assertThat(configuration.getOverride(new SchemaAndTable("", "employee")).getTable())
        .isEqualTo("emp");
    assertThat(configuration.getOverride(new SchemaAndTable("public", "employee")).getTable())
        .isEqualTo("employees");

    configuration.setDynamicNameMapping(new PreConfiguredNameMapping());
    var notOverriddenSchemaAndTable = new SchemaAndTable("notoverridden", "notoverridden");
    assertThat(configuration.getOverride(notOverriddenSchemaAndTable))
        .isEqualTo(notOverriddenSchemaAndTable);

    configuration.setDynamicNameMapping(
        new ChangeLetterCaseNameMapping(LetterCase.UPPER, Locale.ENGLISH));
    var notDirectOverriden = "notDirectOverriden";
    assertThat(
            configuration.getOverride(new SchemaAndTable("public", notDirectOverriden)).getTable())
        .isEqualTo(notDirectOverriden.toUpperCase(Locale.ENGLISH));
  }

  @Test
  public void columnOverride() {
    var configuration = new Configuration(new H2Templates());
    assertThat(
            configuration.getColumnOverride(
                new SchemaAndTable("myschema", "mytable"), "notoverriddencolumn"))
        .isEqualTo("notoverriddencolumn");

    // Testing when chained name mapping does not give back any result.
    configuration.setDynamicNameMapping(new PreConfiguredNameMapping());
    assertThat(
            configuration.getColumnOverride(
                new SchemaAndTable("myschema", "mytable"), "notoverriddencolumn"))
        .isEqualTo("notoverriddencolumn");

    // Testing all other use-cases when letter case changing is in the end of the chain
    configuration.setDynamicNameMapping(
        new ChangeLetterCaseNameMapping(LetterCase.LOWER, Locale.ENGLISH));

    configuration.registerColumnOverride("mytable", "oldcolumn", "newcolumn");
    configuration.registerColumnOverride("mytable", "oldcolumn2", "newcolumn2");
    assertThat(
            configuration.getColumnOverride(new SchemaAndTable("myschema", "mytable"), "oldcolumn"))
        .isEqualTo("newcolumn");
    assertThat(
            configuration.getColumnOverride(
                new SchemaAndTable("myschema", "mytable"), "oldcolumn2"))
        .isEqualTo("newcolumn2");

    configuration.registerColumnOverride("myschema", "mytable", "oldcolumn", "newcolumnwithschema");
    configuration.registerColumnOverride(
        "myschema", "mytable", "oldcolumn2", "newcolumnwithschema2");
    assertThat(
            configuration.getColumnOverride(
                new SchemaAndTable("myschema", "mytable"), "oldcolumn2"))
        .isEqualTo("newcolumnwithschema2");
    assertThat(
            configuration.getColumnOverride(
                new SchemaAndTable("myschema", "mytable"), "notoverriddencolumn"))
        .isEqualTo("notoverriddencolumn");

    assertThat(configuration.getColumnOverride(new SchemaAndTable("myschema", "mytable"), "LOWER"))
        .isEqualTo("lower");
  }

  @Test(expected = NullPointerException.class)
  public void npeWithNullParameterOfChainedNameMappingConstructor() {
    new ChainedNameMapping((NameMapping[]) null);
  }

  @Test(expected = NullPointerException.class)
  public void npeWithNullElementInParameterOfChainedNameMappingConstructor() {
    new ChainedNameMapping(new NameMapping[] {null});
  }

  @Test
  public void numericOverriden() {
    var configuration = new Configuration(new H2Templates());
    configuration.registerNumeric(19, 0, BigInteger.class);
    assertThat(BigInteger.class)
        .isEqualTo(configuration.getJavaType(Types.NUMERIC, "", 19, 0, "", ""));
  }

  @Test
  public void numericOverriden2() {
    var configuration = new Configuration(new H2Templates());
    configuration.registerNumeric(18, 19, 0, 0, BigInteger.class);
    assertThat(BigInteger.class)
        .isEqualTo(configuration.getJavaType(Types.NUMERIC, "", 18, 0, "", ""));
    assertThat(BigInteger.class)
        .isEqualTo(configuration.getJavaType(Types.NUMERIC, "", 19, 0, "", ""));
  }
}
