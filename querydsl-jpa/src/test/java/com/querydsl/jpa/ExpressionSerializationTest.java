package com.querydsl.jpa;

import static com.querydsl.core.testutil.Serialization.serialize;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.jpa.domain.QCat;
import java.io.IOException;
import org.junit.Test;

public class ExpressionSerializationTest {

  @Test
  public void serialize1() throws Exception {
    Expression<?> expr = QCat.cat.name.eq("test");
    Expression<?> expr2 = serialize(expr);

    assertThat(expr2).isEqualTo(expr);
    assertThat(expr2.hashCode()).isEqualTo(expr.hashCode());
  }

  @Test
  public void query() throws ClassNotFoundException, IOException {
    selectFrom(QCat.cat).where(serialize(QCat.cat.name.eq("test")));
  }
}
