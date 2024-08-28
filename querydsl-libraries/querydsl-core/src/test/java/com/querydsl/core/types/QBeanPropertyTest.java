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
package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.PathBuilder;
import org.junit.Test;

public class QBeanPropertyTest {

  public static class Entity {

    private Integer cId;

    private Integer eId;

    public Integer getcId() {
      return cId;
    }

    public void setcId(Integer cId) {
      this.cId = cId;
    }

    public Integer geteId() {
      return eId;
    }

    public void seteId(Integer eId) {
      this.eId = eId;
    }
  }

  @Test
  public void field_access() {
    var entity = new PathBuilder<>(Entity.class, "entity");
    var beanProjection =
        new QBean<>(
            Entity.class,
            true,
            entity.getNumber("cId", Integer.class),
            entity.getNumber("eId", Integer.class));

    var bean = beanProjection.newInstance(1, 2);
    assertThat(bean.getcId()).isEqualTo(Integer.valueOf(1));
    assertThat(bean.geteId()).isEqualTo(Integer.valueOf(2));
  }

  @Test
  public void property_access() {
    var entity = new PathBuilder<>(Entity.class, "entity");
    var beanProjection =
        new QBean<>(
            Entity.class,
            entity.getNumber("cId", Integer.class),
            entity.getNumber("eId", Integer.class));

    var bean = beanProjection.newInstance(1, 2);
    assertThat(bean.getcId()).isEqualTo(Integer.valueOf(1));
    assertThat(bean.geteId()).isEqualTo(Integer.valueOf(2));
  }
}
