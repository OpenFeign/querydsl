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

import com.querydsl.core.alias.Gender;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.types.EnumByNameType;
import com.querydsl.sql.types.StringType;
import com.querydsl.sql.types.UtilDateType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

public class CustomTypesTest extends AbstractJDBCTest {

  private Configuration configuration;

  @Override
  @Before
  public void setUp() throws ClassNotFoundException, SQLException {
    super.setUp();
    // create schema
    statement.execute("drop table person if exists");
    statement.execute(
        """
        create table person(\
        id INT, \
        firstname VARCHAR(50), \
        gender VARCHAR(50), \
        securedId VARCHAR(50), \
        CONSTRAINT PK_person PRIMARY KEY (id) \
        )\
        """);

    // create configuration
    configuration = new Configuration(new HSQLDBTemplates());
    //        configuration.setJavaType(Types.DATE, java.util.Date.class);
    configuration.register(new UtilDateType());
    configuration.register("PERSON", "SECUREDID", new EncryptedString());
    configuration.register("PERSON", "GENDER", new EnumByNameType<Gender>(Gender.class));
    configuration.register(new StringType());
  }

  @Test
  public void export() throws SQLException, IOException {
    // create exporter
    String namePrefix = "Q";
    MetadataExporterConfigImpl config = new MetadataExporterConfigImpl();
    config.setNamePrefix(namePrefix);
    config.setPackageName("test");
    config.setTargetFolder(new File("target/customExport"));
    config.setNamingStrategyClass(DefaultNamingStrategy.class);

    MetaDataExporter exporter = new MetaDataExporter(config);
    exporter.setConfiguration(configuration);

    // export
    exporter.export(connection.getMetaData());
    Path qpersonFile = Paths.get("target", "customExport", "test", "QPerson.java");
    assertThat(qpersonFile).exists();
    String person = new String(Files.readAllBytes(qpersonFile), StandardCharsets.UTF_8);
    // System.err.println(person);
    assertThat(person).contains("createEnum(\"gender\"");
  }

  @Test
  public void insert_query_update() {
    QPerson person = QPerson.person;

    // insert
    SQLInsertClause insert = new SQLInsertClause(connection, configuration, person);
    insert.set(person.id, 10);
    insert.set(person.firstname, "Bob");
    insert.set(person.gender, Gender.MALE);
    assertThat(insert.execute()).isEqualTo(1L);

    // query
    SQLQuery<?> query = new SQLQuery<Void>(connection, configuration);
    assertThat(query.from(person).where(person.id.eq(10)).select(person.gender).fetchOne())
        .isEqualTo(Gender.MALE);

    // update
    SQLUpdateClause update = new SQLUpdateClause(connection, configuration, person);
    update.set(person.gender, Gender.FEMALE);
    update.set(person.firstname, "Jane");
    update.where(person.id.eq(10));
    update.execute();

    // query
    query = new SQLQuery<Void>(connection, configuration);
    assertThat(query.from(person).where(person.id.eq(10)).select(person.gender).fetchOne())
        .isEqualTo(Gender.FEMALE);
  }
}
