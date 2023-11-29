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
package com.querydsl.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Annotations is a merging adapter for the {@link AnnotatedElement} interface
 *
 * @author tiwe
 */
public class Annotations implements AnnotatedElement {

  private final Map<Class<? extends Annotation>, Annotation> annotations =
      new HashMap<Class<? extends Annotation>, Annotation>();

  public Annotations(AnnotatedElement... elements) {
    for (AnnotatedElement element : elements) {
      addAnnotations(element);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return (T) annotations.get(annotationClass);
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations.values().toArray(new Annotation[0]);
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return getAnnotations();
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return annotations.containsKey(annotationClass);
  }

  public void addAnnotation(@Nullable Annotation annotation) {
    if (annotation != null) {
      annotations.put(annotation.annotationType(), annotation);
    }
  }

  public void addAnnotations(AnnotatedElement element) {
    for (Annotation annotation : element.getAnnotations()) {
      annotations.put(annotation.annotationType(), annotation);
    }
  }
}
