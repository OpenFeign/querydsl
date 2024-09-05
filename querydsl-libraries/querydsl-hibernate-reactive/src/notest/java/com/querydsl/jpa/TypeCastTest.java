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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.domain.Animal;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.InheritedProperties;
import com.querydsl.jpa.domain.QAnimal;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QInheritedProperties;
import org.junit.Test;

public class TypeCastTest {

  @Test
  public void mappedSuperclass() {
    var subClass = QInheritedProperties.inheritedProperties;
    var superClass = subClass._super;

    assertThat(superClass.getType()).isEqualTo(InheritedProperties.class);
    //        assertEquals(InheritedProperties.class.getSimpleName(), superClass.getEntityName());
    assertThat(superClass).hasToString("inheritedProperties");
  }

  //    @Test
  //    public void mappedSuperclass2() {
  //        QInheritedProperties subClass = QInheritedProperties.inheritedProperties;
  //        QSuperclass superClass = new QSuperclass(subClass.getMetadata());
  //
  //        assertEquals(Superclass.class, superClass.getType());
  //        assertEquals(Superclass.class.getSimpleName(), superClass.getEntityName());
  //        assertEquals("inheritedProperties", superClass.toString());
  //    }

  @Test
  public void subClassToSuper() {
    var cat = QCat.cat;
    var animal = new QAnimal(cat);

    assertThat(animal.getType()).isEqualTo(Cat.class);
    //        assertEquals(Cat.class.getSimpleName(), animal.getEntityName());
    assertThat(animal).hasToString("cat");
  }

  @Test
  public void subClassToSuper2() {
    var cat = QCat.cat;
    var animal = new QAnimal(cat.getMetadata());

    assertThat(animal.getType()).isEqualTo(Animal.class);
    //        assertEquals(Animal.class.getSimpleName(), animal.getEntityName());
    assertThat(animal).hasToString("cat");
  }

  @Test
  public void superClassToSub() {
    var animal = QAnimal.animal;
    var cat = new QCat(animal.getMetadata());

    assertThat(cat.getType()).isEqualTo(Cat.class);
    //        assertEquals(Cat.class.getSimpleName(), cat.getEntityName());
    assertThat(cat).hasToString("animal");
  }
}
