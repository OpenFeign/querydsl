package fluentq.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fluentq.jpa.domain.Alchemist;
import fluentq.jpa.domain.PotionEffect;
import fluentq.jpa.domain.QAlchemist;
import fluentq.jpa.impl.JPAQueryFactory;
import fluentq.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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

    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }

    queryFactory.delete(qAlchemist).where(qAlchemist.alchemistName.isNotNull()).execute();

    em.persist(
        new Alchemist(
            "Merlin",
            EnumSet.of(PotionEffect.INVISIBILITY, PotionEffect.STRENGTH),
            Collections.emptyList()));
    em.persist(new Alchemist("Circe", EnumSet.of(PotionEffect.HEALING), Collections.emptyList()));
    em.persist(
        new Alchemist(
            "Paracelsus",
            EnumSet.of(PotionEffect.STRENGTH, PotionEffect.SPEED),
            Collections.emptyList()));
    em.persist(
        new Alchemist("Zosimos", EnumSet.of(PotionEffect.INVISIBILITY), Collections.emptyList()));

    em.flush();
    em.clear();
  }

  @After
  public void tearDown() {
    if (em != null && em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
  }

  @Test
  public void knownEffectsContains_ShouldThrowFluentQException_WhenConverterIsUsed() {
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
        .isInstanceOf(fluentq.core.QueryException.class)
        .hasMessageContaining(
            "FluentQ Error: Path 'alchemist.knownEffects' is a Java collection mapped via a JPA @Converter to a basic database type.")
        .hasMessageContaining("The FluentQ operation 'IN' on this path")
        .hasMessageContaining("is not supported by JPA/Hibernate for such converted attributes");
  }

  @Test
  public void learnedSpellsContains_ShouldNOTThrowFluentQException_ForStandardCollection() {
    if (queryFactory == null) {
      throw new IllegalStateException("JPAQueryFactory not initialized.");
    }
    String searchSpell = "Fireball";

    Alchemist gandalf =
        new Alchemist(
            "Gandalf", EnumSet.of(PotionEffect.STRENGTH), List.of("Fireball", "Lightning Bolt"));
    em.persist(gandalf);
    em.flush();

    List<Alchemist> results =
        queryFactory
            .selectFrom(qAlchemist)
            .where(
                qAlchemist
                    .learnedSpells
                    .contains(searchSpell)
                    .and(qAlchemist.alchemistName.eq("Gandalf")))
            .fetch();

    assertThat(results).isNotNull();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getAlchemistName()).isEqualTo("Gandalf");
  }
}
