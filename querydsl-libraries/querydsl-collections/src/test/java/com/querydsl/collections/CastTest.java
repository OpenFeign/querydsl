package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.Test;

public class CastTest extends AbstractQueryTest {

  @Test
  public void parents() {
    var cat = QAnimal.animal.as(QCat.class);
    assertThat(cat.getMetadata().getParent()).isEqualTo(QAnimal.animal);
  }

  @Test
  public void cast() {
    assertThat(
            query()
                .from(QAnimal.animal, cats)
                .where(QAnimal.animal.as(QCat.class).breed.eq(0))
                .select(QAnimal.animal)
                .fetch())
        .isEqualTo(Arrays.asList(c1, c2, c3, c4));
  }

  @Test
  public void property_dereference() {
    var cat = new Cat();
    cat.setEyecolor(Color.TABBY);
    assertThat(
            CollQueryFactory.from(QAnimal.animal, cat)
                .where(QAnimal.animal.instanceOf(Cat.class))
                .select(QAnimal.animal.as(QCat.class).eyecolor)
                .fetchFirst())
        .isEqualTo(Color.TABBY);
  }
}
