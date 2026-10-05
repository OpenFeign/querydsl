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
package com.querydsl.sql.vector;

import static com.querydsl.sql.vector.QDocument.document;
import static com.querydsl.vector.VectorExpressions.vector;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractSQLVectorTest {

  private static final float[] QUERY = {1, 0, 0};

  private Connection connection;

  private SQLQueryFactory queryFactory;

  protected abstract Connection connect() throws SQLException;

  protected abstract SQLTemplates templates();

  protected abstract void createTable(Connection connection) throws SQLException;

  @BeforeAll
  void setUp() throws SQLException {
    connection = connect();
    createTable(connection);
    queryFactory = new SQLQueryFactory(new Configuration(templates()), () -> connection);
    queryFactory
        .insert(document)
        .columns(document.id, document.title, document.embedding)
        .values(1L, "x-axis", new float[] {1, 0, 0})
        .addBatch()
        .values(2L, "y-axis", new float[] {0, 1, 0})
        .addBatch()
        .values(3L, "near x-axis", new float[] {0.9f, 0.1f, 0})
        .addBatch()
        .values(4L, "diagonal", new float[] {2, 2, 2})
        .addBatch()
        .execute();
  }

  @AfterAll
  void tearDown() throws SQLException {
    try (var stmt = connection.createStatement()) {
      stmt.execute("drop table vector_document");
    }
    connection.close();
  }

  @Test
  void roundTrip() {
    var embedding =
        queryFactory.select(document.embedding).from(document).where(document.id.eq(3L)).fetchOne();

    assertThat(embedding).containsExactly(0.9f, 0.1f, 0f);
  }

  @Test
  void orderByL2Distance() {
    assertThat(titlesOrderedBy(document.embedding.l2Distance(QUERY)))
        .containsExactly("x-axis", "near x-axis", "y-axis", "diagonal");
  }

  @Test
  void orderByCosineDistance() {
    assertThat(titlesOrderedBy(document.embedding.cosineDistance(QUERY)))
        .containsExactly("x-axis", "near x-axis", "diagonal", "y-axis");
  }

  @Test
  void orderByNegativeInnerProduct() {
    assertThat(titlesOrderedBy(document.embedding.negativeInnerProduct(QUERY)))
        .containsExactly("diagonal", "x-axis", "near x-axis", "y-axis");
  }

  @Test
  void nearestNeighbours() {
    var titles =
        queryFactory
            .select(document.title)
            .from(document)
            .orderBy(document.embedding.l2Distance(QUERY).asc())
            .limit(2)
            .fetch();

    assertThat(titles).containsExactly("x-axis", "near x-axis");
  }

  @Test
  void filterByCosineDistance() {
    var titles =
        queryFactory
            .select(document.title)
            .from(document)
            .where(document.embedding.cosineDistance(QUERY).lt(0.1))
            .orderBy(document.id.asc())
            .fetch();

    assertThat(titles).containsExactly("x-axis", "near x-axis");
  }

  @Test
  void distances() {
    var e = document.embedding;
    var row =
        queryFactory
            .select(
                e.l2Distance(QUERY),
                e.l2SquaredDistance(QUERY),
                e.innerProduct(QUERY),
                e.l1Distance(QUERY),
                e.l2Distance(e))
            .from(document)
            .where(document.id.eq(4L))
            .fetchOne();

    assertThat(row.get(e.l2Distance(QUERY))).isCloseTo(3.0, within(1e-6));
    assertThat(row.get(e.l2SquaredDistance(QUERY))).isCloseTo(9.0, within(1e-6));
    assertThat(row.get(e.innerProduct(QUERY))).isCloseTo(2.0, within(1e-6));
    assertThat(row.get(e.l1Distance(QUERY))).isCloseTo(5.0, within(1e-6));
    assertThat(row.get(e.l2Distance(e))).isCloseTo(0.0, within(1e-6));
  }

  @Test
  void constantVectors() {
    var row =
        queryFactory
            .select(vector(QUERY).dims(), vector(QUERY).l2Distance(new float[] {1, 0, 2}))
            .from(document)
            .where(document.id.eq(1L))
            .fetchOne();

    assertThat(row.get(0, Integer.class)).isEqualTo(3);
    assertThat(row.get(1, Double.class)).isCloseTo(2.0, within(1e-6));
  }

  @Test
  void dimsAndNorm() {
    var row =
        queryFactory
            .select(document.embedding.dims(), document.embedding.norm())
            .from(document)
            .where(document.id.eq(2L))
            .fetchOne();

    assertThat(row.get(document.embedding.dims())).isEqualTo(3);
    assertThat(row.get(document.embedding.norm())).isCloseTo(1.0, within(1e-6));
  }

  @Test
  void literals() {
    var configuration = new Configuration(templates());
    configuration.setUseLiterals(true);
    var title =
        new SQLQueryFactory(configuration, () -> connection)
            .select(document.title)
            .from(document)
            .orderBy(document.embedding.cosineDistance(new float[] {0, 1, 0}).asc())
            .limit(1)
            .fetchOne();

    assertThat(title).isEqualTo("y-axis");
  }

  private List<String> titlesOrderedBy(NumberExpression<Double> distance) {
    return queryFactory.select(document.title).from(document).orderBy(distance.asc()).fetch();
  }
}
