package fluentq.jpa.support;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.jpa.domain.Cat;
import fluentq.jpa.domain.Domain;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JPAPathBuilderValidatorTest {

  private EntityManagerFactory entityManagerFactory;

  @BeforeEach
  public void setUp() {
    entityManagerFactory =
        Persistence.createEntityManagerFactory(
            "h2", Map.of("hibernate.loaded_classes", Domain.classes));
  }

  @AfterEach
  public void tearDown() {
    entityManagerFactory.close();
  }

  @Test
  public void validate() {
    var validator = new JPAPathBuilderValidator(entityManagerFactory.getMetamodel());
    assertThat(validator.validate(Cat.class, "name", String.class)).isEqualTo(String.class);
    assertThat(validator.validate(Cat.class, "kittens", Collection.class)).isEqualTo(Cat.class);
    assertThat(validator.validate(Cat.class, "mate", Cat.class)).isEqualTo(Cat.class);
    assertThat(validator.validate(Cat.class, "xxx", String.class)).isNull();
    assertThat(validator.validate(Object.class, "name", String.class)).isNull();
  }
}
