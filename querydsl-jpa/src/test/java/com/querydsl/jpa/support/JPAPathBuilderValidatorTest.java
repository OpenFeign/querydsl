package com.querydsl.jpa.support;

import static org.assertj.core.api.Assertions.assertThat;
import com.querydsl.jpa.domain.Cat;
import java.util.Collection;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JPAPathBuilderValidatorTest {

  private EntityManagerFactory entityManagerFactory;

  @Before
  public void setUp() {
    entityManagerFactory = Persistence.createEntityManagerFactory("h2");
  }

  @After
  public void tearDown() {
    entityManagerFactory.close();
  }

  @Test
  public void validate() {
    JPAPathBuilderValidator validator =
        new JPAPathBuilderValidator(entityManagerFactory.getMetamodel());
    assertThat(validator.validate(Cat.class, "name", String.class)).isEqualTo(String.class);
    assertThat(validator.validate(Cat.class, "kittens", Collection.class)).isEqualTo(Cat.class);
    assertThat(validator.validate(Cat.class, "mate", Cat.class)).isEqualTo(Cat.class);
    assertThat(validator.validate(Cat.class, "xxx", String.class)).isNull();
    assertThat(validator.validate(Object.class, "name", String.class)).isNull();
  }
}
