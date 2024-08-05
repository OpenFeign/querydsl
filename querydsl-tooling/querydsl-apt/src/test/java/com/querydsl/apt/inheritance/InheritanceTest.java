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
package com.querydsl.apt.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import org.junit.Test;

public class InheritanceTest {

  @QueryEntity
  public abstract class BobbinGenOperation<M extends FlexPlasticFilm> extends Operation<M> {}

  @QueryEntity
  public abstract class Entity {}

  @QueryEntity
  public abstract class FlexPlastic extends Storable {}

  @QueryEntity
  public abstract class FlexPlasticFilm extends FlexPlastic implements Rimmable {}

  @QueryEntity
  public abstract class Merchandise extends Entity implements UnitConversionSupporter {}

  @QueryEntity
  public abstract class Operation<M extends Merchandise> extends Entity {}

  @QueryEntity
  public abstract class Party<A extends PartyRole> extends Entity {}

  public interface PartyRole {}

  @QueryEntity
  public class Person extends Party<PersonRole> {}

  public interface PersonRole extends PartyRole {}

  public interface Rimmable {}

  @QueryEntity
  public abstract class Storable extends Merchandise {}

  public interface UnitConversionSupporter {}

  @Test
  public void test() {
    assertThat(QInheritanceTest_BobbinGenOperation.bobbinGenOperation).isNotNull();
    assertThat(QInheritanceTest_Entity.entity).isNotNull();
    assertThat(QInheritanceTest_FlexPlastic.flexPlastic).isNotNull();
    assertThat(QInheritanceTest_FlexPlasticFilm.flexPlasticFilm).isNotNull();
    assertThat(QInheritanceTest_Merchandise.merchandise).isNotNull();
    assertThat(QInheritanceTest_Operation.operation).isNotNull();
    assertThat(QInheritanceTest_Party.party).isNotNull();
    assertThat(QInheritanceTest_Person.person).isNotNull();
  }
}
