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

import com.querydsl.core.Target;
import java.util.Arrays;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 replacement for the former {@code JPAProviderRule}. Disables a test annotated with {@link
 * NoEclipseLink}, {@link NoOpenJPA}, {@link NoBatooJPA} or {@link NoHibernate} when the active
 * {@link Mode#mode} matches the corresponding provider.
 */
public class JPAProviderExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var element = context.getElement().orElse(null);
    if (element == null) {
      return ConditionEvaluationResult.enabled("No annotated element");
    }
    var mode = Mode.mode.get();
    if (mode == null) {
      return ConditionEvaluationResult.enabled("No mode configured");
    }
    var noEclipseLink = element.getAnnotation(NoEclipseLink.class);
    var noOpenJPA = element.getAnnotation(NoOpenJPA.class);
    var noBatooJPA = element.getAnnotation(NoBatooJPA.class);
    var noHibernate = element.getAnnotation(NoHibernate.class);
    if (noEclipseLink != null && applies(noEclipseLink.value()) && mode.contains("-eclipselink")) {
      return ConditionEvaluationResult.disabled("Disabled for EclipseLink");
    } else if (noOpenJPA != null && applies(noOpenJPA.value()) && mode.contains("-openjpa")) {
      return ConditionEvaluationResult.disabled("Disabled for OpenJPA");
    } else if (noBatooJPA != null && applies(noBatooJPA.value()) && mode.contains("-batoo")) {
      return ConditionEvaluationResult.disabled("Disabled for BatooJPA");
    } else if (noHibernate != null && applies(noHibernate.value()) && !mode.contains("-")) {
      return ConditionEvaluationResult.disabled("Disabled for Hibernate");
    }
    return ConditionEvaluationResult.enabled("Enabled for mode " + mode);
  }

  private boolean applies(Target[] targets) {
    return targets.length == 0 || Arrays.asList(targets).contains(Mode.target.get());
  }
}
