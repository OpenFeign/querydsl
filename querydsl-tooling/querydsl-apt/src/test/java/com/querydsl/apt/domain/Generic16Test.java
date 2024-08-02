package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;

public class Generic16Test extends AbstractTest {

  @Entity
  public abstract static class HidaBez<B extends HidaBez<B, G>, G extends HidaBezGruppe<G, B>>
      extends CapiBCKeyedByGrundstueck {}

  @Entity
  public abstract static class HidaBezGruppe<G extends HidaBezGruppe<G, B>, B extends HidaBez<B, G>>
      extends CapiBCKeyedByGrundstueck {

    SortedSet<B> bez = new TreeSet<>();
  }

  @MappedSuperclass
  public abstract static class CapiBCKeyedByGrundstueck extends CapiBusinessClass {}

  @MappedSuperclass
  public abstract static class CapiBusinessClass implements ICapiBusinessClass {}

  public interface ICapiBusinessClass extends Comparable<ICapiBusinessClass> {}

  @Test
  public void test() {
    assertThat(QGeneric16Test_HidaBez.hidaBez).isNotNull();
    assertThat(QGeneric4Test_HidaBezGruppe.hidaBezGruppe).isNotNull();
    assertThat(
            QGeneric16Test_HidaBezGruppe.hidaBezGruppe.bez.getElementType().equals(HidaBez.class))
        .isTrue();
  }
}
