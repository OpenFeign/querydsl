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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.querydsl.core.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QGroup;
import com.querydsl.jpa.hibernate.DefaultSessionHolder;
import com.querydsl.jpa.hibernate.HibernateDeleteClause;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.testutil.HibernateTestRunner;
import java.io.IOException;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author tiwe
 */
@RunWith(HibernateTestRunner.class)
public class HibernateBase extends AbstractJPATest implements HibernateTest {

  private static final QCat cat = QCat.cat;

  @Rule @ClassRule public static TestRule jpaProviderRule = new JPAProviderRule();

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  private Session session;

  @Override
  protected HibernateQuery<?> query() {
    return new HibernateQuery<Void>(session, getTemplates());
  }

  protected HibernateDeleteClause delete(EntityPath<?> path) {
    return new HibernateDeleteClause(session, path);
  }

  @Override
  protected HibernateQuery<?> testQuery() {
    return new HibernateQuery<Void>(
        new DefaultSessionHolder(session), getTemplates(), new DefaultQueryMetadata());
  }

  protected JPQLTemplates getTemplates() {
    return HQLTemplates.DEFAULT;
  }

  @Override
  public void setSession(Session session) {
    this.session = session;
  }

  @Override
  protected void save(Object entity) {
    session.persist(entity);
  }

  @Test
  public void query_exposure() {
    //        save(new Cat());
    List<Cat> results = query().from(cat).select(cat).createQuery().list();
    assertThat(results).isNotNull();
    assertThat(results).isNotEmpty();
  }

  @Test
  public void delete() {
    assertThat(delete(QGroup.group).execute()).isEqualTo(0);
  }

  @Test
  public void with_comment() {
    query().from(cat).setComment("my comment").select(cat).fetch();
  }

  @Test
  public void lockMode() {
    query().from(cat).setLockMode(cat, LockMode.PESSIMISTIC_WRITE).select(cat).fetch();
  }

  @Test
  public void flushMode() {
    query().from(cat).setFlushMode(org.hibernate.FlushMode.AUTO).select(cat).fetch();
  }

  @Test
  public void scroll() throws IOException {
    CloseableIterator<Cat> cats =
        new ScrollableResultsIterator<Cat>(
            query().from(cat).select(cat).createQuery().getResultList());
    assertThat(cats).hasNext();
    while (cats.hasNext()) {
      assertThat(cats.next()).isNotNull();
    }
    cats.close();
  }

  @Test
  public void scrollTuple() throws IOException {
    CloseableIterator<Tuple> rows =
        new ScrollableResultsIterator<Tuple>(
            query().from(cat).select(cat.name, cat.birthdate).createQuery().getResultList());
    assertThat(rows).hasNext();
    while (rows.hasNext()) {
      var row = rows.next();
      assertThat(row.size()).isEqualTo(2);
    }
    rows.close();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void createQuery() {
    List<Tuple> rows = query().from(cat).select(cat.id, cat.name).createQuery().list();
    for (Tuple row : rows) {
      assertThat(row.size()).isEqualTo(2);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void createQuery2() {
    List<Tuple> rows =
        query().from(cat).select(new Expression[] {cat.id, cat.name}).createQuery().list();
    for (Tuple row : rows) {
      assertThat(row.size()).isEqualTo(2);
    }
  }

  @Test
  public void createQuery3() {
    List<String> rows = query().from(cat).select(cat.name).createQuery().list();
    for (String row : rows) {
      assertThat(row instanceof String).isTrue();
    }
  }

  @Test
  @ExcludeIn(Target.DERBY)
  public void createQuery4() {
    assertDoesNotThrow(
        () ->
            query()
                .from(cat)
                .leftJoin(cat.kittens)
                .fetchJoin()
                .distinct()
                .transform(GroupBy.groupBy(cat.id).as(cat)));
  }

  @Test
  public void subQueryWithLimitAndOffset() {
    var kitten = new QCat("kitten");

    List<Integer> allIds = query().from(kitten).orderBy(kitten.id.asc()).select(kitten.id).fetch();

    List<Integer> expectedIds = allIds.subList(1, 4);

    List<Cat> results =
        query()
            .from(cat)
            .where(
                cat.id.in(
                    JPAExpressions.select(kitten.id)
                        .from(kitten)
                        .orderBy(kitten.id.asc())
                        .limit(3)
                        .offset(1)))
            .orderBy(cat.id.asc())
            .select(cat)
            .fetch();

    assertThat(results).hasSize(3);
    assertThat(results.stream().map(Cat::getId).toList()).isEqualTo(expectedIds);
  }

  @Test
  public void subQueryWithLimitInSelectClause() {
    var firstCat = query().from(cat).orderBy(cat.id.asc()).select(cat).fetchFirst();
    var mate = new QCat("mate");

    Integer result =
        query()
            .from(cat)
            .where(cat.id.eq(firstCat.getId()))
            .select(
                JPAExpressions.select(mate.id)
                    .from(mate)
                    .where(mate.name.eq(firstCat.getName()))
                    .orderBy(mate.id.asc())
                    .limit(1))
            .fetchFirst();

    assertThat(result).isEqualTo(firstCat.getId());
  }

  @Test
  public void subQueryWithOffsetOnly() {
    var kitten = new QCat("kitten");

    List<Integer> allIds = query().from(kitten).orderBy(kitten.id.asc()).select(kitten.id).fetch();

    List<Integer> expectedIds = allIds.subList(2, allIds.size());

    List<Cat> results =
        query()
            .from(cat)
            .where(
                cat.id.in(
                    JPAExpressions.select(kitten.id)
                        .from(kitten)
                        .orderBy(kitten.id.asc())
                        .offset(2)))
            .orderBy(cat.id.asc())
            .select(cat)
            .fetch();

    assertThat(results).hasSize(expectedIds.size());
    assertThat(results.stream().map(Cat::getId).toList()).isEqualTo(expectedIds);
  }

  @Test
  public void cteWithInnerJoinAndHint() {
    // does not work before Hibernate 6.5,
    // see https://hibernate.atlassian.net/browse/HHH-17897

    // find the heaviest cat that is lighter than Felix
    var felix = new QCat("felix");
    Cat result =
        query()
            .withNotMaterializedHint(
                felix,
                JPAExpressions.select(QCat.cat.bodyWeight.as(felix.bodyWeight))
                    .from(QCat.cat)
                    .where(QCat.cat.name.eq("Felix123")))
            .select(QCat.cat)
            .from(felix)
            .join(QCat.cat)
            .on(QCat.cat.bodyWeight.lt(felix.bodyWeight))
            .orderBy(QCat.cat.bodyWeight.desc())
            .limit(1)
            .fetchOne();
    assertThat(result)
        .hasFieldOrPropertyWithValue("id", 2)
        .hasFieldOrPropertyWithValue("name", "Ruth123");
  }

  @Test
  public void cteWithCrossJoinAndCustomColumn() {
    // all cats in ascending order by comparing their weight to the most average weight of all cats
    var avgCat = new QCat("avgcat");
    NumberPath<Double> avgWeightColumn = Expressions.numberPath(Double.class, avgCat, "avgweight");
    List<Cat> results =
        query()
            .with(
                avgCat,
                JPAExpressions.select(QCat.cat.bodyWeight.avg().as(avgWeightColumn)).from(QCat.cat))
            .select(QCat.cat)
            .from(QCat.cat, avgCat)
            .orderBy(QCat.cat.bodyWeight.subtract(avgWeightColumn).abs().asc(), QCat.cat.id.asc())
            .fetch();
    // the average body weights of all cats is 3.5
    assertThat(results)
        .hasSize(6)
        .satisfiesExactly(
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 3)
                    .hasFieldOrPropertyWithValue("bodyWeight", 3.0D),
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 4)
                    .hasFieldOrPropertyWithValue("bodyWeight", 4.0D),
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 2)
                    .hasFieldOrPropertyWithValue("bodyWeight", 2.0D),
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 5)
                    .hasFieldOrPropertyWithValue("bodyWeight", 5.0D),
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 1)
                    .hasFieldOrPropertyWithValue("bodyWeight", 1.0D),
            cat ->
                assertThat(cat)
                    .hasFieldOrPropertyWithValue("id", 6)
                    .hasFieldOrPropertyWithValue("bodyWeight", 6.0D));
  }

  @Test
  public void multipleCtes() {
    QCat felix = new QCat("felix");
    QCat felixMates = new QCat("felixMates");
    List<Integer> results =
        query()
            .with(
                felix,
                JPAExpressions.select(QCat.cat.id.as(felix.id))
                    .from(QCat.cat)
                    .where(QCat.cat.name.eq("Felix123")))
            .with(
                felixMates,
                JPAExpressions.select(QCat.cat.id.as(QCat.cat.id))
                    .from(QCat.cat)
                    .innerJoin(felix)
                    .on(QCat.cat.mate.id.eq(felix.id)))
            .select(felixMates.id)
            .from(felixMates)
            .fetch();
    assertThat(results).hasSize(1).isEqualTo(List.of(4));
  }
}
