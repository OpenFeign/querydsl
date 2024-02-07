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

import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static com.querydsl.jpa.JPAExpressions.selectOne;
import static org.assertj.core.api.Assertions.assertThat;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Parent;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCatSummary;
import com.querydsl.jpa.domain.QChild;
import com.querydsl.jpa.domain.QGroup;
import com.querydsl.jpa.domain.QParent;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author tiwe
 */
@RunWith(JPATestRunner.class)
public class JPABase extends AbstractJPATest implements JPATest {

  private static final QCat cat = QCat.cat;

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  @Rule @ClassRule public static TestRule jpaProviderRule = new JPAProviderRule();

  private EntityManager entityManager;

  @Override
  protected JPAQuery<?> query() {
    return new JPAQuery<Void>(entityManager);
  }

  protected JPADeleteClause delete(EntityPath<?> path) {
    return new JPADeleteClause(entityManager, path);
  }

  @Override
  protected JPAQuery<?> testQuery() {
    return new JPAQuery<Void>(entityManager, new DefaultQueryMetadata());
  }

  @Override
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  protected void save(Object entity) {
    entityManager.persist(entity);
    entityManager.flush();
  }

  @Test
  @NoEclipseLink
  @NoOpenJPA
  @NoHibernate
  public void connection_access() {
    assertThat(query().from(cat).select(cat).createQuery().unwrap(Connection.class)).isNotNull();
  }

  @Test
  @Ignore
  public void delete() {
    delete(cat).execute();
  }

  @Test
  public void delete2() {
    assertThat(delete(QGroup.group).execute()).isEqualTo(0);
  }

  @Test
  @NoBatooJPA
  public void delete_where() {
    delete(cat).where(cat.name.eq("XXX")).execute();
  }

  @Test
  @ExcludeIn(Target.MYSQL)
  public void delete_where_any() {
    delete(cat).where(cat.kittens.any().name.eq("XXX")).execute();
  }

  @Test
  @NoBatooJPA
  @ExcludeIn(Target.MYSQL)
  public void delete_where_subQuery_exists() {
    QCat parent = cat;
    QCat child = new QCat("kitten");

    delete(child)
        .where(
            child.id.eq(-100),
            selectOne().from(parent).where(parent.id.eq(-200), child.in(parent.kittens)).exists())
        .execute();
  }

  @Test
  @NoBatooJPA
  public void delete_where_subQuery2() {
    QChild child = QChild.child;
    QParent parent = QParent.parent;

    JPQLSubQuery<Parent> subQuery =
        selectFrom(parent).where(parent.id.eq(2), child.parent.eq(parent));
    // child.in(parent.children));

    delete(child).where(child.id.eq(1), subQuery.exists()).execute();
  }

  @Test
  public void finder() {
    Map<String, Object> conditions = new HashMap<String, Object>();
    conditions.put("name", "Bob123");

    List<Cat> cats = CustomFinder.findCustom(entityManager, Cat.class, conditions, "name");
    assertThat(cats).hasSize(1);
    assertThat(cats.getFirst().getName()).isEqualTo("Bob123");
  }

  @Test
  public void flushMode() {
    assertThat(query().from(cat).setFlushMode(FlushModeType.AUTO).select(cat).fetch()).isNotEmpty();
  }

  @Test
  @NoEclipseLink
  @NoOpenJPA
  public void hint() {
    jakarta.persistence.Query query =
        query().from(cat).setHint("org.hibernate.cacheable", true).select(cat).createQuery();

    assertThat(query).isNotNull();
    assertThat(query.getHints()).containsKey("org.hibernate.cacheable");
    assertThat(query.getResultList()).isNotEmpty();
  }

  @Test
  public void hint2() {
    assertThat(query().from(cat).setHint("org.hibernate.cacheable", true).select(cat).fetch())
        .isNotEmpty();
  }

  @Test
  @Ignore
  @NoHibernate
  @NoOpenJPA
  @NoBatooJPA
  public void hint3() {
    jakarta.persistence.Query query =
        query()
            .from(cat)
            .setHint("eclipselink.batch.type", "IN")
            .setHint("eclipselink.batch", "person.workAddress")
            .setHint("eclipselink.batch", "person.homeAddress")
            .select(cat)
            .createQuery();

    assertThat(query).isNotNull();
    assertThat(query.getHints()).containsEntry("eclipselink.batch", "person.homeAddress");
  }

  @Test
  @ExcludeIn(Target.DERBY)
  public void iterate() {
    CloseableIterator<Cat> cats = query().from(cat).select(cat).iterate();
    while (cats.hasNext()) {
      Cat cat = cats.next();
      assertThat(cat).isNotNull();
    }
    cats.close();
  }

  @Test
  public void limit1_uniqueResult() {
    assertThat(query().from(cat).limit(1).select(cat).fetchOne()).isNotNull();
  }

  @Test
  public void lockMode() {
    jakarta.persistence.Query query =
        query().from(cat).setLockMode(LockModeType.PESSIMISTIC_READ).select(cat).createQuery();
    assertThat(LockModeType.PESSIMISTIC_READ).isEqualTo(query.getLockMode());
    assertThat(query.getResultList()).isNotEmpty();
  }

  @Test
  public void lockMode2() {
    assertThat(query().from(cat).setLockMode(LockModeType.PESSIMISTIC_READ).select(cat).fetch())
        .isNotEmpty();
  }

  @Test
  public void queryExposure() {
    // save(new Cat(20));
    List<Cat> results = query().from(cat).select(cat).createQuery().getResultList();
    assertThat(results).isNotNull();
    assertThat(results).isNotEmpty();
  }

  @Test
  @Ignore // isn't a valid JPQL query
  public void subquery_uniqueResult() {
    QCat cat2 = new QCat("cat2");

    BooleanExpression exists = selectOne().from(cat2).where(cat2.eyecolor.isNotNull()).exists();
    assertThat(
            query()
                .from(cat)
                .where(cat.breed.eq(0).not())
                .select(new QCatSummary(cat.breed.count(), exists))
                .fetchOne())
        .isNotNull();
  }

  @SuppressWarnings("unchecked")
  @Test
  @NoEclipseLink
  @NoBatooJPA
  public void createQuery() {
    List<Tuple> rows = query().from(cat).select(cat.id, cat.name).createQuery().getResultList();
    for (Tuple row : rows) {
      assertThat(row.size()).isEqualTo(2);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  @NoEclipseLink
  @NoBatooJPA
  public void createQuery2() {
    List<Tuple> rows = query().from(cat).select(cat.id, cat.name).createQuery().getResultList();
    for (Tuple row : rows) {
      assertThat(row.size()).isEqualTo(2);
    }
  }

  @Test
  public void createQuery3() {
    List<String> rows = query().from(cat).select(cat.name).createQuery().getResultList();
    for (String row : rows) {
      assertThat(row).isNotNull();
    }
  }

  @Test
  @NoHibernate
  @ExcludeIn(Target.DERBY)
  public void createQuery4() {
    List<Tuple> rows =
        query().from(cat).select(new Expression<?>[] {Expressions.nullExpression()}).fetch();
    for (Tuple row : rows) {
      assertThat(row).isNotNull();
      assertThat(row.size()).isEqualTo(1);
      assertThat(row.get(Expressions.<Void>nullExpression())).isNull();
    }
  }

  @Test
  public void fetchCountResultsGroupByWithMultipleFields() {
    QueryResults<Tuple> results =
        query()
            .from(cat)
            .groupBy(cat.alive, cat.breed)
            .select(cat.alive, cat.breed, cat.id.sumLong())
            .fetchResults();

    assertThat(results.getTotal()).isEqualTo(1);
  }

  @Test
  public void fetchCountResultsGroupByWithHaving() {
    QueryResults<Tuple> results =
        query()
            .from(cat)
            .groupBy(cat.alive)
            .having(cat.id.sumLong().gt(5))
            .select(cat.alive, cat.id.sumLong())
            .fetchResults();

    assertThat(results.getTotal()).isEqualTo(1);
  }

  @Test
  public void shouldTransformWithGroupBy() {
    Map<Boolean, List<Cat>> transform =
        query().select(cat).from(cat).transform(GroupBy.groupBy(cat.alive).as(list(cat)));

    assertThat(transform).hasSize(1);
  }
}
