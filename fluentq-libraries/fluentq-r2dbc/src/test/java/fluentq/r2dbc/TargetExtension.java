package fluentq.r2dbc;

import fluentq.core.Target;
import fluentq.core.testutil.ExcludeIn;
import fluentq.core.testutil.IncludeIn;
import java.util.Arrays;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 replacement for the former {@code TargetRule}. Disables a test (or container) when the
 * configured {@link Target} is excluded via {@link ExcludeIn} or not part of {@link IncludeIn}.
 */
public class TargetExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var target = Connections.getTarget();
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
