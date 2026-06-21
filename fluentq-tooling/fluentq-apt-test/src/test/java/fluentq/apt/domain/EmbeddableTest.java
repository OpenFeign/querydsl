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
package fluentq.apt.domain;

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import java.util.List;
import org.junit.jupiter.api.Disabled;

@Disabled
public class EmbeddableTest {

  @QueryEntity
  public static class EntityWithEmbedded {

    public WithEntityRef e1;

    public WithStringProp e2;

    public WithEntityAndString e3;

    public WithList e4;
  }

  @QueryEmbeddable
  public static class WithEntityRef {

    public AnimalTest.Cat cat;
  }

  @QueryEmbeddable
  public static class WithStringProp {

    public String str;
  }

  @QueryEmbeddable
  public static class WithEntityAndString extends WithEntityRef {

    public String str2;
  }

  @QueryEmbeddable
  public static class WithList extends WithStringProp {

    public List<AnimalTest.Cat> cats;

    public String str3;
  }

  @QueryEntity
  @QueryEmbeddable
  public static class EntityAndEmbeddable {}

  @QuerySupertype
  @QueryEmbeddable
  public static class SuperclassAndEmbeddable {}
}
