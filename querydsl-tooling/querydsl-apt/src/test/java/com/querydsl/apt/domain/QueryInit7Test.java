/*
 * Copyright 2016, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import org.junit.Test;

public class QueryInit7Test {

  @MappedSuperclass
  public abstract static class Access<
          I extends AccessId<S, T>, S extends Serializable, T extends Serializable>
      implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId @QueryEmbedded private I id;

    public I getId() {
      return id;
    }

    public void setId(I id) {
      this.id = id;
    }
  }

  @MappedSuperclass
  public static class AccessId<S extends Serializable, T extends Serializable>
      implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne private S source;

    @ManyToOne private T target;
  }

  @Entity
  public static class Source implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  }

  @Entity
  public static class SourceToTarget1 extends Access<SourceToTargetId, Source, Target> {

    private static final long serialVersionUID = 1L;
  }

  @Entity
  public static class SourceToTarget2 extends Access<SourceToTargetId, Source, Target> {

    private static final long serialVersionUID = 1L;

    @QueryInit({"source", "target"})
    @Override
    public SourceToTargetId getId() {
      return super.getId();
    }
  }

  @Entity
  public static class SourceToTarget3 extends Access<SourceToTargetId, Source, Target> {

    private static final long serialVersionUID = 1L;

    @Column private String imNotEmpty;

    @QueryInit({"source", "target"})
    @Override
    public SourceToTargetId getId() {
      return super.getId();
    }
  }

  @Embeddable
  public static class SourceToTargetId extends AccessId<Source, Target> {

    private static final long serialVersionUID = 1L;
  }

  @Entity
  public static class Target implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  }

  @Test
  public void test1DefaultCase() {
    assertThat(QQueryInit7Test_SourceToTarget1.sourceToTarget1.id.source).isNotNull();
  }

  @Test
  public void test2AtQueryInitAndEmptyClass() {
    assertThat(QQueryInit7Test_SourceToTarget2.sourceToTarget2.id.source).isNotNull();
  }

  @Test
  public void test3AtQueryInitAndNonEmptyClass() {
    assertThat(QQueryInit7Test_SourceToTarget3.sourceToTarget3.id.source).isNotNull();
  }
}
