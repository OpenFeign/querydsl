package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.TypeWrapper;
import com.querydsl.jpa.domain.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JPATestRunner.class)
public class JPAQueryCustomTypeWrapperTest implements JPATest {

  private EntityManager em;

  @Override
  public void setEntityManager(EntityManager em) {
    this.em = em;
  }

  @Before
  public void setupData() {
    em.createQuery("delete from Invoice").executeUpdate();
    em.persist(new Invoice("00000000-0000-0000-0000-000000000001", "A", new Money("111")));
    em.persist(new Invoice("00000000-0000-0000-0000-000000000002", "A", new Money("222")));
    em.persist(new Invoice("00000000-0000-0000-0000-000000000003", "B", new Money("333")));
    em.flush();
    em.clear();
  }

  @Test
  public void sumWithoutWrapper_shouldThrowUnsupportedTargetTypeException() {
    // Expect IllegalArgumentException due to unsupported target type Money
    assertThatThrownBy(
            () ->
                new JPAQuery<>(em)
                    .select(QInvoice.invoice.amount.sumAggregate())
                    .from(QInvoice.invoice)
                    .fetchOne())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported target type : Money");
  }

  @Test
  public void projectionWithoutWrapper_shouldThrowException() {
    // Expect IllegalArgumentException due to unsupported target type in projection
    assertThatThrownBy(
            () ->
                new JPAQuery<>(em)
                    .select(
                        new QInvoiceSummary(
                            QInvoice.invoice.category, QInvoice.invoice.amount.sumAggregate()))
                    .from(QInvoice.invoice)
                    .groupBy(QInvoice.invoice.category)
                    .fetch())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported target type");
  }

  @Test
  public void projectionWithTypeWrapper_shouldReturnInvoiceSummary() {
    // When: Use TypeWrapper to convert BigDecimal to Money in projection
    List<InvoiceSummary> results =
        new JPAQuery<>(em)
            .select(
                new QInvoiceSummary(
                    QInvoice.invoice.category,
                    new TypeWrapper<>(
                        QInvoice.invoice.amount.sumAggregate().castToNum(BigDecimal.class),
                        Money.class,
                        Money::new)))
            .from(QInvoice.invoice)
            .groupBy(QInvoice.invoice.category)
            .fetch();

    // Then: The results should match the expected InvoiceSummary values
    assertThat(results)
        .containsExactlyInAnyOrder(
            new InvoiceSummary("A", new Money("333.00")), // 111 + 222
            new InvoiceSummary("B", new Money("333.00")) // 333
            );
  }

  @Test
  public void sumWithTypeWrapper_shouldWrapToMoney() {
    // Given: persist some Invoice instances ...

    // Create a sum expression and cast to BigDecimal
    NumberExpression<BigDecimal> sumExpr =
        QInvoice.invoice.amount.sumAggregate().castToNum(BigDecimal.class);

    // Use TypeWrapper to convert the sum result to Money
    Money result =
        new JPAQuery<>(em)
            .select(new TypeWrapper<>(sumExpr, Money.class, Money::new))
            .from(QInvoice.invoice)
            .fetchOne();

    assertThat(result).isEqualTo(new Money("666.00")); // 111 + 222 + 333 = 666

    em.clear();
  }
}
