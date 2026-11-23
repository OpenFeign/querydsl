/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.apt.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import fluentq.core.types.dsl.NumberPath;
import java.io.Serializable;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class Inheritance5Test {

  @QuerySupertype
  public static class CommonPersistence {

    private Date createdOn;

    public Date getCreatedOn() {
      return new Date(createdOn.getTime());
    }

    public void setCreatedOn(Date createdOn) {
      this.createdOn = new Date(createdOn.getTime());
    }
  }

  @QuerySupertype
  public static class CommonIdentifiable<ID extends Serializable> extends CommonPersistence {

    private ID id;

    public ID getId() {
      return id;
    }

    public void setId(ID id) {
      this.id = id;
    }
  }

  @QueryEntity
  public class Entity extends CommonIdentifiable<Long> {}

  @Test
  public void test() {
    assertThat(QInheritance5Test_Entity.entity.id.getClass()).isEqualTo(NumberPath.class);
  }
}
