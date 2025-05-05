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
package com.querydsl.mongodb.morphia;

import com.mongodb.DBRef;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.mongodb.MongodbSerializer;
import dev.morphia.Datastore;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.Mapper;

/**
 * {@code MorphiaSerializer} extends {@link MongodbSerializer} with Morphia specific annotation
 * handling
 *
 * @author tiwe
 */
public class MorphiaSerializer extends MongodbSerializer {

  private final Datastore morphia;

  public MorphiaSerializer(Datastore morphia) {
    this.morphia = morphia;
  }

  @Override
  public Object visit(Constant<?> expr, Void context) {
    var value = super.visit(expr, context);

    // Skip Morphia mapping for primitives
    if (!morphia.getMapper().isMappable(value.getClass())) {
      return value;
    }

    return morphia.getMapper().toDocument(value);
  }

  @Override
  protected String getKeyForPath(Path<?> expr, PathMetadata metadata) {
    var annotations = expr.getAnnotatedElement();
    if (annotations.isAnnotationPresent(Id.class)) {
      Path<?> parent = expr.getMetadata().getParent();
      if (parent.getAnnotatedElement().isAnnotationPresent(Reference.class)) {
        return null; // go to parent
      } else {
        return "_id";
      }
    } else if (annotations.isAnnotationPresent(Property.class)) {
      var property = annotations.getAnnotation(Property.class);
      if (!property.value().equals(Mapper.IGNORED_FIELDNAME)) {
        return property.value();
      }
    } else if (annotations.isAnnotationPresent(Reference.class)) {
      var reference = annotations.getAnnotation(Reference.class);
      if (!reference.value().equals(Mapper.IGNORED_FIELDNAME)) {
        return reference.value();
      }
    }
    return super.getKeyForPath(expr, metadata);
  }

  @Override
  protected boolean isReference(Path<?> arg) {
    return arg.getAnnotatedElement().isAnnotationPresent(Reference.class);
  }

  @Override
  protected boolean isImplicitObjectIdConversion() {
    // see https://github.com/mongodb/morphia/wiki/FrequentlyAskedQuestions
    return false;
  }

  @Override
  protected boolean isId(Path<?> arg) {
    return arg.getAnnotatedElement().isAnnotationPresent(Id.class);
  }

  @Override
  protected DBRef asReference(Object entity) {
    Object key = morphia.getMapper().getId(entity);
    return new DBRef(
        morphia.getMapper().getEntityModel(entity.getClass()).getCollectionName(), key);
  }

  @Override
  protected DBRef asReferenceKey(Class<?> entity, Object id) {
    var collection = morphia.getMapper().getEntityModel(entity).getCollectionName();
    return new DBRef(collection, id);
  }
}
