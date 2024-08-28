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
import com.querydsl.core.annotations.QuerySupertype;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import java.io.Serializable;
import java.util.Date;
import org.junit.Test;

/** Test multiple level superclasses with generics. */
public class Inheritance6Test {

  /*
   * Top superclass.
   */
  @QuerySupertype
  public static class CommonIdentifiable<ID extends Serializable> {
    @SuppressWarnings("unused")
    private ID id;

    @SuppressWarnings("unused")
    private Date createdOn;
  }

  /*
   * Intermediate superclass, equivalent to @MappedSuperclass.
   */
  @QuerySupertype
  public abstract static class Translation<
          T extends Translation<T, K>, K extends TranslationKey<T, K>>
      extends CommonIdentifiable<Long> {
    @SuppressWarnings("unused")
    private String value;
  }

  /*
   * Intermediate superclass, equivalent to @MappedSuperclass.
   */
  @QuerySupertype
  public abstract static class TranslationKey<
          T extends Translation<T, K>, K extends TranslationKey<T, K>>
      extends CommonIdentifiable<Long> {}

  @QueryEntity
  public static class Gloss extends Translation<Gloss, GlossKey> {}

  @QueryEntity
  public static class GlossKey extends TranslationKey<Gloss, GlossKey> {}

  @Test
  public void gloss_subtype_should_contain_fields_from_superclass() {
    assertThat(QInheritance6Test_Gloss.gloss.value.getType()).isEqualTo(String.class);
  }

  @Test
  public void intermediate_superclass_should_contain_fields_from_top_superclass() {
    var translation = QInheritance6Test_Gloss.gloss._super;
    assertThat(translation.createdOn.getClass()).isEqualTo(DateTimePath.class);
  }

  @Test
  public void gloss_subtype_should_contain_fields_from_top_superclass() {
    assertThat(QInheritance6Test_Gloss.gloss.createdOn.getClass()).isEqualTo(DateTimePath.class);
  }

  @Test
  public void gloss_subtype_should_contain_id_from_top_superclass() {
    assertThat(QInheritance6Test_Gloss.gloss.id.getClass()).isEqualTo(NumberPath.class);
  }
}
