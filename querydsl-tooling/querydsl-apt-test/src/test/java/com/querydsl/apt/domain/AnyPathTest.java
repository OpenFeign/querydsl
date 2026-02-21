package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryInit;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class AnyPathTest {
  @Entity
  public static class Foo {

    @OneToMany(mappedBy = "key.foo")
    @QueryInit("key.student")
    private Set<Bar> bars = new HashSet<>();
  }

  @Entity
  public static class Bar {

    @EmbeddedId
    @QueryInit("student")
    private BarId key = new BarId();
  }

  @Embeddable
  public static class BarId {

    @ManyToOne private Student student;

    @ManyToOne private Foo foo;
  }

  @Entity
  public static class Student {}

  private BooleanExpression authorFilter(Student student) {
    // return QFoo.foo.bars.any().key.student.eq(Student student);
    return QAnyPathTest_Foo.foo.bars.any().key.student.eq(student);
  }

  @Test
  public void anyPath() {
    assertThat(authorFilter(new Student())).isNotNull();
  }
}
