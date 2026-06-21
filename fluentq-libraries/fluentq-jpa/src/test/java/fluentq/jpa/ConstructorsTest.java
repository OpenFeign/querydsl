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
package fluentq.jpa;

import static fluentq.jpa.Constants.cat;

import fluentq.core.types.ConstructorExpression;
import fluentq.core.types.Expression;
import fluentq.core.types.Projections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConstructorsTest extends AbstractQueryTest {

  public static final class BookmarkDTO {}

  public static final class QBookmarkDTO extends ConstructorExpression<BookmarkDTO> {

    private static final long serialVersionUID = 2664671413344744578L;

    public QBookmarkDTO(Expression<java.lang.String> address) {
      super(BookmarkDTO.class, new Class<?>[] {String.class}, address);
    }
  }

  @Test
  @Disabled
  public void constructors() {
    ConstructorExpression<fluentq.jpa.domain.Cat> c =
        Projections.constructor(
            fluentq.jpa.domain.Cat.class, new Class<?>[] {String.class}, cat.name);
    assertToString("new " + fluentq.jpa.domain.Cat.class.getName() + "(cat.name)", c);
    assertToString(
        "new " + getClass().getName() + "$BookmarkDTO(cat.name)", new QBookmarkDTO(cat.name));
  }
}
