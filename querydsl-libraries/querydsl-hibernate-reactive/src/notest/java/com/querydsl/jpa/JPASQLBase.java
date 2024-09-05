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

import com.querydsl.core.Target;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Color;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.sql.SAnimal_;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.jpa.testutil.JPATestRunner;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

@RunWith(JPATestRunner.class)
public class JPASQLBase extends AbstractSQLTest implements JPATest {

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  @Rule @ClassRule public static TestRule hibernateOnly = new JPAProviderRule();

  private final SQLTemplates templates = Mode.getSQLTemplates();

  private EntityManager entityManager;

  private final SAnimal_ cat = new SAnimal_("cat");
  private final QCat catEntity = QCat.cat;

  @Override
  protected JPASQLQuery<?> query() {
    return new JPASQLQuery<Void>(entityManager, templates);
  }

  @Override
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Before
  public void setUp() {
    if (query().from(cat).fetchCount() == 0) {
      entityManager.persist(new Cat("Beck", 1, Color.BLACK));
      entityManager.persist(new Cat("Kate", 2, Color.BLACK));
      entityManager.persist(new Cat("Kitty", 3, Color.BLACK));
      entityManager.persist(new Cat("Bobby", 4, Color.BLACK));
      entityManager.persist(new Cat("Harold", 5, Color.BLACK));
      entityManager.persist(new Cat("Tim", 6, Color.BLACK));
      entityManager.flush();
    }
  }

  private <T> void insertEntitiesForTest(final List<T> entities) {
    for (T entity : entities) {
      entityManager.persist(entity);
    }
    entityManager.flush();
  }

  private <T> void removeEntitiesForTest(final List<T> entities) {
    for (T entity : entities) {
      entityManager.remove(entity);
    }
    entityManager.flush();
  }

  @Test
  public void entityQueries_createQuery() {
    var query = query().from(cat).select(catEntity).createQuery();
    assertThat(query.getResultList()).hasSize(6);
  }

  @Test
  @ExcludeIn(Target.MYSQL)
  public void entityQueries_createQuery2() {
    var cat = new SAnimal_("CAT");

    var query = query().from(cat).select(catEntity).createQuery();
    assertThat(query.getResultList()).hasSize(6);
  }

  @Test
  public void should_fetch_results_with_factory_expression() {
    final var expectedTotalResultCount = 6L;
    final var bindings = new HashMap<String, Expression<?>>();
    bindings.put("name", cat.name);

    final var actualTotalResultCount =
        query().from(cat).select(Projections.bean(Cat.class, bindings)).fetchResults().getTotal();

    assertThat(actualTotalResultCount).isEqualTo(expectedTotalResultCount);
  }

  @Test
  public void should_get_grouped_list_by_using_fetch_results() {
    final var expectedCatColorKindCount = 1L;

    final var actualCatColorKindCount =
        query()
            .from(cat)
            .select(catEntity.color)
            .groupBy(catEntity.color)
            .fetchResults()
            .getTotal();

    assertThat(actualCatColorKindCount).isEqualTo(expectedCatColorKindCount);
  }

  @Test
  public void should_get_black_cat_count_by_using_group_by_and_having() {
    final var expectedTabbyCatCount = 2L;
    final var tabbyColorCatFoo = new Cat("Foo", 7, Color.TABBY);
    final var tabbyColorCatBar = new Cat("Bar", 8, Color.TABBY);

    insertEntitiesForTest(Arrays.asList(tabbyColorCatFoo, tabbyColorCatBar));

    final long actualTabbyCatCount =
        query()
            .from(cat)
            .select(catEntity.name.count())
            .groupBy(catEntity.color)
            .having(catEntity.name.count().eq(2L))
            .fetchOne();

    removeEntitiesForTest(Arrays.asList(tabbyColorCatFoo, tabbyColorCatBar));

    assertThat(actualTabbyCatCount).isEqualTo(expectedTabbyCatCount);
  }
}
