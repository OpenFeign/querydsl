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

import static com.querydsl.jpa.JPAExpressions.selectOne;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.domain.*;
import java.util.Arrays;
import org.junit.Test;

public class JPQLSerializerTest {

  @Test
  public void and_or() {
    // A.a.id.eq(theId).and(B.b.on.eq(false).or(B.b.id.eq(otherId)));
    var cat = QCat.cat;
    Predicate pred = cat.id.eq(1).and(cat.name.eq("Kitty").or(cat.name.eq("Boris")));
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.handle(pred);
    assertThat(serializer).hasToString("cat.id = ?1 and (cat.name = ?2 or cat.name = ?3)");
    assertThat(pred).hasToString("cat.id = 1 && (cat.name = Kitty || cat.name = Boris)");
  }

  @Test
  public void case1() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    Expression<?> expr =
        Expressions.cases().when(cat.toes.eq(2)).then(2).when(cat.toes.eq(3)).then(3).otherwise(4);
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo("case when (cat.toes = ?1) then ?2 when (cat.toes = ?3) then ?4 else ?5 end");
  }

  @Test
  public void case1_hibernate() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    Expression<?> expr =
        Expressions.cases().when(cat.toes.eq(2)).then(2).when(cat.toes.eq(3)).then(3).otherwise(4);
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo("case when (cat.toes = ?1) then 2 when (cat.toes = ?2) then 3 else 4 end");
  }

  @Test
  public void case2() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    Expression<?> expr =
        Expressions.cases()
            .when(cat.toes.eq(2))
            .then(cat.id.multiply(2))
            .when(cat.toes.eq(3))
            .then(cat.id.multiply(3))
            .otherwise(4);
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            case when (cat.toes = ?1) then (cat.id * ?2) when (cat.toes = ?3) then (cat.id * ?4)\
             else ?5 end\
            """);
  }

  @Test
  public void case2_hibernate() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    Expression<?> expr =
        Expressions.cases()
            .when(cat.toes.eq(2))
            .then(cat.id.multiply(2))
            .when(cat.toes.eq(3))
            .then(cat.id.multiply(3))
            .otherwise(4);
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            case when (cat.toes = ?1) then (cat.id * ?2) when (cat.toes = ?3) then (cat.id * ?4)\
             else 4 end\
            """);
  }

  @Test
  public void count() {
    var cat = QCat.cat;
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.setProjection(cat.mate.countDistinct());
    var serializer1 = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer1.serialize(md, true, null);
    assertThat(serializer1.toString())
        .isEqualTo("select count(count(distinct cat.mate))\n" + "from Cat cat");

    var serializer2 = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer2.serialize(md, false, null);
    assertThat(serializer2.toString())
        .isEqualTo("select count(distinct cat.mate)\n" + "from Cat cat");
  }

  @Test
  public void fromWithCustomEntityName() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    EntityPath<Location> entityPath = new EntityPathBase<>(Location.class, "entity");
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, entityPath);
    serializer.serialize(md, false, null);
    assertThat(serializer).hasToString("select entity\nfrom Location2 entity");
  }

  @Test
  public void join_with() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.addJoin(JoinType.INNERJOIN, cat.mate);
    md.addJoinCondition(cat.mate.alive);
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo("select cat\nfrom Cat cat\n  inner join cat.mate with cat.mate.alive");
  }

  @Test
  public void normalizeNumericArgs() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    NumberPath<Double> doublePath = Expressions.numberPath(Double.class, "doublePath");
    serializer.handle(doublePath.add(1));
    serializer.handle(doublePath.between((float) 1.0, 1L));
    serializer.handle(doublePath.lt((byte) 1));
    for (Object constant : serializer.getConstants()) {
      assertThat(constant.getClass()).isEqualTo(Double.class);
    }
  }

  @Test
  public void delete_clause_uses_dELETE_fROM() {
    var employee = QEmployee.employee;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, employee);
    md.addWhere(employee.lastName.isNull());
    serializer.serializeForDelete(md);
    assertThat(serializer.toString())
        .isEqualTo("delete from Employee employee\nwhere employee.lastName is null");
  }

  @Test
  public void delete_with_subQuery() {
    var parent = QCat.cat;
    var child = new QCat("kitten");

    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, child);
    md.addWhere(
        child
            .id
            .eq(1)
            .and(
                selectOne()
                    .from(parent)
                    .where(parent.id.eq(2), child.in(parent.kittens))
                    .exists()));
    serializer.serializeForDelete(md);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            delete from Cat kitten
            where kitten.id = ?1 and exists (select 1
            from Cat cat
            where cat.id = ?2 and kitten member of cat.kittens)\
            """);
  }

  @Test
  public void in() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.handle(Expressions.numberPath(Integer.class, "id").in(Arrays.asList(1, 2)));
    assertThat(serializer).hasToString("id in (?1)");
  }

  @Test
  public void not_in() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.handle(Expressions.numberPath(Integer.class, "id").notIn(Arrays.asList(1, 2)));
    assertThat(serializer).hasToString("id not in (?1)");
  }

  @Test
  public void like() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.handle(Expressions.stringPath("str").contains("abc!"));
    assertThat(serializer).hasToString("str like ?1 escape '!'");
    assertThat(serializer.getConstants().get(0)).hasToString("%abc!!%");
  }

  @Test
  public void stringContainsIc() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.handle(Expressions.stringPath("str").containsIgnoreCase("ABc!"));
    assertThat(serializer).hasToString("lower(str) like ?1 escape '!'");
    assertThat(serializer.getConstants().get(0)).hasToString("%abc!!%");
  }

  @Test
  public void substring() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    var cat = QCat.cat;
    serializer.handle(cat.name.substring(cat.name.length().subtract(1), 1));
    assertThat(serializer.toString())
        .isEqualTo("substring(cat.name,length(cat.name) + ?1,?2 - (length(cat.name) - ?3))");
  }

  @Test
  public void nullsFirst() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.addOrderBy(cat.name.asc().nullsFirst());
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo("""
			select cat
			from Cat cat
			order by cat.name asc nulls first""");
  }

  @Test
  public void nullsLast() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.addOrderBy(cat.name.asc().nullsLast());
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo("""
			select cat
			from Cat cat
			order by cat.name asc nulls last""");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void treat() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.addJoin(JoinType.JOIN, cat.mate.as((Path) QDomesticCat.domesticCat));
    md.setProjection(QDomesticCat.domesticCat);
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select domesticCat
            from Cat cat
              inner join treat(cat.mate as DomesticCat) as domesticCat\
            """);
  }

  @Test
  public void treated_path() {
    var animal = QAnimal.animal;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, animal);

    md.addWhere(JPAExpressions.treat(animal, QCat.class).breed.eq(1));
    md.setProjection(animal);
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select animal
            from Animal animal
            where treat(animal as Cat).breed = ?1\
            """);
  }

  @Test
  public void openJPA_variables() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(OpenJPATemplates.DEFAULT);
    QueryMetadata md = new DefaultQueryMetadata();
    md.addJoin(JoinType.DEFAULT, cat);
    md.addJoin(JoinType.INNERJOIN, cat.mate);
    md.addJoinCondition(cat.mate.alive);
    serializer.serialize(md, false, null);
    assertThat(serializer.toString())
        .isEqualTo("select cat_\nfrom Cat cat_\n  inner join cat_.mate on cat_.mate.alive");
  }

  @Test
  public void visitLiteral_boolean() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visitLiteral(Boolean.TRUE);
    assertThat(serializer).hasToString("true");
  }

  @Test
  public void visitLiteral_number() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visitLiteral(1.543);
    assertThat(serializer).hasToString("1.543");
  }

  @Test
  public void visitLiteral_string() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visitLiteral("abc''def");
    assertThat(serializer).hasToString("'abc''''def'");
  }

  @Test
  public void substring_indexOf() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    cat.name.substring(cat.name.indexOf("")).accept(serializer, null);
    assertThat(serializer).hasToString("substring(cat.name,locate(?1,cat.name)-1 + ?2)");
  }

  @Test
  public void case_enumConversion() {
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);

    Expression<?> expr =
        new CaseBuilder()
            .when(Expressions.TRUE)
            .then(JobFunction.MANAGER)
            .otherwise(JobFunction.CONSULTANT);

    serializer.handle(expr);

    assertThat(serializer.toString())
        .isEqualTo("case when true then 'MANAGER' else 'CONSULTANT' end");
  }

  @Test
  public void inClause_enumCollection() {
    QAnimal animal = QAnimal.animal;
    Expression<?> predicate = animal.color.in(Arrays.asList(Color.BLACK, Color.TABBY));
    JPQLSerializer serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    serializer.handle(predicate);
    assertThat(serializer.toString()).isEqualTo("animal.color in ?1");
    Object constant = serializer.getConstants().get(0);
    assertThat(constant.toString()).isEqualTo("[BLACK, TABBY]");
  }

  @Test
  public void visit_subQueryExpression_with_limit_jpql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).limit(5);
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString()).isEqualTo("(select cat.id\nfrom Cat cat)");
    assertThat(serializer.getConstants()).isEmpty();
  }

  @Test
  public void visit_subQueryExpression_with_limit_hql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).limit(5);
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString()).isEqualTo("(select cat.id\nfrom Cat cat\nlimit ?1)");
    assertThat(serializer.getConstants()).hasSize(1);
    assertThat(serializer.getConstants().getFirst()).isEqualTo(5L);
  }

  @Test
  public void visit_subQueryExpression_with_offset_jpql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).offset(10);
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString()).isEqualTo("(select cat.id\nfrom Cat cat)");
    assertThat(serializer.getConstants()).isEmpty();
  }

  @Test
  public void visit_subQueryExpression_with_offset_hql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).offset(10);
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString()).isEqualTo("(select cat.id\nfrom Cat cat\noffset ?1)");
    assertThat(serializer.getConstants()).hasSize(1);
    assertThat(serializer.getConstants().getFirst()).isEqualTo(10L);
  }

  @Test
  public void visit_subQueryExpression_with_limit_and_offset_jpql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).limit(5).offset(10);
    var serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString()).isEqualTo("(select cat.id\nfrom Cat cat)");
    assertThat(serializer.getConstants()).isEmpty();
  }

  @Test
  public void visit_subQueryExpression_with_limit_and_offset_hql() {
    var cat = QCat.cat;
    var subQuery = JPAExpressions.select(cat.id).from(cat).limit(5).offset(10);
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visit(subQuery, null);

    assertThat(serializer.toString())
        .isEqualTo("(select cat.id\nfrom Cat cat\nlimit ?1\noffset ?2)");
    assertThat(serializer.getConstants()).hasSize(2);
    assertThat(serializer.getConstants().get(0)).isEqualTo(5L);
    assertThat(serializer.getConstants().get(1)).isEqualTo(10L);
  }

  @Test
  public void visit_nested_subQueryExpression_with_modifiers_hql() {
    var cat = QCat.cat;
    var mate = new QCat("mate");

    var innerSubQuery = JPAExpressions.select(mate.id).from(mate).limit(3);

    var outerSubQuery =
        JPAExpressions.select(cat.id).from(cat).where(cat.id.in(innerSubQuery)).limit(5);

    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visit(outerSubQuery, null);

    assertThat(serializer.toString())
        .isEqualTo(
            """
        (select cat.id
        from Cat cat
        where cat.id in (select mate.id
        from Cat mate
        limit ?1)
        limit ?2)""");
    assertThat(serializer.getConstants()).hasSize(2);
    assertThat(serializer.getConstants().get(0)).isEqualTo(3L);
    assertThat(serializer.getConstants().get(1)).isEqualTo(5L);
  }

  @Test
  public void subquery_in_where_clause_with_modifiers_jpql() {
    var cat = QCat.cat;
    var kitten = new QCat("kitten");

    QueryMetadata mainQuery = new DefaultQueryMetadata();
    mainQuery.addJoin(JoinType.DEFAULT, cat);

    SubQueryExpression<Integer> subQuery =
        JPAExpressions.select(kitten.id)
            .from(kitten)
            .where(kitten.name.isNotNull())
            .limit(5)
            .offset(10);

    mainQuery.addWhere(cat.id.in(subQuery));
    mainQuery.setProjection(cat);

    var jpqlSerializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    jpqlSerializer.serialize(mainQuery, false, null);

    assertThat(jpqlSerializer.toString())
        .isEqualTo(
            """
        select cat
        from Cat cat
        where cat.id in (select kitten.id
        from Cat kitten
        where kitten.name is not null)""");
    assertThat(jpqlSerializer.getConstants()).isEmpty();
  }

  @Test
  public void subquery_in_where_clause_with_modifiers_hql() {
    var cat = QCat.cat;
    var kitten = new QCat("kitten");

    QueryMetadata mainQuery = new DefaultQueryMetadata();
    mainQuery.addJoin(JoinType.DEFAULT, cat);

    SubQueryExpression<Integer> subQuery =
        JPAExpressions.select(kitten.id)
            .from(kitten)
            .where(kitten.name.isNotNull())
            .limit(5)
            .offset(10);

    mainQuery.addWhere(cat.id.in(subQuery));
    mainQuery.setProjection(cat);

    var hqlSerializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    hqlSerializer.serialize(mainQuery, false, null);

    assertThat(hqlSerializer.toString())
        .isEqualTo(
            """
        select cat
        from Cat cat
        where cat.id in (select kitten.id
        from Cat kitten
        where kitten.name is not null
        limit ?1
        offset ?2)""");
    assertThat(hqlSerializer.getConstants()).hasSize(2);
    assertThat(hqlSerializer.getConstants().get(0)).isEqualTo(5L);
    assertThat(hqlSerializer.getConstants().get(1)).isEqualTo(10L);
  }

  @Test
  public void subquery_in_select_clause_with_modifiers_jpql() {
    var cat = QCat.cat;
    var mate = new QCat("mate");

    QueryMetadata mainQuery = new DefaultQueryMetadata();
    mainQuery.addJoin(JoinType.DEFAULT, cat);

    SubQueryExpression<Integer> subQuery =
        JPAExpressions.select(mate.weight.max())
            .from(mate)
            .where(mate.name.eq(cat.name))
            .limit(1)
            .offset(2);

    mainQuery.setProjection(subQuery);

    var jpqlSerializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
    jpqlSerializer.serialize(mainQuery, false, null);

    assertThat(jpqlSerializer.toString())
        .isEqualTo(
            """
        select (select max(mate.weight)
        from Cat mate
        where mate.name = cat.name)
        from Cat cat""");
    assertThat(jpqlSerializer.getConstants()).isEmpty();
  }

  @Test
  public void subquery_in_select_clause_with_modifiers_hql() {
    var cat = QCat.cat;
    var mate = new QCat("mate");

    QueryMetadata mainQuery = new DefaultQueryMetadata();
    mainQuery.addJoin(JoinType.DEFAULT, cat);

    SubQueryExpression<Integer> subQuery =
        JPAExpressions.select(mate.weight.max())
            .from(mate)
            .where(mate.name.eq(cat.name))
            .limit(1)
            .offset(2);

    mainQuery.setProjection(subQuery);

    var hqlSerializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    hqlSerializer.serialize(mainQuery, false, null);

    assertThat(hqlSerializer.toString())
        .isEqualTo(
            """
        select (select max(mate.weight)
        from Cat mate
        where mate.name = cat.name
        limit ?1
        offset ?2)
        from Cat cat""");
    assertThat(hqlSerializer.getConstants()).hasSize(2);
    assertThat(hqlSerializer.getConstants().get(0)).isEqualTo(1L);
    assertThat(hqlSerializer.getConstants().get(1)).isEqualTo(2L);
  }
}
