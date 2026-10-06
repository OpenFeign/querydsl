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
import static com.querydsl.vector.VectorExpressions.createConstantVector;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.vector.VectorOps;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractSQLVectorTest {

  private static final float[] QUERY_EMBEDDING = {1, 0, 0};

  private Connection connection;

  private SQLQueryFactory queryFactory;

  protected abstract Connection connect() throws SQLException;

  protected abstract SQLTemplates createTemplates();

  protected abstract void recreateVectorDocumentTable(Connection connection) throws SQLException;

  protected Set<VectorOps> unsupportedOperators() {
    return EnumSet.noneOf(VectorOps.class);
  }

  @BeforeAll
  void connectCreateTableAndInsertSampleDocuments() throws SQLException {
    connection = connect();
    recreateVectorDocumentTable(connection);
    queryFactory = new SQLQueryFactory(new Configuration(createTemplates()), () -> connection);
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
  void dropVectorDocumentTableAndCloseConnection() throws SQLException {
    try (var stmt = connection.createStatement()) {
      stmt.execute("drop table vector_document");
    }
    connection.close();
  }

  @Test
  void selectEmbeddingReturnsInsertedFloats() {
    var embedding =
        queryFactory.select(document.embedding).from(document).where(document.id.eq(3L)).fetchOne();

    assertThat(embedding).containsExactly(0.9f, 0.1f, 0f);
  }

  @Test
  void orderByL2Distance() {
    assertThat(fetchTitlesOrderedAscendingBy(document.embedding.l2Distance(QUERY_EMBEDDING)))
        .containsExactly("x-axis", "near x-axis", "y-axis", "diagonal");
  }

  @Test
  void orderByCosineDistance() {
    assertThat(fetchTitlesOrderedAscendingBy(document.embedding.cosineDistance(QUERY_EMBEDDING)))
        .containsExactly("x-axis", "near x-axis", "diagonal", "y-axis");
  }

  @Test
  void orderByNegativeInnerProduct() {
    assumeSupported(VectorOps.NEGATIVE_INNER_PRODUCT);
    assertThat(
            fetchTitlesOrderedAscendingBy(document.embedding.negativeInnerProduct(QUERY_EMBEDDING)))
        .containsExactly("diagonal", "x-axis", "near x-axis", "y-axis");
  }

  @Test
  void limitToTwoNearestByL2Distance() {
    var titles =
        queryFactory
            .select(document.title)
            .from(document)
            .orderBy(document.embedding.l2Distance(QUERY_EMBEDDING).asc())
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
            .where(document.embedding.cosineDistance(QUERY_EMBEDDING).lt(0.1))
            .orderBy(document.id.asc())
            .fetch();

    assertThat(titles).containsExactly("x-axis", "near x-axis");
  }

  @Test
  void selectVectorMetricsOfSingleDocument() {
    var embeddingPath = document.embedding;
    var metricsTuple =
        queryFactory
            .select(
                embeddingPath.l2Distance(QUERY_EMBEDDING), embeddingPath.l2Distance(embeddingPath))
            .from(document)
            .where(document.id.eq(4L))
            .fetchOne();

    assertThat(metricsTuple.get(embeddingPath.l2Distance(QUERY_EMBEDDING)))
        .isCloseTo(3.0, within(1e-6));
    assertThat(metricsTuple.get(embeddingPath.l2Distance(embeddingPath)))
        .isCloseTo(0.0, within(1e-6));
  }

  @Test
  void l2SquaredDistanceOfSingleDocument() {
    assumeSupported(VectorOps.L2_SQUARED_DISTANCE);
    assertThat(fetchDiagonalDocumentMetric(document.embedding.l2SquaredDistance(QUERY_EMBEDDING)))
        .isCloseTo(9.0, within(1e-6));
  }

  @Test
  void innerProductOfSingleDocument() {
    assumeSupported(VectorOps.INNER_PRODUCT);
    assertThat(fetchDiagonalDocumentMetric(document.embedding.innerProduct(QUERY_EMBEDDING)))
        .isCloseTo(2.0, within(1e-6));
  }

  @Test
  void l1DistanceOfSingleDocument() {
    assumeSupported(VectorOps.L1_DISTANCE);
    assertThat(fetchDiagonalDocumentMetric(document.embedding.l1Distance(QUERY_EMBEDDING)))
        .isCloseTo(5.0, within(1e-6));
  }

  @Test
  void dimensionCountAndL2DistanceOfConstantVector() {
    var dimensionCountAndL2DistanceRow =
        queryFactory
            .select(
                createConstantVector(QUERY_EMBEDDING).dimensionCount(),
                createConstantVector(QUERY_EMBEDDING).l2Distance(new float[] {1, 0, 2}))
            .from(document)
            .where(document.id.eq(1L))
            .fetchOne();

    assertThat(dimensionCountAndL2DistanceRow.get(0, Integer.class)).isEqualTo(3);
    assertThat(dimensionCountAndL2DistanceRow.get(1, Double.class)).isCloseTo(2.0, within(1e-6));
  }

  @Test
  void dimensionCount() {
    var dimensionCount =
        queryFactory
            .select(document.embedding.dimensionCount())
            .from(document)
            .where(document.id.eq(2L))
            .fetchOne();

    assertThat(dimensionCount).isEqualTo(3);
  }

  @Test
  void l2NormOfSingleDocument() {
    assumeSupported(VectorOps.L2_NORM);
    assertThat(fetchDiagonalDocumentMetric(document.embedding.l2Norm()))
        .isCloseTo(Math.sqrt(12), within(1e-6));
  }

  @Test
  void orderByCosineDistanceWithInlinedLiterals() {
    var literalsConfiguration = new Configuration(createTemplates());
    literalsConfiguration.setUseLiterals(true);
    var nearestTitle =
        new SQLQueryFactory(literalsConfiguration, () -> connection)
            .select(document.title)
            .from(document)
            .orderBy(document.embedding.cosineDistance(new float[] {0, 1, 0}).asc())
            .limit(1)
            .fetchOne();

    assertThat(nearestTitle).isEqualTo("y-axis");
  }

  private Double fetchDiagonalDocumentMetric(NumberExpression<Double> metric) {
    return queryFactory.select(metric).from(document).where(document.id.eq(4L)).fetchOne();
  }

  private void assumeSupported(VectorOps operator) {
    assumeFalse(
        unsupportedOperators().contains(operator), () -> operator + " is not supported here");
  }

  private List<String> fetchTitlesOrderedAscendingBy(NumberExpression<Double> dissimilarityScore) {
    return queryFactory
        .select(document.title)
        .from(document)
        .orderBy(dissimilarityScore.asc())
        .fetch();
  }
}
