/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
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

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import org.junit.Ignore;

@Ignore
@SuppressWarnings("serial")
public class EmbeddableDeepTest {

  public enum SomeType {
    a,
    b;
  }

  @MappedSuperclass
  public abstract static class AValueObject implements Cloneable, Serializable {}

  @MappedSuperclass
  public abstract static class AEntity extends AValueObject {}

  // JPA

  @Entity
  public static class A extends AEntity {

    @Embedded B b;
  }

  @Embeddable
  public static class B extends AValueObject {

    @Embedded C c;
  }

  @Embeddable
  public static class C extends AValueObject {

    SomeType someType;
  }

  // plain

  //    @QueryEntity
  //    public class AA extends AValueObject {
  //
  //        @QueryEmbedded
  //        BB b;
  //
  //    }
  //
  //    @QueryEmbeddable
  //    public class BB extends AValueObject {
  //
  //        @QueryEmbedded
  //        CC c;
  //
  //    }
  //
  //    @QueryEmbeddable
  //    public class CC extends AValueObject {
  //
  //        SomeType someType;
  //
  //    }

}
