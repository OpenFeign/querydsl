package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Test;

public class FunctionalHelpersTest {

  @Test
  public void predicate() {
    Predicate<Cat> predicate = FunctionalHelpers.wrap(QCat.cat.name.startsWith("Ann"));
    assertThat(predicate.test(new Cat("Ann"))).isTrue();
    assertThat(predicate.test(new Cat("Bob"))).isFalse();
  }

  @Test
  public void function() {
    Function<Cat, String> function = FunctionalHelpers.wrap(QCat.cat.name);
    assertThat(function.apply(new Cat("Ann"))).isEqualTo("Ann");
    assertThat(function.apply(new Cat("Bob"))).isEqualTo("Bob");
  }
}
