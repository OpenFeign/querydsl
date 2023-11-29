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
package com.querydsl.jpa.codegen;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.SerializerConfig;
import com.querydsl.codegen.SimpleSerializerConfig;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import jakarta.persistence.Temporal;
import jakarta.persistence.metamodel.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.hibernate.MappingException;

/**
 * {@code JPADomainExporter} exports JPA 2 metamodels to Querydsl expression types
 *
 * @author tiwe
 */
public class JPADomainExporter extends AbstractDomainExporter {

  private final Metamodel metamodel;

  /**
   * Create a new JPADomainExporter instance
   *
   * @param targetFolder target folder
   * @param metamodel metamodel
   */
  public JPADomainExporter(File targetFolder, Metamodel metamodel) {
    this(
        "Q", "", targetFolder, SimpleSerializerConfig.DEFAULT, metamodel, Charset.defaultCharset());
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param targetFolder target folder
   * @param metamodel metamodel
   */
  public JPADomainExporter(String namePrefix, File targetFolder, Metamodel metamodel) {
    this(
        namePrefix,
        "",
        targetFolder,
        SimpleSerializerConfig.DEFAULT,
        metamodel,
        Charset.defaultCharset());
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param targetFolder target folder
   * @param metamodel metamodel
   * @param charset charset (default: system charset)
   */
  public JPADomainExporter(
      String namePrefix, File targetFolder, Metamodel metamodel, Charset charset) {
    this(namePrefix, "", targetFolder, SimpleSerializerConfig.DEFAULT, metamodel, charset);
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param nameSuffix name suffix
   * @param targetFolder target folder
   * @param metamodel metamodel
   */
  public JPADomainExporter(
      String namePrefix, String nameSuffix, File targetFolder, Metamodel metamodel) {
    this(
        namePrefix,
        nameSuffix,
        targetFolder,
        SimpleSerializerConfig.DEFAULT,
        metamodel,
        Charset.defaultCharset());
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param targetFolder target folder
   * @param serializerConfig serializer config
   * @param metamodel metamodel
   */
  public JPADomainExporter(
      String namePrefix,
      File targetFolder,
      SerializerConfig serializerConfig,
      Metamodel metamodel) {
    this(namePrefix, "", targetFolder, serializerConfig, metamodel, Charset.defaultCharset());
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param targetFolder target folder
   * @param serializerConfig serializer config
   * @param metamodel metamodel
   * @param charset charset (default: system charset)
   */
  public JPADomainExporter(
      String namePrefix,
      File targetFolder,
      SerializerConfig serializerConfig,
      Metamodel metamodel,
      Charset charset) {
    this(namePrefix, "", targetFolder, serializerConfig, metamodel, charset);
  }

  /**
   * Create a new JPADomainExporter instance
   *
   * @param namePrefix name prefix (default: Q)
   * @param nameSuffix name suffix (default: empty)
   * @param targetFolder target folder
   * @param serializerConfig serializer config
   * @param metamodel metamodel
   * @param charset charset (default: system charset)
   */
  public JPADomainExporter(
      String namePrefix,
      String nameSuffix,
      File targetFolder,
      SerializerConfig serializerConfig,
      Metamodel metamodel,
      Charset charset) {
    super(namePrefix, nameSuffix, targetFolder, serializerConfig, charset);
    this.metamodel = metamodel;
  }

  @Override
  protected void collectTypes()
      throws IOException, XMLStreamException, ClassNotFoundException, NoSuchMethodException {

    Map<ManagedType<?>, EntityType> types = new HashMap<>();
    for (ManagedType<?> managedType : metamodel.getManagedTypes()) {
      if (managedType instanceof MappedSuperclassType) {
        types.put(managedType, createSuperType(managedType.getJavaType()));
      } else if (managedType instanceof jakarta.persistence.metamodel.EntityType) {
        types.put(managedType, createEntityType(managedType.getJavaType()));
      } else if (managedType instanceof EmbeddableType) {
        types.put(managedType, createEmbeddableType(managedType.getJavaType()));
      } else {
        throw new IllegalArgumentException("Unknown type " + managedType);
      }
    }

    // handle properties
    for (Map.Entry<ManagedType<?>, EntityType> entry : types.entrySet()) {
      EntityType entityType = entry.getValue();
      for (Attribute<?, ?> attribute : entry.getKey().getDeclaredAttributes()) {
        handleProperty(entityType, entityType.getJavaClass(), attribute);
      }
    }
  }

  private void handleProperty(EntityType entityType, Class<?> cl, Attribute<?, ?> p)
      throws NoSuchMethodException, ClassNotFoundException {
    Class<?> clazz = Object.class;
    try {
      clazz = p.getJavaType();
    } catch (MappingException e) {
      // ignore
    }
    Type propertyType = getType(cl, clazz, p.getName());

    AnnotatedElement annotated = getAnnotatedElement(cl, p.getName());
    propertyType = getTypeOverride(propertyType, annotated);
    if (propertyType == null) {
      return;
    }

    if (p.isCollection()) {
      if (p instanceof MapAttribute) {
        MapAttribute<?, ?, ?> map = (MapAttribute<?, ?, ?>) p;
        Type keyType = typeFactory.get(map.getKeyJavaType());
        Type valueType = typeFactory.get(map.getElementType().getJavaType());
        valueType = getPropertyType(p, valueType);
        propertyType =
            new SimpleType(
                propertyType,
                normalize(propertyType.getParameters().get(0), keyType),
                normalize(propertyType.getParameters().get(1), valueType));
      } else {
        Type valueType =
            typeFactory.get(((PluralAttribute<?, ?, ?>) p).getElementType().getJavaType());
        valueType = getPropertyType(p, valueType);
        propertyType =
            new SimpleType(propertyType, normalize(propertyType.getParameters().get(0), valueType));
      }
    } else {
      propertyType = getPropertyType(p, propertyType);
    }

    Property property = createProperty(entityType, p.getName(), propertyType, annotated);
    entityType.addProperty(property);
  }

  private Type getPropertyType(Attribute<?, ?> p, Type propertyType) {
    Temporal temporal = ((AnnotatedElement) p.getJavaMember()).getAnnotation(Temporal.class);
    if (temporal != null) {
      switch (temporal.value()) {
        case DATE:
          propertyType = propertyType.as(TypeCategory.DATE);
          break;
        case TIME:
          propertyType = propertyType.as(TypeCategory.TIME);
          break;
        case TIMESTAMP:
          propertyType = propertyType.as(TypeCategory.DATETIME);
          break;
      }
    }
    return propertyType;
  }
}
