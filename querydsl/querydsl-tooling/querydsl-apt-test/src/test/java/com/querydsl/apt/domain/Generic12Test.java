package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class Generic12Test {

  @Entity
  @Inheritance
  @DiscriminatorColumn(name = "CONTEXT")
  public abstract static class Permission {
    // some common stuff
  }

  @Entity
  @DiscriminatorValue("CHANNEL")
  public static class ChannelPermission extends Permission {
    // CP specific stuff
  }

  @Entity
  @DiscriminatorValue("SUBJECT")
  public static class SubjectPermission extends Permission {
    // SP specific stuff
  }

  // A bunch of role classes

  @Entity
  @Inheritance
  @DiscriminatorColumn(name = "CONTEXT")
  public abstract static class Role<T extends Permission> {

    @ManyToMany(targetEntity = Permission.class)
    private final List<T> permissions = new ArrayList<>();
  }

  @Entity
  @DiscriminatorValue("CHANNEL")
  public static class ChannelRole extends Role<ChannelPermission> {
    // some constructors
  }

  @Entity
  @DiscriminatorValue("SUBJECT")
  public static class SubjectRole
      extends Role { // missing type param, should be Role<SubjectPermission>
    // some constructors
  }

  @Test
  public void test() {
    assertThat(QGeneric12Test_ChannelRole.channelRole.permissions.get(0).getClass())
        .isEqualTo(QGeneric12Test_Permission.class);
    assertThat(QGeneric12Test_SubjectRole.subjectRole.permissions.get(0).getClass())
        .isEqualTo(QGeneric12Test_Permission.class);
  }
}
