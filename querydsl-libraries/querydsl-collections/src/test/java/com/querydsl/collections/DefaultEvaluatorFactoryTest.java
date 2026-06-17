package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.Evaluator;
import com.querydsl.codegen.utils.EvaluatorFactory;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DefaultEvaluatorFactoryTest extends AbstractQueryTest {

  /**
   * The compiled evaluator is cached by a key that does not encode constant ordering, and its
   * arguments are supplied positionally. The factory must therefore emit constants in a
   * deterministic order matching the AST traversal so that a cached method is always invoked with
   * its arguments in the same positions it was compiled with.
   */
  @Test
  public void constants_follow_ast_order() {
    var capturing = new CapturingEvaluatorFactory();
    var factory = new DefaultEvaluatorFactory(CollQueryTemplates.DEFAULT, capturing);

    BooleanExpression filter = cat.name.eq("c1");
    for (var i = 2; i <= 12; i++) {
      filter = filter.or(cat.name.eq("c" + i));
    }

    factory.createEvaluator(new DefaultQueryMetadata(), cat, filter);

    var expectedKeys = new ArrayList<String>();
    for (var i = 1; i <= 12; i++) {
      expectedKeys.add("a" + i);
    }
    assertThat(capturing.constants.keySet()).containsExactlyElementsOf(expectedKeys);
    for (var i = 1; i <= 12; i++) {
      assertThat(capturing.constants.get("a" + i)).isEqualTo("c" + i);
    }
  }

  private static final class CapturingEvaluatorFactory implements EvaluatorFactory {

    private Map<String, Object> constants;

    @Override
    public <T> Evaluator<T> createEvaluator(
        String source,
        Class<? extends T> projectionType,
        String[] names,
        Class<?>[] classes,
        Map<String, Object> constants) {
      this.constants = constants;
      return null;
    }

    @Override
    public <T> Evaluator<T> createEvaluator(
        String source,
        ClassType projection,
        String[] names,
        Type[] types,
        Class<?>[] classes,
        Map<String, Object> constants) {
      this.constants = constants;
      return null;
    }
  }
}
