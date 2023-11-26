package com.querydsl.apt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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
    Assertions.assertEquals(
        EmailType.class, QManagedEmailTest_ManagedEmails.managedEmails.emails.getKeyType());
    assertEquals(
        ManagedEmailImpl.class,
        QManagedEmailTest_ManagedEmails.managedEmails.emails.getValueType());
  }
}
