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
import com.querydsl.core.domain.QAnimal;
import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.domain.JobFunction;
import com.querydsl.jpa.domain.Location;
import com.querydsl.jpa.domain.QDomesticCat;
import com.querydsl.jpa.domain.QEmployee;
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
        .isEqualTo("case when (cat.toes = ?1) then ?2 when (cat.toes = ?3) then ?4 else 4 end");
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
  public void visitLiteral_enum() {
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    serializer.visitLiteral(JobFunction.MANAGER);
    assertThat(serializer).hasToString("com.querydsl.jpa.domain.JobFunction.MANAGER");
  }

  @Test
  public void substring_indexOf() {
    var cat = QCat.cat;
    var serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
    cat.name.substring(cat.name.indexOf("")).accept(serializer, null);
    assertThat(serializer).hasToString("substring(cat.name,locate(?1,cat.name)-1 + ?2)");
  }
}
