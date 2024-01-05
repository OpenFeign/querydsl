package com.querydsl.jpa;

import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Color;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.sql.SAnimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractSQLTest {

  protected static final SAnimal cat = new SAnimal("cat");

  protected abstract AbstractSQLQuery<?, ?> query();

  public static class CatDTO {

    Cat cat;

    public CatDTO(Cat cat) {
      this.cat = cat;
    }
  }

  @Test
  public void count() {
    assertThat(query().from(cat).where(cat.dtype.eq("C")).fetchCount()).isEqualTo(6L);
  }

  @Test
  public void count_via_unique() {
    assertThat(query().from(cat).where(cat.dtype.eq("C")).select(cat.id.count()).fetchFirst())
        .isEqualTo(Long.valueOf(6));
  }

  @Test
  public void countDistinct() {
    assertThat(query().from(cat).where(cat.dtype.eq("C")).distinct().fetchCount()).isEqualTo(6L);
  }

  @Test
  public void enum_binding() {
    List<Cat> cats = query().from(cat).select(Projections.bean(Cat.class, QCat.cat.color)).fetch();
    assertThat(cats).isNotEmpty();

    for (Cat cat : cats) {
      assertThat(cat.getColor()).isEqualTo(Color.BLACK);
    }
  }

  @Test
  @Ignore
  public void entityProjections() {
    List<Cat> cats =
        query()
            .from(cat)
            .orderBy(cat.name.asc())
            .select(Projections.constructor(Cat.class, cat.name, cat.id))
            .fetch();
    assertThat(cats).hasSize(6);
    for (Cat c : cats) {
      assertThat(c.getName()).isNotNull();
      assertThat(c.getId() > 0).isTrue();
    }
  }

  @Test
  public void entityQueries() {
    QCat catEntity = QCat.cat;

    List<Cat> cats = query().from(cat).orderBy(cat.name.asc()).select(catEntity).fetch();
    assertThat(cats).hasSize(6);
    for (Cat c : cats) {
      assertThat(c.getName()).isNotNull();
    }
  }

  @Test
  public void entityQueries2() {
    SAnimal mate = new SAnimal("mate");
    QCat catEntity = QCat.cat;

    List<Cat> cats =
        query()
            .from(cat)
            .innerJoin(mate)
            .on(cat.mateId.eq(mate.id))
            .where(cat.dtype.eq("C"), mate.dtype.eq("C"))
            .select(catEntity)
            .fetch();
    assertThat(cats).isEmpty();
  }

  @Test
  public void entityQueries3() {
    QCat catEntity = new QCat("animal_");
    assertThat(query().from(catEntity).select(catEntity.toes.max()).fetchFirst().intValue())
        .isEqualTo(0);
  }

  @Test
  @NoBatooJPA
  @NoEclipseLink
  public void entityQueries4() {
    QCat catEntity = QCat.cat;
    List<Tuple> cats = query().from(cat).select(catEntity, cat.name, cat.id).fetch();
    assertThat(cats).hasSize(6);

    for (Tuple tuple : cats) {
      assertThat(tuple.get(catEntity) instanceof Cat).isTrue();
      assertThat(tuple.get(cat.name) instanceof String).isTrue();
      assertThat(tuple.get(cat.id) instanceof Integer).isTrue();
    }
  }

  @Test
  @NoBatooJPA
  @NoEclipseLink
  public void entityQueries5() {
    QCat catEntity = QCat.cat;
    SAnimal otherCat = new SAnimal("otherCat");
    QCat otherCatEntity = new QCat("otherCat");
    List<Tuple> cats = query().from(cat, otherCat).select(catEntity, otherCatEntity).fetch();
    assertThat(cats).hasSize(36);

    for (Tuple tuple : cats) {
      assertThat(tuple.get(catEntity) instanceof Cat).isTrue();
      assertThat(tuple.get(otherCatEntity) instanceof Cat).isTrue();
    }
  }

  @Test
  @NoBatooJPA
  @NoEclipseLink
  public void entityQueries6() {
    QCat catEntity = QCat.cat;
    List<CatDTO> results =
        query().from(cat).select(Projections.constructor(CatDTO.class, catEntity)).fetch();
    assertThat(results).hasSize(6);

    for (CatDTO cat : results) {
      assertThat(cat.cat instanceof Cat).isTrue();
    }
  }

  @Test
  public void entityQueries7() {
    QCompany company = QCompany.company;
    assertThat(query().from(company).select(company.officialName).fetch())
        .isEqualTo(Collections.emptyList());
  }

  @Test
  public void in() {
    assertThat(query().from(cat).where(cat.dtype.in("C", "CX")).fetchCount()).isEqualTo(6L);
  }

  @Test
  public void limit_offset() {
    assertThat(
            query()
                .from(cat)
                .orderBy(cat.id.asc())
                .limit(2)
                .offset(2)
                .select(cat.id, cat.name)
                .fetch())
        .hasSize(2);
  }

  @Test
  public void list() {
    assertThat(query().from(cat).where(cat.dtype.eq("C")).select(cat.id).fetch()).hasSize(6);
  }

  @Test
  public void list_limit_and_offset() {
    assertThat(query().from(cat).orderBy(cat.id.asc()).offset(3).limit(3).select(cat.id).fetch())
        .hasSize(3);
  }

  @Test
  public void list_limit_and_offset2() {
    List<Tuple> tuples =
        query().from(cat).orderBy(cat.id.asc()).offset(3).limit(3).select(cat.id, cat.name).fetch();
    assertThat(tuples).hasSize(3);
    assertThat(tuples.getFirst().size()).isEqualTo(2);
  }

  @Test
  public void list_limit_and_offset3() {
    List<Tuple> tuples =
        query()
            .from(cat)
            .orderBy(cat.id.asc())
            .offset(3)
            .limit(3)
            .select(Projections.tuple(cat.id, cat.name))
            .fetch();
    assertThat(tuples).hasSize(3);
    assertThat(tuples.getFirst().size()).isEqualTo(2);
  }

  @Test
  public void list_multiple() {
    print(
        query()
            .from(cat)
            .where(cat.dtype.eq("C"))
            .select(cat.id, cat.name, cat.bodyWeight)
            .fetch());
  }

  @Test
  public void list_non_path() {
    assertThat(
            query()
                .from(cat)
                .where(cat.dtype.eq("C"))
                .select(cat.birthdate.year(), cat.birthdate.month(), cat.birthdate.dayOfMonth())
                .fetch())
        .hasSize(6);
  }

  @Test
  public void list_results() {
    QueryResults<String> results =
        query().from(cat).limit(3).orderBy(cat.name.asc()).select(cat.name).fetchResults();
    assertThat(results.getResults()).isEqualTo(Arrays.asList("Beck", "Bobby", "Harold"));
    assertThat(results.getTotal()).isEqualTo(6L);
  }

  @Test
  @ExcludeIn(Target.H2)
  public void list_wildcard() {
    assertThat(query().from(cat).where(cat.dtype.eq("C")).select(Wildcard.all).fetch()).hasSize(6);
  }

  @Test
  public void list_with_count() {
    print(
        query()
            .from(cat)
            .where(cat.dtype.eq("C"))
            .groupBy(cat.name)
            .select(cat.name, cat.id.count())
            .fetch());
  }

  @Test
  public void list_with_limit() {
    assertThat(query().from(cat).limit(3).select(cat.id).fetch()).hasSize(3);
  }

  @Test
  @ExcludeIn({Target.H2, Target.MYSQL})
  public void list_with_offset() {
    assertThat(query().from(cat).orderBy(cat.id.asc()).offset(3).select(cat.id).fetch()).hasSize(3);
  }

  @Test
  @ExcludeIn(Target.HSQLDB)
  public void no_from() {
    assertThat(query().select(DateExpression.currentDate()).fetchFirst()).isNotNull();
  }

  @Test
  public void null_as_uniqueResult() {
    assertThat(
            query()
                .from(cat)
                .where(cat.name.eq(UUID.randomUUID().toString()))
                .select(cat.name)
                .fetchOne())
        .isNull();
  }

  private void print(Iterable<Tuple> rows) {
    for (Tuple row : rows) {
      System.out.println(row);
    }
  }

  @Test
  public void projections_duplicateColumns() {
    SAnimal cat = new SAnimal("cat");
    assertThat(query().from(cat).select(Projections.list(cat.count(), cat.count())).fetch())
        .hasSize(1);
  }

  @Test
  public void single_result() {
    query().from(cat).select(cat.id).fetchFirst();
  }

  @Test
  public void single_result_multiple() {
    assertThat(
            query()
                .from(cat)
                .orderBy(cat.id.asc())
                .select(new Expression<?>[] {cat.id})
                .fetchFirst()
                .get(cat.id)
                .intValue())
        .isEqualTo(1);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void union() throws SQLException {
    SubQueryExpression<Integer> sq1 = select(cat.id.max()).from(cat);
    SubQueryExpression<Integer> sq2 = select(cat.id.min()).from(cat);
    List<Integer> list = query().union(sq1, sq2).list();
    assertThat(list).isNotEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void union_all() {
    SubQueryExpression<Integer> sq1 = select(cat.id.max()).from(cat);
    SubQueryExpression<Integer> sq2 = select(cat.id.min()).from(cat);
    List<Integer> list = query().unionAll(sq1, sq2).list();
    assertThat(list).isNotEmpty();
  }

  @SuppressWarnings("unchecked")
  @Test
  @ExcludeIn({Target.DERBY, Target.POSTGRESQL})
  @Ignore // FIXME
  public void union2() {
    List<Tuple> rows =
        query()
            .union(
                select(cat.name, cat.id).from(cat).where(cat.name.eq("Beck")).distinct(),
                select(cat.name, null).from(cat).where(cat.name.eq("Kate")).distinct())
            .list();

    assertThat(rows).hasSize(2);
    for (Tuple row : rows) {
      System.err.println(row);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  @ExcludeIn(Target.DERBY)
  @Ignore // FIXME
  public void union3() {
    SAnimal cat2 = new SAnimal("cat2");
    List<Tuple> rows =
        query()
            .union(
                select(cat.id, cat2.id).from(cat).innerJoin(cat2).on(cat2.id.eq(cat.id)),
                select(cat.id, null).from(cat))
            .list();

    assertThat(rows).hasSize(12);
    int nulls = 0;
    for (Tuple row : rows) {
      System.err.println(Collections.singletonList(row));
      if (row.get(1, Object.class) == null) {
        nulls++;
      }
    }
    assertThat(nulls).isEqualTo(6);
  }

  @SuppressWarnings("unchecked")
  @Test
  @ExcludeIn({Target.DERBY, Target.POSTGRESQL})
  @Ignore // FIXME
  public void union4() {
    query()
        .union(
            cat,
            select(cat.name, cat.id).from(cat).where(cat.name.eq("Beck")).distinct(),
            select(cat.name, null).from(cat).where(cat.name.eq("Kate")).distinct())
        .select(cat.name, cat.id)
        .fetch();
  }

  @SuppressWarnings("unchecked")
  @Test
  @ExcludeIn({Target.DERBY, Target.ORACLE})
  public void union5() {
    SAnimal cat2 = new SAnimal("cat2");
    List<Tuple> rows =
        query()
            .union(
                select(cat.id, cat2.id).from(cat).join(cat2).on(cat2.id.eq(cat.id.add(1))),
                select(cat.id, cat2.id).from(cat).join(cat2).on(cat2.id.eq(cat.id.add(1))))
            .list();

    assertThat(rows).hasSize(5);
    for (Tuple row : rows) {
      int first = row.get(cat.id);
      int second = row.get(cat2.id);
      assertThat(second).isEqualTo(first + 1);
    }
  }

  @Test
  public void unique_result() {
    assertThat(
            query().from(cat).orderBy(cat.id.asc()).limit(1).select(cat.id).fetchOne().intValue())
        .isEqualTo(1);
  }

  @Test
  public void unique_result_multiple() {
    assertThat(
            query()
                .from(cat)
                .orderBy(cat.id.asc())
                .limit(1)
                .select(new Expression<?>[] {cat.id})
                .fetchOne()
                .get(cat.id)
                .intValue())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn(Target.H2)
  public void wildcard() {
    List<Tuple> rows = query().from(cat).select(cat.all()).fetch();
    assertThat(rows).hasSize(6);
    print(rows);

    //        rows = query().from(cat).fetch(cat.id, cat.all());
    //        assertEquals(6, rows.size());
    //        print(rows);
  }
}
