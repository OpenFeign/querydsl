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
package fluentq.apt;

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntities;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import fluentq.core.annotations.QueryTransient;
import java.lang.annotation.Annotation;
import java.util.Collections;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

/**
 * Default annotation processor for FluentQ which handles {@link QueryEntity}, {@link
 * QuerySupertype}, {@link QueryEmbeddable}, {@link QueryEmbedded} and {@link QueryTransient}
 *
 * @author tiwe
 */
@SupportedAnnotationTypes({"fluentq.core.annotations.*"})
public class FluentQAnnotationProcessor extends AbstractFluentQProcessor {

  @Override
  protected Configuration createConfiguration(RoundEnvironment roundEnv) {
    Class<? extends Annotation> entities = QueryEntities.class;
    Class<? extends Annotation> entity = QueryEntity.class;
    Class<? extends Annotation> superType = QuerySupertype.class;
    Class<? extends Annotation> embeddable = QueryEmbeddable.class;
    Class<? extends Annotation> embedded = QueryEmbedded.class;
    Class<? extends Annotation> skip = QueryTransient.class;

    return new DefaultConfiguration(
        processingEnv,
        roundEnv,
        Collections.<String>emptySet(),
        entities,
        entity,
        superType,
        embeddable,
        embedded,
        skip);
  }
}
