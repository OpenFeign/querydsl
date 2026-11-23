/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fluentq.core;

import fluentq.core.types.Templates;
import io.github.classgraph.ClassGraph;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class TemplatesTestBase {

  private final ClassGraph classGraph =
      new ClassGraph()
          .enableClassInfo()
          .acceptPackages(TemplatesTestBase.class.getPackage().getName());

  private final String modulePrefix = getClass().getPackage().getName();

  @Test
  void default_instance() {
    var templates = classGraph.scan().getSubclasses(Templates.class.getName()).loadClasses();
    Set<Class<?>> moduleSpecific =
        templates.stream().filter(MODULE_SPECIFIC).collect(Collectors.toSet());

    var softly = new SoftAssertions();
    for (Class<?> template : moduleSpecific) {
      try {
        var defaultInstance = (Templates) template.getField("DEFAULT").get(null);
        softly.assertThat(defaultInstance).isInstanceOf(template);
      } catch (Exception ex) {
        softly.fail("Failed to read DEFAULT of " + template.getName(), ex);
      }
    }
    softly.assertAll();
  }

  private final Predicate<Class<?>> objectPredicate =
      o -> Pattern.matches("class " + modulePrefix + ".*", o.toString());
  private final Predicate<Class<?>> MODULE_SPECIFIC = objectPredicate.and(topLevelClass);

  private static final Predicate<Class<?>> topLevelClass =
      input -> !input.isAnonymousClass() && !input.isMemberClass();
}
