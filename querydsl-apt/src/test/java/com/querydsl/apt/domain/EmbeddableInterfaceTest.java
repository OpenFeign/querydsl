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

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import java.util.Collection;
import org.junit.Test;

public class EmbeddableInterfaceTest {

  @Entity
  public static class EntityClass {

    @ElementCollection(targetClass = EmbeddableClass.class)
    Collection<EmbeddableInterface> children;
  }

  @Embeddable
  public interface EmbeddableInterface {

    String getName();
  }

  @Embeddable
  public static class EmbeddableClass implements EmbeddableInterface {

    @Override
    public String getName() {
      return null;
    }
  }

  @Test
  public void type() {
    assertThat(QEmbeddableInterfaceTest_EntityClass.entityClass.children.any().getClass())
        .isEqualTo(QEmbeddableInterfaceTest_EmbeddableInterface.class);
  }

  @Test
  public void properties() {
    assertThat(QEmbeddableInterfaceTest_EmbeddableInterface.embeddableInterface.name).isNotNull();
    assertThat(QEmbeddableInterfaceTest_EmbeddableClass.embeddableClass.name).isNotNull();
  }
}
