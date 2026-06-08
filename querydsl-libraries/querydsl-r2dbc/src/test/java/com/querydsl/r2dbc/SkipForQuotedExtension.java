package com.querydsl.r2dbc;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 replacement for the former {@code SkipForQuotedRule}. Disables test methods annotated
 * with {@link SkipForQuoted} when the active configuration uses quoting, schema printing or
 * literals.
 */
public class SkipForQuotedExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var testMethod = context.getTestMethod().orElse(null);
    if (testMethod == null || !testMethod.isAnnotationPresent(SkipForQuoted.class)) {
      return ConditionEvaluationResult.enabled("No @SkipForQuoted");
    }
    var configuration = Connections.getConfiguration();
    if (configuration == null) {
      return ConditionEvaluationResult.enabled("No configuration");
    }
    var templates = configuration.getTemplates();
    if (templates.isUseQuotes() || templates.isPrintSchema() || configuration.getUseLiterals()) {
      return ConditionEvaluationResult.disabled("Skipped for quoted templates");
    }
    return ConditionEvaluationResult.enabled("Not quoted");
  }
}
