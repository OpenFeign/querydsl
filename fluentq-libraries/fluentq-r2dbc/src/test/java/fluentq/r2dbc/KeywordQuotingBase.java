/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team).
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
package fluentq.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.types.PathMetadata;
import fluentq.core.types.PathMetadataFactory;
import fluentq.core.types.dsl.BooleanPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.r2dbc.ddl.CreateTableClause;
import fluentq.r2dbc.ddl.DropTableClause;
import fluentq.sql.ColumnMetadata;
import fluentq.sql.RelationalPathBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class KeywordQuotingBase extends AbstractBaseTest {

  private static class Quoting extends RelationalPathBase<Quoting> {

    public static final Quoting quoting = new Quoting("quoting");

    public final StringPath from = createString("from");
    public final BooleanPath all = createBoolean("all");

    private Quoting(String path) {
      super(Quoting.class, PathMetadataFactory.forVariable(path), "PUBLIC", "quoting");
      addMetadata();
    }

    public Quoting(PathMetadata metadata) {
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
        .execute()
        .block();
    execute(insert(quoting).columns(quoting.from, quoting.all).values("from", true)).block();
  }

  @After
  public void tearDown() throws Exception {
    new DropTableClause(connection, configuration, "quoting").execute().block();
  }

  @Test
  public void keywords() {
    var from = new Quoting("from");
    assertThat(
            query()
                .from(quoting.as(from))
                .where(from.from.eq("from").and(from.all.isNotNull()))
                .select(from.from)
                .fetchFirst()
                .block())
        .isEqualTo("from");
  }
}
