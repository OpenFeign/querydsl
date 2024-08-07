/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Splitter;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ddl.CreateTableClause;
import com.querydsl.sql.ddl.DropTableClause;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class KeywordQuotingBase extends AbstractBaseTest {

  private static Splitter COMMA = Splitter.on(',');

  private static class Quoting extends RelationalPathBase<Quoting> {

    public static final Quoting quoting = new Quoting("quoting");

    public final StringPath from = createString("from");
    public final BooleanPath all = createBoolean("all");

    private Quoting(String path) {
      super(Quoting.class, PathMetadataFactory.forVariable(path), "PUBLIC", "quoting");
      addMetadata();
    }

    Quoting(PathMetadata metadata) {
      super(Quoting.class, metadata, "PUBLIC", "quoting");
      addMetadata();
    }

    protected void addMetadata() {
      addMetadata(from, ColumnMetadata.named("from"));
      addMetadata(all, ColumnMetadata.named("all"));
    }
  }

  private final Quoting quoting = Quoting.quoting;

  @Before
  public void setUp() throws Exception {
    new CreateTableClause(connection, configuration, "quoting")
        .column("from", String.class)
        .size(30)
        .column("all", Boolean.class)
        .execute();
    execute(insert(quoting).columns(quoting.from, quoting.all).values("from", true));
  }

  @After
  public void tearDown() throws Exception {
    new DropTableClause(connection, configuration, "quoting").execute();
  }

  @Test
  public void keywords() {
    var from = new Quoting("from");
    assertThat(
            query()
                .from(quoting.as(from))
                .where(from.from.eq("from").and(from.all.isNotNull()))
                .select(from.from)
                .fetchFirst())
        .isEqualTo("from");
  }

  @Test
  public void validateKeywordsCompleteness() throws SQLException {
    var keywords =
        switch (target) {
          case CUBRID -> Keywords.CUBRID;
          case DB2 -> Keywords.DB2;
          case DERBY -> Keywords.DERBY;
          case FIREBIRD -> Keywords.FIREBIRD;
          case H2 -> Keywords.H2;
          case HSQLDB -> Keywords.HSQLDB;
          case LUCENE -> Keywords.DEFAULT;
          case MEM -> Keywords.DEFAULT;
          case MYSQL -> Keywords.MYSQL;
          case ORACLE -> Keywords.ORACLE;
          case POSTGRESQL -> Keywords.POSTGRESQL;
          case SQLITE -> Keywords.SQLITE;
          case SQLSERVER -> Keywords.SQLSERVER2012;
          case TERADATA -> Keywords.DEFAULT;
        };

    var driverKeyWords =
        COMMA
            .splitToStream(connection.getMetaData().getSQLKeywords())
            .filter(w -> !w.isBlank())
            .map(String::toUpperCase)
            .map(String::strip)
            .toList();

    assertThat(keywords).containsAll(driverKeyWords);
  }
}
