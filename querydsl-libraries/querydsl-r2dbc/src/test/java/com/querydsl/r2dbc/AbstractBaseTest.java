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

import static org.junit.Assert.assertEquals;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Target;
import com.querydsl.core.dml.ReactiveDMLClause;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.dml.R2DBCInsertClause;
import com.querydsl.r2dbc.dml.R2DBCUpdateClause;
import com.querydsl.sql.RelationalPath;
import io.r2dbc.spi.Connection;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractBaseTest {

  // protected static final Logger logger = LoggerFactory.getLogger(AbstractBaseTest.class);

  protected final class TestQuery<T> extends R2DBCQuery<T> {

    private TestQuery(Connection conn, Configuration configuration) {
      super(conn, configuration);
    }

    private TestQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
      super(conn, configuration, metadata);
    }

    @Override
    protected SQLSerializer serialize(boolean countRow) {
      SQLSerializer serializer = super.serialize(countRow);
      String rv = serializer.toString();
      if (expectedQuery != null) {
        assertEquals(expectedQuery, rv.replace('\n', ' '));
        expectedQuery = null;
      }
      //      logger.debug(rv);
      return serializer;
    }

    public TestQuery<T> clone(Connection conn) {
      TestQuery<T> q = new TestQuery<T>(conn, getConfiguration(), getMetadata().clone());
      q.union = union;
      q.unionAll = unionAll;
      q.firstUnionSubQuery = firstUnionSubQuery;
      return q;
    }
  }

  protected Connection connection = Connections.getConnection();

  protected Target target = Connections.getTarget();

  protected Configuration configuration = Connections.getConfiguration();

  @Nullable protected String expectedQuery;

  @Rule public MethodRule skipForQuotedRule = new SkipForQuotedRule(configuration);

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  protected <T> void add(List<T> list, T arg, Target... exclusions) {
    if (exclusions.length > 0) {
      for (Target t : exclusions) {
        if (t.equals(target)) {
          return;
        }
      }
    }
    list.add(arg);
  }

  protected R2DBCUpdateClause update(RelationalPath<?> e) {
    R2DBCUpdateClause sqlUpdateClause = new R2DBCUpdateClause(connection, configuration, e);
    return sqlUpdateClause;
  }

  protected R2DBCInsertClause insert(RelationalPath<?> e) {
    R2DBCInsertClause sqlInsertClause = new R2DBCInsertClause(connection, configuration, e);
    return sqlInsertClause;
  }

  protected R2DBCInsertClause insert(RelationalPath<?> e, R2DBCQuery<?> sq) {
    R2DBCInsertClause sqlInsertClause = new R2DBCInsertClause(connection, configuration, e, sq);
    return sqlInsertClause;
  }

  protected R2DBCDeleteClause delete(RelationalPath<?> e) {
    R2DBCDeleteClause sqlDeleteClause = new R2DBCDeleteClause(connection, configuration, e);
    return sqlDeleteClause;
  }

  protected ExtendedR2DBCQuery<?> extQuery() {
    ExtendedR2DBCQuery<?> extendedR2DBCQuery =
        new ExtendedR2DBCQuery<Void>(connection, configuration);
    return extendedR2DBCQuery;
  }

  protected R2DBCQuery<?> query() {
    R2DBCQuery<Void> testQuery = new TestQuery<Void>(connection, configuration);
    return testQuery;
  }

  protected TestQuery<?> testQuery() {
    TestQuery<Void> testQuery =
        new TestQuery<Void>(connection, configuration, new DefaultQueryMetadata());
    return testQuery;
  }

  protected Mono<Long> execute(ReactiveDMLClause<?>... clauses) {
    return Flux.fromArray(clauses).flatMap(ReactiveDMLClause::execute).reduce(0L, Long::sum);
  }
}
