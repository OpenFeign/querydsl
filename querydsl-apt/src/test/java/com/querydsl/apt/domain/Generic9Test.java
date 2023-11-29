package com.querydsl.apt.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import org.junit.Test;

public class Generic9Test {

  @MappedSuperclass
  public abstract static class CommonOrganizationalUnit<
          T extends EntityLocalized,
          E extends TenantPreference,
          P extends CommonOrganizationalUnit<?, ?, ?>>
      extends LocalizableEntity<T>
      implements Serializable, Comparable<CommonOrganizationalUnit<T, E, P>> {

    P parent;

    //        CommonOrganizationalUnit<?,?,?> parent2;
    //
    //        CommonOrganizationalUnit<T,E,P> parent3;

  }

  @MappedSuperclass
  public abstract static class ProductionSurface<
          T extends EntityLocalized,
          E extends TenantPreference,
          P extends CommonOrganizationalUnit<?, ?, ?>>
      extends CommonOrganizationalUnit<T, E, P> implements Serializable {}

  //    @Entity
  //    public class Building extends ProductionSurface<BuildingLocalized, BuildingPreference, Site>
  // {
  //
  //    }

  @MappedSuperclass
  public abstract static class EntityLocalized extends CommonEntity {}

  @Entity
  public static class Preference {}

  @Entity
  @Table(name = "preference")
  @DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
  public abstract static class TenantPreference extends Preference {}

  @MappedSuperclass
  public abstract static class CommonEntity {}

  @MappedSuperclass
  public abstract static class LocalizableEntity<T extends EntityLocalized> extends CommonEntity {}

  @Test
  public void test() {
    new QGeneric9Test_CommonOrganizationalUnit("test");
  }
}
