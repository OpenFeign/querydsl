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
package com.querydsl.apt;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.model.Constructor;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryInit;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.annotations.QueryType;
import com.querydsl.core.util.Annotations;
import com.querydsl.core.util.BeanUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * {@code TypeElementHandler} is an APT visitor for entity types
 *
 * @author tiwe
 */
public class TypeElementHandler {

  private final TypeMappings typeMappings;

  private final QueryTypeFactory queryTypeFactory;

  private final Configuration configuration;

  private final ExtendedTypeFactory typeFactory;

  public TypeElementHandler(
      Configuration configuration,
      ExtendedTypeFactory typeFactory,
      TypeMappings typeMappings,
      QueryTypeFactory queryTypeFactory) {
    this.configuration = configuration;
    this.typeFactory = typeFactory;
    this.typeMappings = typeMappings;
    this.queryTypeFactory = queryTypeFactory;
  }

  public EntityType handleEntityType(TypeElement element, Messager messager) {
    EntityType entityType = typeFactory.getEntityType(element.asType(), true);
    List<? extends Element> elements = element.getEnclosedElements();
    var config = configuration.getConfig(element, elements);
    Set<String> blockedProperties = new HashSet<>();
    Map<String, TypeMirror> propertyTypes = new HashMap<>();
    Map<String, TypeMirror> fixedTypes = new HashMap<>();
    Map<String, Annotations> propertyAnnotations = new HashMap<>();

    // constructors
    if (config.visitConstructors()) {
      handleConstructors(entityType, elements, true, messager);
    }

    // fields
    if (config.visitFieldProperties()) {
      for (VariableElement field : ElementFilter.fieldsIn(elements)) {
        var name = field.getSimpleName().toString();
        if (configuration.isBlockedField(field)) {
          blockedProperties.add(name);
        } else if (configuration.isValidField(field)) {
          var annotations = new Annotations();
          configuration.inspect(field, annotations);
          annotations.addAnnotation(field.getAnnotation(QueryType.class));
          annotations.addAnnotation(field.getAnnotation(QueryInit.class));
          propertyAnnotations.put(name, annotations);
          propertyTypes.put(name, field.asType());
          var fixedType = configuration.getRealType(field);
          if (fixedType != null) {
            fixedTypes.put(name, fixedType);
          }
        }
      }
    }

    // methods
    if (config.visitMethodProperties()) {
      for (ExecutableElement method : ElementFilter.methodsIn(elements)) {
        var name = method.getSimpleName().toString();
        if (name.startsWith("get") && name.length() > 3 && method.getParameters().isEmpty()) {
          name = BeanUtils.uncapitalize(name.substring(3));
        } else if (name.startsWith("is") && name.length() > 2 && method.getParameters().isEmpty()) {
          name = BeanUtils.uncapitalize(name.substring(2));
        } else {
          continue;
        }

        if (configuration.isBlockedGetter(method)) {
          blockedProperties.add(name);
        } else if (configuration.isValidGetter(method) && !blockedProperties.contains(name)) {
          var annotations = propertyAnnotations.get(name);
          if (annotations == null) {
            annotations = new Annotations();
            propertyAnnotations.put(name, annotations);
          }
          configuration.inspect(method, annotations);
          annotations.addAnnotation(method.getAnnotation(QueryType.class));
          annotations.addAnnotation(method.getAnnotation(QueryInit.class));
          propertyTypes.put(name, method.getReturnType());
          var fixedType = configuration.getRealType(method);
          if (fixedType != null) {
            fixedTypes.put(name, fixedType);
          }
        }
      }
    }

    // fixed types override property types
    propertyTypes.putAll(fixedTypes);
    for (Map.Entry<String, Annotations> entry : propertyAnnotations.entrySet()) {
      var property =
          toProperty(
              entityType, entry.getKey(), propertyTypes.get(entry.getKey()), entry.getValue());
      if (property != null) {
        entityType.addProperty(property);
      }
    }

    return entityType;
  }

  private Property toProperty(
      EntityType entityType, String name, TypeMirror type, Annotations annotations) {
    // type
    Type propertyType = typeFactory.getType(type, true);
    if (annotations.isAnnotationPresent(QueryType.class)) {
      var propertyTypeAnn = annotations.getAnnotation(QueryType.class).value();
      if (propertyTypeAnn != PropertyType.NONE) {
        var typeCategory =
            TypeCategory.valueOf(annotations.getAnnotation(QueryType.class).value().name());
        if (typeCategory == null) {
          return null;
        }
        propertyType = propertyType.as(typeCategory);
      } else {
        return null;
      }
    }

    // inits
    List<String> inits = Collections.emptyList();
    if (annotations.isAnnotationPresent(QueryInit.class)) {
      inits = Arrays.asList(annotations.getAnnotation(QueryInit.class).value());
    }

    return new Property(entityType, name, propertyType, inits);
  }

  public EntityType handleProjectionType(
      TypeElement e, boolean onlyAnnotatedConstructors, Messager messager) {
    Type c = typeFactory.getType(e.asType(), true);
    var entityType =
        new EntityType(c.as(TypeCategory.ENTITY), configuration.getVariableNameFunction());
    typeMappings.register(entityType, queryTypeFactory.create(entityType));
    List<? extends Element> elements = e.getEnclosedElements();
    handleConstructors(entityType, elements, onlyAnnotatedConstructors, messager);
    return entityType;
  }

  private Type getType(VariableElement element) {
    Type rv = typeFactory.getType(element.asType(), true);
    if (element.getAnnotation(QueryType.class) != null) {
      var qt = element.getAnnotation(QueryType.class);
      if (qt.value() != PropertyType.NONE) {
        var typeCategory = TypeCategory.valueOf(qt.value().name());
        rv = rv.as(typeCategory);
      }
    }
    return rv;
  }

  private void handleConstructors(
      EntityType entityType,
      List<? extends Element> elements,
      boolean onlyAnnotatedConstructors,
      Messager messager) {
    var builderNameSet = new HashSet<String>();
    for (ExecutableElement constructor : ElementFilter.constructorsIn(elements)) {
      if (configuration.isValidConstructor(constructor, onlyAnnotatedConstructors)) {
        var parameters = transformParams(constructor.getParameters());
        QueryProjection projection = constructor.getAnnotation(QueryProjection.class);
        if (projection != null
            && projection.useBuilder()
            && projection.builderName().trim().isEmpty()) {
          messager.printMessage(
              Diagnostic.Kind.ERROR,
              "@QueryProjection with builder=true requires a non-empty builderName",
              constructor);
          return;
        }

        Constructor constructorModel = new Constructor(parameters);
        constructorModel.setUseBuilder(projection != null && projection.useBuilder());
        constructorModel.setBuilderName(projection != null ? projection.builderName() : "");

        if (builderNameSet.contains(constructorModel.getBuilderName())) {
          messager.printMessage(
              Diagnostic.Kind.ERROR,
              "Duplicate builderName found: " + constructorModel.getBuilderName(),
              constructor);
          return;
        }

        builderNameSet.add(constructorModel.getBuilderName());
        entityType.addConstructor(constructorModel);
      }
    }
  }

  public List<Parameter> transformParams(List<? extends VariableElement> params) {
    List<Parameter> parameters = new ArrayList<>(params.size());
    for (VariableElement param : params) {
      var paramType = getType(param);
      parameters.add(new Parameter(param.getSimpleName().toString(), paramType));
    }
    return parameters;
  }
}
