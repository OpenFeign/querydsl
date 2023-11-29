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
package com.querydsl.collections;

import com.querydsl.codegen.utils.ECJEvaluatorFactory;
import com.querydsl.codegen.utils.Evaluator;
import com.querydsl.codegen.utils.EvaluatorFactory;
import com.querydsl.codegen.utils.JDKEvaluatorFactory;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.codegen.utils.support.ClassUtils;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.CollectionAnyVisitor;
import com.querydsl.core.support.Context;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.util.PrimitiveUtils;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
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
    final JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
    if (classLoader instanceof URLClassLoader && systemJavaCompiler != null) {
      this.factory = new JDKEvaluatorFactory((URLClassLoader) classLoader, systemJavaCompiler);
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
    final CollQuerySerializer serializer = new CollQuerySerializer(templates);
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

    Map<Object, String> constantToLabel = serializer.getConstantToLabel();
    Map<String, Object> constants = getConstants(metadata, constantToLabel);
    Class<?>[] types = new Class<?>[sources.size()];
    String[] names = new String[sources.size()];
    for (int i = 0; i < sources.size(); i++) {
      types[i] = sources.get(i).getType();
      names[i] = sources.get(i).toString();
    }

    // normalize types
    for (int i = 0; i < types.length; i++) {
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
    String typeName = ClassUtils.getName(source.getType());
    CollQuerySerializer ser = new CollQuerySerializer(templates);
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

    Map<Object, String> constantToLabel = ser.getConstantToLabel();
    Map<String, Object> constants = getConstants(metadata, constantToLabel);

    Type sourceType = new ClassType(TypeCategory.SIMPLE, source.getType());
    ClassType sourceListType = new ClassType(TypeCategory.SIMPLE, Iterable.class, sourceType);

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
    List<String> sourceNames = new ArrayList<String>();
    List<Type> sourceTypes = new ArrayList<Type>();
    List<Class<?>> sourceClasses = new ArrayList<Class<?>>();
    StringBuilder vars = new StringBuilder();
    CollQuerySerializer ser = new CollQuerySerializer(templates);
    ser.append("java.util.List<Object[]> rv = new java.util.ArrayList<Object[]>();\n");

    List<String> anyJoinMatchers = new ArrayList<String>();

    // creating context
    for (JoinExpression join : joins) {
      Expression<?> target = join.getTarget();
      String typeName = com.querydsl.codegen.utils.support.ClassUtils.getName(target.getType());
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
          boolean colAnyJoin =
              join.getCondition() != null && join.getCondition().toString().equals("any");
          boolean leftJoin = join.getType() == JoinType.LEFTJOIN;
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
            Context context = new Context();
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
    int amount = joins.size() + anyJoinMatchers.size();
    for (int i = 0; i < amount; i++) {
      ser.append("}\n");
    }
    ser.append("return rv;");

    Map<Object, String> constantToLabel = ser.getConstantToLabel();
    Map<String, Object> constants = getConstants(metadata, constantToLabel);

    ClassType projectionType = new ClassType(TypeCategory.LIST, List.class, Types.OBJECTS);
    return factory.createEvaluator(
        ser.toString(),
        projectionType,
        sourceNames.toArray(new String[0]),
        sourceTypes.toArray(new Type[0]),
        sourceClasses.toArray(new Class<?>[0]),
        constants);
  }

  private Map<String, Object> getConstants(
      QueryMetadata metadata, Map<Object, String> constantToLabel) {
    Map<String, Object> constants = new HashMap<String, Object>();
    for (Map.Entry<Object, String> entry : constantToLabel.entrySet()) {
      if (entry.getKey() instanceof ParamExpression<?>) {
        Object value = metadata.getParams().get(entry.getKey());
        if (value == null) {
          throw new ParamNotSetException((ParamExpression<?>) entry.getKey());
        }
        constants.put(entry.getValue(), value);
      } else {
        constants.put(entry.getValue(), entry.getKey());
      }
    }
    return constants;
  }
}
