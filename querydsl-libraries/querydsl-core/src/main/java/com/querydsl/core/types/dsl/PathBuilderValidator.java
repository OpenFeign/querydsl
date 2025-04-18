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
package com.querydsl.core.types.dsl;

import com.querydsl.core.util.BeanUtils;
import com.querydsl.core.util.PrimitiveUtils;
import com.querydsl.core.util.ReflectionUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/** {@code PathBuilderValidator} validates {@link PathBuilder} properties at creation time */
public interface PathBuilderValidator extends Serializable {

  /**
   * Validates the given property of given class
   *
   * @param parent type of the parent object
   * @param property property name
   * @param propertyType property type
   * @return propertyType or subtype of it
   */
  Class<?> validate(Class<?> parent, String property, Class<?> propertyType);

  PathBuilderValidator DEFAULT =
      new PathBuilderValidator() {

        private Pattern SPACES = Pattern.compile("\\s");

        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
          if (SPACES.matcher(property).find()) {
            throw new IllegalStateException("Unsafe due to CVE-2024-49203");
          }
          return propertyType;
        }
      };

  PathBuilderValidator FIELDS =
      new PathBuilderValidator() {
        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
          while (!parent.equals(Object.class)) {
            try {
              var field = parent.getDeclaredField(property);
              if (Map.class.isAssignableFrom(field.getType())) {
                return ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 1);
              } else if (Collection.class.isAssignableFrom(field.getType())) {
                return ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 0);
              } else {
                return PrimitiveUtils.wrap(field.getType());
              }
            } catch (NoSuchFieldException e) {
              parent = parent.getSuperclass();
            }
          }
          return null;
        }
      };

  PathBuilderValidator PROPERTIES =
      new PathBuilderValidator() {
        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
          var getter = BeanUtils.getAccessor("get", property, parent);
          if (getter == null && PrimitiveUtils.wrap(propertyType).equals(Boolean.class)) {
            getter = BeanUtils.getAccessor("is", property, parent);
          }
          if (getter != null) {
            if (Map.class.isAssignableFrom(getter.getReturnType())) {
              return ReflectionUtils.getTypeParameterAsClass(getter.getGenericReturnType(), 1);
            } else if (Collection.class.isAssignableFrom(getter.getReturnType())) {
              return ReflectionUtils.getTypeParameterAsClass(getter.getGenericReturnType(), 0);
            } else {
              return PrimitiveUtils.wrap(getter.getReturnType());
            }
          } else {
            return null;
          }
        }
      };
}
