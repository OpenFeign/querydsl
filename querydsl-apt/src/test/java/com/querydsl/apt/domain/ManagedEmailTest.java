package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import java.util.Map;
import org.junit.Test;

public class ManagedEmailTest {

  public interface ManagedEmail {}

  public enum EmailType {
    WORK,
    HOME
  }

  @Entity
  public static class ManagedEmailImpl implements ManagedEmail {}

  @Entity
  public static class ManagedEmails {

    @OneToMany(targetEntity = ManagedEmailImpl.class)
    @MapKey(name = "emailType")
    private Map<EmailType, ManagedEmail> emails;
  }

  @Test
  public void test() {
    assertThat(QManagedEmailTest_ManagedEmails.managedEmails.emails.getKeyType())
        .isEqualTo(EmailType.class);
    assertThat(QManagedEmailTest_ManagedEmails.managedEmails.emails.getValueType())
        .isEqualTo(ManagedEmailImpl.class);
  }
}
