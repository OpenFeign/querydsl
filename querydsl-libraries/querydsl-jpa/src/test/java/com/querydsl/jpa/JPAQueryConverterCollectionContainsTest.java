package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.querydsl.jpa.domain.Alchemist;
import com.querydsl.jpa.domain.PotionEffect;
import com.querydsl.jpa.domain.QAlchemist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import java.util.EnumSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JPATestRunner.class)
public class JPAQueryConverterCollectionContainsTest implements JPATest {

  private EntityManager em;
  private JPAQueryFactory queryFactory;

  @Override
  public void setEntityManager(EntityManager em) {
    this.em = em;
    if (this.em != null) {
      this.queryFactory = new JPAQueryFactory(this.em);
    }
  }

  private static final QAlchemist qAlchemist = QAlchemist.alchemist;

  @Before
  public void setUpData() {
    if (em == null) {
      throw new IllegalStateException("EntityManager has not been set by JPATestRunner.");
    }
    if (queryFactory == null) {
      queryFactory = new JPAQueryFactory(em);
    }

    boolean newTransaction = false;
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
      newTransaction = true;
    }

    queryFactory.delete(qAlchemist).where(qAlchemist.alchemistName.isNotNull()).execute();

    em.persist(
        new Alchemist("Merlin", EnumSet.of(PotionEffect.INVISIBILITY, PotionEffect.STRENGTH)));
    em.persist(new Alchemist("Circe", EnumSet.of(PotionEffect.HEALING)));
    em.persist(new Alchemist("Paracelsus", EnumSet.of(PotionEffect.STRENGTH, PotionEffect.SPEED)));
    em.persist(new Alchemist("Zosimos", EnumSet.of(PotionEffect.INVISIBILITY)));

    em.flush();
    if (newTransaction) {
      em.getTransaction().commit();
    }
    em.clear();

    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
  }

  @After
  public void tearDown() {
    if (em != null && em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
  }

  @Test
  public void knownEffectsContains_ShouldThrowQueryDSLException_WhenConverterIsUsed() {
    if (queryFactory == null) {
      throw new IllegalStateException("JPAQueryFactory not initialized.");
    }
    PotionEffect searchEffect = PotionEffect.INVISIBILITY;

    assertThatThrownBy(
            () -> {
              queryFactory
                  .selectFrom(qAlchemist)
                  .where(qAlchemist.knownEffects.contains(searchEffect))
                  .fetch();
            })
        .isInstanceOf(com.querydsl.core.QueryException.class)
        .hasMessageContaining(
            "QueryDSL Error: Path 'alchemist.knownEffects' is a Java collection mapped via a JPA @Converter to a basic database type.")
        .hasMessageContaining("The QueryDSL operation 'IN' on this path")
        .hasMessageContaining("is not supported by JPA/Hibernate for such converted attributes");
  }
}
