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
package fluentq.codegen;

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import java.io.Serializable;

public class Embedded2Test extends AbstractExporterTest {

  @QuerySupertype
  public static class EntityCode {

    public String code;
  }

  @QuerySupertype
  public abstract static class AbstractEntity<C extends EntityCode> {

    @QueryEmbedded public C code;
  }

  @QuerySupertype
  public static class AbstractMultilingualEntity<C extends EntityCode> extends AbstractEntity<C> {}

  @QuerySupertype
  public abstract static class AbstractNamedEntity<C extends EntityCode>
      extends AbstractMultilingualEntity<C> {

    public String nameEn;

    public String nameNl;
  }

  @QueryEntity
  public static class Brand extends AbstractNamedEntity<BrandCode> {

    public Long id;
  }

  public interface Entity<T> extends Serializable {

    boolean sameIdentityAs(T other);
  }

  @QueryEmbeddable
  public static class BrandCode extends EntityCode {}
}
