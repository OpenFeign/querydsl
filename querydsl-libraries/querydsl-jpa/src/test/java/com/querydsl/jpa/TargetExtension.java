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

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import java.util.Arrays;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 replacement for the former {@code TargetRule}. Disables a test (or container) when the
 * active {@link Mode#target} is excluded via {@link ExcludeIn} or not part of {@link IncludeIn}.
 */
public class TargetExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var target = Mode.target.get();
    if (target == null) {
      return ConditionEvaluationResult.enabled("No target configured");
    }
    var element = context.getElement().orElse(null);
    if (element == null) {
      return ConditionEvaluationResult.enabled("No annotated element");
    }
    // excluded in given targets
    var ex = element.getAnnotation(ExcludeIn.class);
    if (ex != null && Arrays.asList(ex.value()).contains(target)) {
      return ConditionEvaluationResult.disabled("Excluded in target " + target);
    }
    // included only in given targets
    var in = element.getAnnotation(IncludeIn.class);
    if (in != null && !Arrays.asList(in.value()).contains(target)) {
      return ConditionEvaluationResult.disabled("Not included in target " + target);
    }
    return ConditionEvaluationResult.enabled("Enabled for target " + target);
  }
}
