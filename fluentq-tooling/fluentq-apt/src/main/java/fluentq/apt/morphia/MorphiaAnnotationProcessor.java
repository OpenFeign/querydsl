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
package fluentq.apt.morphia;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import fluentq.apt.AbstractFluentQProcessor;
import fluentq.apt.Configuration;
import fluentq.apt.DefaultConfiguration;
import fluentq.core.annotations.QueryEntities;
import fluentq.core.annotations.QuerySupertype;
import fluentq.core.types.Expression;
import java.lang.annotation.Annotation;
import java.util.Collections;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

/**
 * Annotation processor to create FluentQ query types for Morphia annotated classes
 *
 * @author tiwe
 */
@SupportedAnnotationTypes({"fluentq.core.annotations.*", "dev.morphia.annotations.*"})
public class MorphiaAnnotationProcessor extends AbstractFluentQProcessor {

  @Override
  protected Configuration createConfiguration(RoundEnvironment roundEnv) {
    Class<? extends Annotation> entities = QueryEntities.class;
    Class<? extends Annotation> entity = Entity.class;
    Class<? extends Annotation> superType = QuerySupertype.class;
    Class<? extends Annotation> embedded = Embedded.class;
    Class<? extends Annotation> skip = Transient.class;
    var conf =
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
          (Class<? extends Expression<Double[]>>) Class.forName("fluentq.mongodb.Point");
      conf.addCustomType(Double[].class, cl);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
    return conf;
  }
}
