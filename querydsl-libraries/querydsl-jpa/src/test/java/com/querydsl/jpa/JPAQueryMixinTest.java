package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.QDepartment;
import com.querydsl.jpa.domain.QEmployee;
import com.querydsl.jpa.domain4.QBookMark;
import com.querydsl.jpa.domain4.QBookVersion;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class JPAQueryMixinTest {

  private JPAQueryMixin<?> mixin = new JPAQueryMixin<>();

  @Test
  public void where_null() {
    mixin.where((Predicate) null);
  }

  @Test
  public void orderBy() {
    var cat = QCat.cat;
    var catMate = new QCat("cat_mate");
    mixin.from(cat);
    mixin.orderBy(cat.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(catMate.name.asc()));
  }

  @Test
  public void orderBy_nonRoot_twice() {
    var department = QDepartment.department;
    var departmentCompany = new QCompany("department_company");
    var departmentCompanyCeo = new QEmployee("department_company_ceo");
    mixin.from(department);
    mixin.orderBy(department.company.ceo.firstName.asc(), department.company.ceo.lastName.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, department),
                new JoinExpression(JoinType.LEFTJOIN, department.company.as(departmentCompany)),
                new JoinExpression(
                    JoinType.LEFTJOIN, departmentCompany.ceo.as(departmentCompanyCeo))));
    assertThat(md.getOrderBy())
        .isEqualTo(
            Arrays.asList(
                departmentCompanyCeo.firstName.asc(), departmentCompanyCeo.lastName.asc()));
  }

  @Test
  public void orderBy_where() {
    var cat = QCat.cat;
    mixin.from(cat);
    mixin.where(cat.mate.name.isNotNull());
    mixin.orderBy(cat.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, cat)));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(cat.mate.name.asc()));
  }

  @Test
  public void orderBy_groupBy() {
    var cat = QCat.cat;
    mixin.from(cat);
    mixin.groupBy(cat.mate.name);
    mixin.orderBy(cat.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, cat)));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(cat.mate.name.asc()));
  }

  @Test
  public void orderBy_operation() {
    var cat = QCat.cat;
    var catMate = new QCat("cat_mate");
    mixin.from(cat);
    mixin.orderBy(cat.mate.name.lower().asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(catMate.name.lower().asc()));
  }

  @Test
  public void orderBy_long() {
    var cat = QCat.cat;
    var catMate = new QCat("cat_mate");
    var catMateMate = new QCat("cat_mate_mate");
    mixin.from(cat);
    mixin.orderBy(cat.mate.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate)),
                new JoinExpression(JoinType.LEFTJOIN, catMate.mate.as(catMateMate))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(catMateMate.name.asc()));
  }

  @Test
  public void orderBy_reuse() {
    var cat = QCat.cat;
    var mate = new QCat("mate");
    mixin.from(cat);
    mixin.leftJoin(cat.mate, mate);
    mixin.orderBy(cat.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.mate.as(mate))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(mate.name.asc()));
  }

  @Test
  public void orderBy_long_reuse() {
    var cat = QCat.cat;
    var mate = new QCat("mate");
    var mateMate = new QCat("mate_mate");
    mixin.from(cat);
    mixin.leftJoin(cat.mate, mate);
    mixin.orderBy(cat.mate.mate.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.mate.as(mate)),
                new JoinExpression(JoinType.LEFTJOIN, mate.mate.as(mateMate))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(mateMate.name.asc()));
  }

  @Test
  public void orderBy_any() {
    var cat = QCat.cat;
    var catKittens = new QCat("cat_kittens");
    mixin.from(cat);
    mixin.orderBy(cat.kittens.any().name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, cat),
                new JoinExpression(JoinType.LEFTJOIN, cat.kittens.as(catKittens))));
    assertThat(md.getOrderBy()).isEqualTo(Collections.singletonList(catKittens.name.asc()));
  }

  @Test
  public void orderBy_embeddable() {
    var bookVersion = QBookVersion.bookVersion;
    mixin.from(bookVersion);
    mixin.orderBy(bookVersion.definition.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, bookVersion)));
    assertThat(md.getOrderBy())
        .isEqualTo(Collections.singletonList(bookVersion.definition.name.asc()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void orderBy_embeddable2() {
    var article = QArticle.article;
    var articleContentArticle = new QArticle("article_content_article");
    mixin.from(article);
    mixin.orderBy(article.content.article.name.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(
            Arrays.asList(
                new JoinExpression(JoinType.DEFAULT, article),
                new JoinExpression(
                    JoinType.LEFTJOIN, article.content.article.as(articleContentArticle))));
    assertThat(md.getOrderBy())
        .isEqualTo(Collections.singletonList(articleContentArticle.name.asc()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void orderBy_embeddable_collection() {
    var bookVersion = QBookVersion.bookVersion;
    var bookMark = new QBookMark("bookVersion_definition_bookMarks");
    mixin.from(bookVersion);
    mixin.orderBy(bookVersion.definition.bookMarks.any().comment.asc());

    var md = mixin.getMetadata();
    assertThat(md.getJoins())
        .isEqualTo(Collections.singletonList(new JoinExpression(JoinType.DEFAULT, bookVersion)));
    assertThat(md.getOrderBy())
        .isEqualTo(
            Collections.singletonList(
                Expressions.stringPath(bookVersion.definition.bookMarks, "comment").asc()));
  }

  @Test
  public void orderBy_nullsLast() {
    var cat = QCat.cat;
    mixin.from(cat);
    mixin.orderBy(cat.mate.name.asc().nullsLast());
    assertThat(mixin.getMetadata().getOrderBy().get(0).getNullHandling())
        .isEqualTo(OrderSpecifier.NullHandling.NullsLast);
  }
}
