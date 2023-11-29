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
package com.querydsl.apt.morphia;

import com.querydsl.apt.AbstractQuerydslProcessor;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.DefaultConfiguration;
import com.querydsl.core.annotations.QueryEntities;
import com.querydsl.core.annotations.QuerySupertype;
import com.querydsl.core.types.Expression;
import java.lang.annotation.Annotation;
import java.util.Collections;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

/**
 * Annotation processor to create Querydsl query types for Morphia annotated classes
 *
 * @author tiwe
 */
@SupportedAnnotationTypes({"com.querydsl.core.annotations.*", "org.mongodb.morphia.annotations.*"})
public class MorphiaAnnotationProcessor extends AbstractQuerydslProcessor {

  @Override
  protected Configuration createConfiguration(RoundEnvironment roundEnv) {
    Class<? extends Annotation> entities = QueryEntities.class;
    Class<? extends Annotation> entity = Entity.class;
    Class<? extends Annotation> superType = QuerySupertype.class;
    Class<? extends Annotation> embedded = Embedded.class;
    Class<? extends Annotation> skip = Transient.class;
    DefaultConfiguration conf =
        new DefaultConfiguration(
            processingEnv,
            roundEnv,
            Collections.<String>emptySet(),
            entities,
            entity,
            superType,
            null,
            embedded,
            skip);
    try {
      @SuppressWarnings("unchecked") // Point is an Expression<Double[]>
      Class<? extends Expression<Double[]>> cl =
          (Class<? extends Expression<Double[]>>) Class.forName("com.querydsl.mongodb.Point");
      conf.addCustomType(Double[].class, cl);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
    return conf;
  }
}
