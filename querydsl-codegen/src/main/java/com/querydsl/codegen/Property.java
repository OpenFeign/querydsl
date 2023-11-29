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

import com.querydsl.codegen.utils.model.Type;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.lang.model.SourceVersion;

/**
 * {@code Property} represents a property in a query domain type.
 *
 * @author tiwe
 */
public final class Property implements Comparable<Property> {

  private final EntityType declaringType;

  private final boolean inherited;

  private final List<String> inits;

  private final String name, escapedName;

  private final Map<Class<?>, Annotation> annotations = new HashMap<Class<?>, Annotation>();

  private final Map<Object, Object> data = new HashMap<Object, Object>();

  private final Type type;

  public Property(EntityType declaringType, String name, Type type) {
    this(declaringType, name, type, Collections.<String>emptyList(), false);
  }

  public Property(EntityType declaringType, String name, Type type, List<String> inits) {
    this(declaringType, name, type, inits, false);
  }

  public Property(
      EntityType declaringType, String name, Type type, List<String> inits, boolean inherited) {
    this(declaringType, name, escapeName(name), type, inits, inherited);
  }

  public Property(
      EntityType declaringType,
      String name,
      String escapedName,
      Type type,
      List<String> inits,
      boolean inherited) {
    this.declaringType = declaringType;
    this.name = name;
    this.escapedName = escapedName;
    this.type = type;
    this.inits = inits;
    this.inherited = inherited;
  }

  private static String escapeName(String name) {
    if (SourceVersion.isKeyword(name)) {
      name = name + "$";
    } else if (!Character.isJavaIdentifierStart(name.charAt(0))) {
      name = "_" + name;
    }
    return name;
  }

  public void addAnnotation(Annotation annotation) {
    annotations.put(annotation.annotationType(), annotation);
  }

  @Override
  public int compareTo(Property o) {
    int rv = name.compareToIgnoreCase(o.getName());
    if (rv == 0) {
      return name.compareTo(o.getName());
    } else {
      return rv;
    }
  }

  public Property createCopy(EntityType targetModel) {
    if (!declaringType.getParameters().isEmpty()) {
      Type newType = TypeResolver.resolve(type, declaringType, targetModel);
      if (!newType.equals(type) || !newType.getClass().equals(type.getClass())) {
        return new Property(targetModel, name, newType, inits, false);
      } else {
        return new Property(targetModel, name, type, inits, targetModel.getSuperType() != null);
      }
    } else {
      return new Property(targetModel, name, type, inits, targetModel.getSuperType() != null);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnotation(Class<T> type) {
    return (T) annotations.get(type);
  }

  public Collection<Annotation> getAnnotations() {
    return Collections.unmodifiableCollection(annotations.values());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof Property) {
      Property p = (Property) o;
      return p.name.equals(name) && p.type.equals(type);
    } else {
      return false;
    }
  }

  public EntityType getDeclaringType() {
    return declaringType;
  }

  public String getEscapedName() {
    return escapedName;
  }

  public List<String> getInits() {
    return inits;
  }

  public String getName() {
    return name;
  }

  public Type getParameter(int i) {
    return type.getParameters().get(i);
  }

  public Map<Object, Object> getData() {
    return data;
  }

  public Type getType() {
    return type;
  }

  public boolean isInherited() {
    return inherited;
  }

  @Override
  public String toString() {
    return declaringType.getFullName() + "." + name;
  }
}
