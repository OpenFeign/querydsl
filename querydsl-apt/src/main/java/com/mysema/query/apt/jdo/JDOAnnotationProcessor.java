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
package com.mysema.query.apt.jdo;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import java.lang.annotation.Annotation;

import com.mysema.query.annotations.QueryEmbedded;
import com.mysema.query.annotations.QueryEntities;
import com.mysema.query.annotations.QuerySupertype;
import com.mysema.query.apt.AbstractQuerydslProcessor;
import com.mysema.query.apt.Configuration;

/**
 * AnnotationProcessor for JDO which takes {@link PersistenceCapable}, {@link EmbeddedOnly} and
 * {@link NotPersistent} into account
 *
 * @author tiwe
 *
 */
@SupportedAnnotationTypes({"com.mysema.query.annotations.*","javax.jdo.annotations.*"})
public class JDOAnnotationProcessor extends AbstractQuerydslProcessor {

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entities = QueryEntities.class;
        Class<? extends Annotation> entity = PersistenceCapable.class;
        Class<? extends Annotation> superType = QuerySupertype.class;
        Class<? extends Annotation> embeddable = EmbeddedOnly.class;
        Class<? extends Annotation> embedded = QueryEmbedded.class;
        Class<? extends Annotation> skip = NotPersistent.class;
        return new JDOConfiguration(roundEnv, processingEnv.getOptions(),
                entities, entity, superType, embeddable, embedded, skip);
    }
}