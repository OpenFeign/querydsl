package fluentq.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.Target;
import fluentq.core.Tuple;
import fluentq.jpa.domain.Cat;
import fluentq.jpa.domain.QCat;
import fluentq.jpa.impl.JPAQuery;
import fluentq.jpa.testutil.JPATestExtension;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JPATestExtension.class)
@Disabled
@Tag("fluentq.core.testutil.Performance")
public class QueryPerformanceTest implements JPATest {

  private static final int iterations = 1000;

  private EntityManager entityManager;

  @BeforeAll
  public static void setUpClass() {
    Mode.mode.set("h2perf");
    Mode.target.set(Target.H2);
  }

  @AfterAll
  public static void tearDownClass() {
    Mode.mode.remove();
    Mode.target.remove();
  }

  private JPAQuery<?> query() {
    return new JPAQuery<Void>(entityManager);
  }

  @BeforeEach
  public void setUp() {
    if (query().from(QCat.cat).fetchCount() == 0) {
      for (var i = 0; i < iterations; i++) {
        entityManager.persist(new Cat(String.valueOf(i), i + 100));
      }
      entityManager.flush();
    }
  }

  @Test
  public void byId_raw() {
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      var cat =
          (Cat)
              entityManager
                  .createQuery("select cat from Cat cat where id = ?")
                  .setParameter(1, i + 100)
                  .getSingleResult();
      assertThat(cat).isNotNull();
    }
    System.err.println("by id - raw" + (System.currentTimeMillis() - start));
  }

  @Test
  public void byId_qdsl() {
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      var cat = QCat.cat;
      Cat c = query().from(cat).where(cat.id.eq(i + 100)).select(cat).fetchOne();
      assertThat(c).isNotNull();
    }
    System.err.println("by id - dsl" + (System.currentTimeMillis() - start));
  }

  @Test
  public void byId_twoCols_raw() {
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      var row =
          (Object[])
              entityManager
                  .createQuery("select cat.id, cat.name from Cat cat where id = ?")
                  .setParameter(1, i + 100)
                  .getSingleResult();
      assertThat(row).isNotNull();
    }
    System.err.println("by id - 2 cols - raw" + (System.currentTimeMillis() - start));
  }

  @Test
  public void byId_twoCols_qdsl() {
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      var cat = QCat.cat;
      Tuple row = query().from(cat).where(cat.id.eq(i + 100)).select(cat.id, cat.name).fetchOne();
      assertThat(row).isNotNull();
    }
    System.err.println("by id - 2 cols - dsl" + (System.currentTimeMillis() - start));
  }

  @Override
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
}
