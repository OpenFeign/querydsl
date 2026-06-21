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
package fluentq.collections;

import fluentq.codegen.utils.ECJEvaluatorFactory;
import fluentq.codegen.utils.Evaluator;
import fluentq.codegen.utils.EvaluatorFactory;
import fluentq.codegen.utils.JDKEvaluatorFactory;
import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.SimpleType;
import fluentq.codegen.utils.model.Type;
import fluentq.codegen.utils.model.TypeCategory;
import fluentq.codegen.utils.model.Types;
import fluentq.codegen.utils.support.ClassUtils;
import fluentq.core.JoinExpression;
import fluentq.core.JoinType;
import fluentq.core.QueryMetadata;
import fluentq.core.support.CollectionAnyVisitor;
import fluentq.core.support.Context;
import fluentq.core.types.Expression;
import fluentq.core.types.FactoryExpression;
import fluentq.core.types.Operation;
import fluentq.core.types.ParamExpression;
import fluentq.core.types.ParamNotSetException;
import fluentq.core.types.Predicate;
import fluentq.core.util.PrimitiveUtils;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.jetbrains.annotations.Nullable;

/**
 * {@code DefaultEvaluatorFactory} provides Java source templates for evaluation of {@link
 * CollQuery} queries
 *
 * @author tiwe
 */
public class DefaultEvaluatorFactory {

  private final EvaluatorFactory factory;

  private final CollQueryTemplates templates;

  private final CollectionAnyVisitor collectionAnyVisitor = new CollectionAnyVisitor();

  public DefaultEvaluatorFactory(CollQueryTemplates templates) {
    this(
        templates,
        Thread.currentThread().getContextClassLoader() != null
            ? Thread.currentThread().getContextClassLoader()
            : DefaultEvaluatorFactory.class.getClassLoader());
  }

  public DefaultEvaluatorFactory(CollQueryTemplates templates, EvaluatorFactory factory) {
    this.templates = templates;
    this.factory = factory;
  }

  protected DefaultEvaluatorFactory(
      CollQueryTemplates templates, URLClassLoader classLoader, JavaCompiler compiler) {
    this.templates = templates;
    this.factory = new JDKEvaluatorFactory(classLoader, compiler);
  }

  protected DefaultEvaluatorFactory(CollQueryTemplates templates, ClassLoader classLoader) {
    this.templates = templates;
    final var systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
    if (classLoader instanceof URLClassLoader && systemJavaCompiler != null) {
      this.factory = new JDKEvaluatorFactory(classLoader, systemJavaCompiler);
    } else {
      // for OSGi and JRE compatibility
      this.factory = new ECJEvaluatorFactory(classLoader);
    }
  }

  /**
   * Create an Evaluator for the given query sources and projection
   *
   * @param <T>
   * @param metadata query metadata
   * @param sources sources of the query
   * @param projection projection of the query
   * @return evaluator
   */
  public <T> Evaluator<T> create(
      QueryMetadata metadata, List<? extends Expression<?>> sources, Expression<T> projection) {
    final var serializer = new CollQuerySerializer(templates);
    serializer.append("return ");
    if (projection instanceof FactoryExpression<?>) {
      serializer.append("(");
      serializer.append(ClassUtils.getName(projection.getType()));
      serializer.append(")(");
      serializer.handle(projection);
      serializer.append(")");
    } else {
      serializer.handle(projection);
    }
    serializer.append(";");

    var constants =
        getConstants(metadata, serializer.getConstants(), serializer.getConstantToLabel());
    var types = new Class<?>[sources.size()];
    var names = new String[sources.size()];
    for (var i = 0; i < sources.size(); i++) {
      types[i] = sources.get(i).getType();
      names[i] = sources.get(i).toString();
    }

    // normalize types
    for (var i = 0; i < types.length; i++) {
      if (PrimitiveUtils.isWrapperType(types[i])) {
        types[i] = PrimitiveUtils.unwrap(types[i]);
      }
    }

    return factory.createEvaluator(
        serializer.toString(), projection.getType(), names, types, constants);
  }

  /**
   * Create an Evaluator for the given source and filter
   *
   * @param <T>
   * @param source source of the query
   * @param filter filter of the query
   * @return evaluator
   */
  public <T> Evaluator<List<T>> createEvaluator(
      QueryMetadata metadata, Expression<? extends T> source, Predicate filter) {
    var typeName = ClassUtils.getName(source.getType());
    var ser = new CollQuerySerializer(templates);
    ser.append(
        "java.util.List<" + typeName + "> rv = new java.util.ArrayList<" + typeName + ">();\n");
    ser.append("for (" + typeName + " " + source + " : " + source + "_) {\n");
    ser.append("    try {\n");
    ser.append("        if (").handle(filter).append(") {\n");
    ser.append("            rv.add(" + source + ");\n");
    ser.append("        }\n");
    ser.append("    } catch (NullPointerException npe) { }\n");
    ser.append("}\n");
    ser.append("return rv;");

    var constants = getConstants(metadata, ser.getConstants(), ser.getConstantToLabel());

    Type sourceType = new ClassType(TypeCategory.SIMPLE, source.getType());
    var sourceListType = new ClassType(TypeCategory.SIMPLE, Iterable.class, sourceType);

    return factory.createEvaluator(
        ser.toString(),
        sourceListType,
        new String[] {source + "_"},
        new Type[] {sourceListType},
        new Class<?>[] {Iterable.class},
        constants);
  }

  /**
   * Create an Evaluator for the given sources and the given optional filter
   *
   * @param metadata query metadata
   * @param joins joins
   * @param filter where condition
   * @return evaluator
   */
  public Evaluator<List<Object[]>> createEvaluator(
      QueryMetadata metadata, List<JoinExpression> joins, @Nullable Predicate filter) {
    List<String> sourceNames = new ArrayList<>();
    List<Type> sourceTypes = new ArrayList<>();
    List<Class<?>> sourceClasses = new ArrayList<>();
    var vars = new StringBuilder();
    var ser = new CollQuerySerializer(templates);
    ser.append("java.util.List<Object[]> rv = new java.util.ArrayList<Object[]>();\n");

    List<String> anyJoinMatchers = new ArrayList<>();

    // creating context
    for (JoinExpression join : joins) {
      Expression<?> target = join.getTarget();
      var typeName = fluentq.codegen.utils.support.ClassUtils.getName(target.getType());
      if (vars.length() > 0) {
        vars.append(",");
      }
      switch (join.getType()) {
        case DEFAULT:
          ser.append("for (" + typeName + " " + target + " : " + target + "_) {\n");
          vars.append(target);
          sourceNames.add(target + "_");
          sourceTypes.add(
              new SimpleType(Types.ITERABLE, new ClassType(TypeCategory.SIMPLE, target.getType())));
          sourceClasses.add(Iterable.class);
          break;

        case INNERJOIN:
        case LEFTJOIN:
          Operation<?> alias = (Operation<?>) join.getTarget();
          var colAnyJoin =
              join.getCondition() != null && join.getCondition().toString().equals("any");
          var leftJoin = join.getType() == JoinType.LEFTJOIN;
          String matcher = null;
          if (colAnyJoin) {
            matcher = alias.getArg(1).toString() + "_matched";
            ser.append("boolean " + matcher + " = false;\n");
            anyJoinMatchers.add(matcher);
          }
          ser.append("for (" + typeName + " " + alias.getArg(1) + " : ");
          if (leftJoin) {
            ser.append(CollQueryFunctions.class.getName() + ".leftJoin(");
          }
          if (colAnyJoin) {
            var context = new Context();
            Expression<?> replacement = alias.getArg(0).accept(collectionAnyVisitor, context);
            ser.handle(replacement);
          } else {
            ser.handle(alias.getArg(0));
          }
          if (alias.getArg(0).getType().equals(Map.class)) {
            ser.append(".values()");
          }
          if (leftJoin) {
            ser.append(")");
          }
          ser.append(") {\n");
          if (matcher != null) {
            ser.append("if (!" + matcher + ") {\n");
          }
          vars.append(alias.getArg(1));
          break;

        default:
          throw new IllegalArgumentException("Illegal join expression " + join);
      }
    }

    // filter
    if (filter != null) {
      ser.append("try {\n");
      ser.append("if (");
      ser.handle(filter).append(") {\n");
      for (String matcher : anyJoinMatchers) {
        ser.append("    " + matcher + " = true;\n");
      }
      ser.append("    rv.add(new Object[]{" + vars + "});\n");
      ser.append("}\n");
      ser.append("} catch (NullPointerException npe) { }\n");
    } else {
      ser.append("rv.add(new Object[]{" + vars + "});\n");
    }

    // closing context
    var amount = joins.size() + anyJoinMatchers.size();
    for (var i = 0; i < amount; i++) {
      ser.append("}\n");
    }
    ser.append("return rv;");

    var constants = getConstants(metadata, ser.getConstants(), ser.getConstantToLabel());

    var projectionType = new ClassType(TypeCategory.LIST, List.class, Types.OBJECTS);
    return factory.createEvaluator(
        ser.toString(),
        projectionType,
        sourceNames.toArray(new String[0]),
        sourceTypes.toArray(new Type[0]),
        sourceClasses.toArray(new Class<?>[0]),
        constants);
  }

  private Map<String, Object> getConstants(
      QueryMetadata metadata, List<Object> constants, Map<Object, String> constantToLabel) {
    var result = new LinkedHashMap<String, Object>();

    for (var constant : constants) {
      var paramName = constantToLabel.get(constant);

      if (constant instanceof ParamExpression<?>) {
        var value = metadata.getParams().get(constant);
        if (value == null) {
          throw new ParamNotSetException((ParamExpression<?>) constant);
        }

        result.put(paramName, value);
        continue;
      }

      result.put(paramName, constant);
    }

    return result;
  }
}
