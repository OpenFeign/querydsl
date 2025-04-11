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
package com.querydsl.codegen;

import static com.querydsl.codegen.utils.StringUtils.capitalize;
import static com.querydsl.codegen.utils.Symbols.*;

import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.codegen.utils.model.*;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code ProjectionSerializer} is a {@link Serializer} implementation for projection types
 *
 * @author tiwe
 */
public class DefaultProjectionSerializer implements ProjectionSerializer {

  private final Class<? extends Annotation> generatedAnnotationClass;
  private final TypeMappings typeMappings;

  /**
   * Create a new {@code ProjectionSerializer} instance
   *
   * @param typeMappings type mappings to be used
   */
  public DefaultProjectionSerializer(TypeMappings typeMappings) {
    this(typeMappings, GeneratedAnnotationResolver.resolveDefault());
  }

  /**
   * Create a new {@code ProjectionSerializer} instance
   *
   * @param typeMappings type mappings to be used
   * @param generatedAnnotationClass the fully qualified class name of the <em>Single-Element
   *     Annotation</em> (with {@code String} element) to be used on the generated classes.
   * @see <a
   *     href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.3">Single-Element
   *     Annotation</a>
   */
  @Inject
  public DefaultProjectionSerializer(
      TypeMappings typeMappings,
      @Named(CodegenModule.GENERATED_ANNOTATION_CLASS)
          Class<? extends Annotation> generatedAnnotationClass) {
    this.typeMappings = typeMappings;
    this.generatedAnnotationClass = generatedAnnotationClass;
  }

  private Parameter getExpressionParameter(EntityType model, boolean asExpr, Parameter p) {
    var type =
        !asExpr
            ? typeMappings.getExprType(p.getType(), model, false, false, true)
            : new ClassType(
                Expression.class,
                p.getType().isFinal() ? p.getType() : new TypeExtends(p.getType()));
    return new Parameter(p.getName(), type);
  }

  protected void intro(EntityType model, CodeWriter writer) throws IOException {
    var simpleName = model.getSimpleName();
    var queryType = typeMappings.getPathType(model, model, false);

    // package
    if (!queryType.getPackageName().isEmpty()) {
      writer.packageDecl(queryType.getPackageName());
    }

    // imports
    imports(model, writer);

    Set<Integer> sizes = new HashSet<>();
    var constructors = new ArrayList<>(model.getConstructors());
    Collections.sort(constructors);
    for (Constructor c : constructors) {
      sizes.add(c.getParameters().size());
    }
    if (sizes.size() != constructors.size()) {
      writer.imports(Expression.class);
    }

    // javadoc
    writer.javadoc(queryType + " is a Querydsl Projection type for " + simpleName);

    writer.suppressWarnings(THIS_ESCAPE);
    writer.line("@", generatedAnnotationClass.getSimpleName(), "(\"", getClass().getName(), "\")");

    // class header
    //        writer.suppressWarnings("serial");
    Type superType = new ClassType(TypeCategory.SIMPLE, ConstructorExpression.class, model);
    writer.beginClass(queryType, superType);
    writer.privateStaticFinal(Types.LONG_P, "serialVersionUID", model.hashCode() + "L");
  }

  protected void imports(EntityType model, CodeWriter writer) throws IOException {
    writer.imports(NumberExpression.class.getPackage());
    writer.imports(ConstructorExpression.class, generatedAnnotationClass);
  }

  protected void outro(EntityType model, CodeWriter writer) throws IOException {
    writer.end();
  }

  @Override
  public void serialize(
      final EntityType model, SerializerConfig serializerConfig, CodeWriter writer)
      throws IOException {
    // intro
    intro(model, writer);

    var localName = writer.getRawName(model);
    Set<Integer> sizes = new HashSet<>();

    var queryType = typeMappings.getPathType(model, model, false);

    var constructors = new ArrayList<>(model.getConstructors());
    Collections.sort(constructors);
    for (Constructor c : constructors) {
      var parameters = new ArrayList<>(c.getParameters());
      final var asExpr = sizes.add(parameters.size());
      // begin
      writer.beginConstructor(
          parameters, parameter -> getExpressionParameter(model, asExpr, parameter));

      // body
      writer.beginLine(SUPER, "(" + writer.getClassConstant(localName));
      // TODO: Fix for Scala (Array[Class])
      writer.append(", new Class<?>[]{");
      for (int i = 0; i < parameters.size(); i++) {
        if (i != 0) {
          writer.append(", ");
        }
        Parameter p = parameters.get(i);
        if (Types.PRIMITIVES.containsKey(p.getType())) {
          var primitive = Types.PRIMITIVES.get(p.getType());
          writer.append(writer.getClassConstant(primitive.getFullName()));
        } else {
          writer.append(writer.getClassConstant(writer.getRawName(p.getType())));
        }
      }
      writer.append("}");

      for (Parameter p : c.getParameters()) {
        writer.append(", ");
        parameter(writer, p);
      }

      // end
      writer.append(");\n");
      writer.end();
    }

    sizes = new HashSet<Integer>();
    for (Constructor c : model.getConstructors()) {
      appendInnerStaticBuilderClass(model, writer, queryType, c, sizes);
      appendStaticBuilderFactoryMethod(writer, c);
    }

    // outro
    outro(model, writer);
  }

  protected void parameterType(CodeWriter writer, Parameter p) throws IOException {
    if (Types.PRIMITIVES.containsKey(p.getType())) {
      var primitive = Types.PRIMITIVES.get(p.getType());
      writer.append(writer.getClassConstant(primitive.getFullName()));
    } else {
      writer.append(writer.getClassConstant(writer.getRawName(p.getType())));
    }
  }

  protected void parameter(CodeWriter writer, Parameter p) throws IOException {
    writer.append(p.getName());
  }

  private void appendInnerStaticBuilderClass(
      EntityType model,
      CodeWriter writer,
      Type queryType,
      Constructor constructor,
      Set<Integer> sizes)
      throws IOException {
    if (isBuilderDisabled(constructor)) return;

    var builderName = constructor.getBuilderName();
    var parameters = new ArrayList<>(constructor.getParameters());
    final var asExpr = sizes.add(parameters.size());

    var builderClassName = capitalize(builderName) + "Builder";

    var builderType = new SimpleType(builderClassName);

    writer.beginInnerStaticClass(builderType);
    for (var param : parameters) {
      var expressionParameter = getExpressionParameter(model, asExpr, param);
      writer.privateField(expressionParameter.getType(), expressionParameter.getName());
    }

    for (var param : parameters) {
      var expressionParameter = getExpressionParameter(model, asExpr, param);
      appendPublicSetterForBuilder(writer, expressionParameter, builderType);
    }

    appendPublicBuildMethodForBuilder(writer, queryType, parameters);

    writer.end();
  }

  private void appendPublicSetterForBuilder(
      CodeWriter writer, Parameter expressionParameter, Type builderType) throws IOException {
    writer.beginPublicMethod(
        builderType, "set" + capitalize(expressionParameter.getName()), expressionParameter);
    writer.line(
        THIS, DOT, expressionParameter.getName(), ASSIGN, expressionParameter.getName(), SEMICOLON);
    writer.line(RETURN, THIS, SEMICOLON);
    writer.end();
  }

  private void appendPublicBuildMethodForBuilder(
      CodeWriter writer, Type queryType, ArrayList<Parameter> parameters) throws IOException {
    writer.beginPublicMethod(queryType, "build");
    writer.beginLine(RETURN, NEW, queryType.getSimpleName(), "(");
    for (int i = 0; i < parameters.size(); i++) {
      if (i != 0) {
        writer.append(", ");
      }
      writer.append(parameters.get(i).getName());
      if (i == parameters.size() - 1) {
        writer.append(")").append(SEMICOLON).append(NEWLINE);
      }
    }
    writer.end();
  }

  private void appendStaticBuilderFactoryMethod(CodeWriter writer, Constructor constructor)
      throws IOException {
    if (isBuilderDisabled(constructor)) return;

    var builderName = constructor.getBuilderName();
    var builderClassName = capitalize(builderName) + "Builder";
    var factoryMethodName = "builder" + capitalize(builderName);

    var builderType = new SimpleType(builderClassName);

    writer.beginStaticMethod(builderType, factoryMethodName);
    writer.line(RETURN, NEW, builderType.getSimpleName(), "()", SEMICOLON);
    writer.end();
  }

  private boolean isBuilderDisabled(Constructor constructor) {
    if (!constructor.useBuilder()) return true;
    return constructor.getBuilderName() == null || constructor.getBuilderName().trim().isEmpty();
  }
}
