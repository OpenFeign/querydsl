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
import com.querydsl.codegen.Supertype;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.TypeExtends;
import com.querydsl.codegen.utils.model.TypeSuper;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.core.annotations.QueryExclude;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import org.jetbrains.annotations.Nullable;

/**
 * {@code ExtendedTypeFactory} is a factory for APT-based inspection {@link Type} creation
 *
 * @author tiwe
 */
public class ExtendedTypeFactory {

  private final Map<List<String>, Type> typeCache = new HashMap<>();

  private final Map<List<String>, EntityType> entityTypeCache = new HashMap<>();

  private final Type defaultType;

  private final Set<Class<? extends Annotation>> entityAnnotations;

  private final ProcessingEnvironment env;

  private final TypeMirror objectType,
      numberType,
      comparableType,
      collectionType,
      setType,
      listType,
      mapType;

  private final TypeMappings typeMappings;

  private final QueryTypeFactory queryTypeFactory;

  private boolean doubleIndexEntities = true;

  private Function<EntityType, String> variableNameFunction;

  private final TypeVisitor<Type, Boolean> visitor =
      new SimpleTypeVisitorAdapter<>() {

        @Override
        public Type visitPrimitive(PrimitiveType primitiveType, Boolean p) {
          return switch (primitiveType.getKind()) {
            case BOOLEAN -> Types.BOOLEAN;
            case BYTE -> Types.BYTE;
            case SHORT -> Types.SHORT;
            case INT -> Types.INTEGER;
            case LONG -> Types.LONG;
            case CHAR -> Types.CHARACTER;
            case FLOAT -> Types.FLOAT;
            case DOUBLE -> Types.DOUBLE;
            default -> null;
          };
        }

        private Type getPrimitive(PrimitiveType primitiveType) {
          return switch (primitiveType.getKind()) {
            case BOOLEAN -> Types.BOOLEAN_P;
            case BYTE -> Types.BYTE_P;
            case SHORT -> Types.SHORT_P;
            case INT -> Types.INT;
            case LONG -> Types.LONG_P;
            case CHAR -> Types.CHAR;
            case FLOAT -> Types.FLOAT_P;
            case DOUBLE -> Types.DOUBLE_P;
            default -> null;
          };
        }

        @Override
        public Type visitNull(NullType nullType, Boolean p) {
          throw new IllegalStateException();
        }

        @Override
        public Type visitArray(ArrayType arrayType, Boolean p) {
          if (arrayType.getComponentType() instanceof PrimitiveType) {
            var type = getPrimitive((PrimitiveType) arrayType.getComponentType());
            if (type != null) {
              return type.asArrayType();
            }
          }
          return visit(arrayType.getComponentType(), p).asArrayType();
        }

        @Override
        public Type visitDeclared(DeclaredType declaredType, Boolean p) {
          if (declaredType.asElement() instanceof TypeElement) {
            var typeElement = (TypeElement) declaredType.asElement();
            switch (typeElement.getKind()) {
              case ENUM:
                return createEnumType(declaredType, typeElement, p);
              case ANNOTATION_TYPE:
              case CLASS:
                return createClassType(declaredType, typeElement, p);
              case INTERFACE:
                return createInterfaceType(declaredType, typeElement, p);
              default:
                {
                  if (typeElement.getKind().name().equals("RECORD")) {
                    return createClassType(declaredType, typeElement, p);
                  }

                  throw new IllegalArgumentException("Illegal type " + typeElement);
                }
            }
          } else {
            throw new IllegalArgumentException(
                "Unsupported element type " + declaredType.asElement());
          }
        }

        @Override
        public Type visitError(ErrorType errorType, Boolean p) {
          return visitDeclared(errorType, p);
        }

        @Override
        public Type visitTypeVariable(TypeVariable typeVariable, Boolean p) {
          var varName = typeVariable.toString();
          if (typeVariable.getUpperBound() != null) {
            var type = visit(typeVariable.getUpperBound(), p);
            return new TypeExtends(varName, type);
          } else if (typeVariable.getLowerBound() != null
              && !(typeVariable.getLowerBound() instanceof NullType)) {
            var type = visit(typeVariable.getLowerBound(), p);
            return new TypeSuper(varName, type);
          } else {
            return null;
          }
        }

        @Override
        public Type visitWildcard(WildcardType wildcardType, Boolean p) {
          if (wildcardType.getExtendsBound() != null) {
            var type = visit(wildcardType.getExtendsBound(), p);
            return new TypeExtends(type);
          } else if (wildcardType.getSuperBound() != null) {
            var type = visit(wildcardType.getSuperBound(), p);
            return new TypeSuper(type);
          } else {
            return null;
          }
        }

        @Override
        public Type visitExecutable(ExecutableType t, Boolean p) {
          throw new IllegalStateException();
        }

        @Override
        public Type visitNoType(NoType t, Boolean p) {
          return defaultType;
        }
      };

  // TODO : return TypeMirror instead ?!?

  private final TypeVisitor<List<String>, Boolean> keyBuilder =
      new SimpleTypeVisitorAdapter<>() {

        private final List<String> defaultValue = Collections.singletonList("Object");

        private List<String> visitBase(TypeMirror t) {
          List<String> rv = new ArrayList<>();
          var name = t.toString();
          if (name.contains("<")) {
            name = name.substring(0, name.indexOf('<'));
          }
          rv.add(name);
          return rv;
        }

        @Override
        public List<String> visitPrimitive(PrimitiveType t, Boolean p) {
          return Collections.singletonList(t.toString());
        }

        @Override
        public List<String> visitNull(NullType t, Boolean p) {
          return defaultValue;
        }

        @Override
        public List<String> visitArray(ArrayType t, Boolean p) {
          List<String> rv = new ArrayList<>(visit(t.getComponentType()));
          rv.add("[]");
          return rv;
        }

        @Override
        public List<String> visitDeclared(DeclaredType t, Boolean p) {
          var rv = visitBase(t);
          for (TypeMirror arg : t.getTypeArguments()) {
            if (p) {
              rv.addAll(visit(arg, false));
            } else {
              rv.add(arg.toString());
            }
          }
          return rv;
        }

        @Override
        public List<String> visitError(ErrorType t, Boolean p) {
          return visitDeclared(t, p);
        }

        @Override
        public List<String> visitTypeVariable(TypeVariable t, Boolean p) {
          var rv = visitBase(t);
          if (t.getUpperBound() != null) {
            rv.addAll(visit(t.getUpperBound(), p));
          }
          if (t.getLowerBound() != null) {
            rv.addAll(visit(t.getLowerBound(), p));
          }
          return rv;
        }

        @Override
        public List<String> visitWildcard(WildcardType t, Boolean p) {
          var rv = visitBase(t);
          if (t.getExtendsBound() != null) {
            rv.addAll(visit(t.getExtendsBound(), p));
          }
          if (t.getSuperBound() != null) {
            rv.addAll(visit(t.getSuperBound(), p));
          }
          return rv;
        }

        @Override
        public List<String> visitExecutable(ExecutableType t, Boolean p) {
          throw new IllegalStateException();
        }

        @Override
        public List<String> visitNoType(NoType t, Boolean p) {
          return defaultValue;
        }
      };

  public ExtendedTypeFactory(
      ProcessingEnvironment env,
      Set<Class<? extends Annotation>> annotations,
      TypeMappings typeMappings,
      QueryTypeFactory queryTypeFactory,
      Function<EntityType, String> variableNameFunction) {
    this.env = env;
    this.defaultType = new TypeExtends(Types.OBJECT);
    this.entityAnnotations = annotations;
    this.objectType = getErasedType(Object.class);
    this.numberType = getErasedType(Number.class);
    this.comparableType = getErasedType(Comparable.class);
    this.collectionType = getErasedType(Collection.class);
    this.listType = getErasedType(List.class);
    this.setType = getErasedType(Set.class);
    this.mapType = getErasedType(Map.class);
    this.typeMappings = typeMappings;
    this.queryTypeFactory = queryTypeFactory;
    this.variableNameFunction = variableNameFunction;
  }

  private TypeMirror getErasedType(Class<?> clazz) {
    return env.getTypeUtils()
        .erasure(env.getElementUtils().getTypeElement(clazz.getName()).asType());
  }

  protected Type createType(
      TypeElement typeElement,
      TypeCategory category,
      List<? extends TypeMirror> typeArgs,
      boolean deep) {
    var name = typeElement.getQualifiedName().toString();
    var simpleName = typeElement.getSimpleName().toString();
    var packageName = env.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
    var params = new Type[typeArgs.size()];
    for (var i = 0; i < params.length; i++) {
      params[i] = getType(typeArgs.get(i), deep);
    }
    return new SimpleType(
        category,
        name,
        packageName,
        simpleName,
        false,
        typeElement.getModifiers().contains(Modifier.FINAL),
        params);
  }

  public Collection<EntityType> getEntityTypes() {
    return entityTypeCache.values();
  }

  @Nullable
  public Type getType(TypeMirror typeMirror, boolean deep) {
    var key = keyBuilder.visit(typeMirror, true);
    if (entityTypeCache.containsKey(key)) {
      return entityTypeCache.get(key);
    } else if (typeCache.containsKey(key)) {
      return typeCache.get(key);
    } else {
      return createType(typeMirror, key, deep);
    }
  }

  @Nullable
  private Type createType(TypeMirror typeMirror, List<String> key, boolean deep) {
    typeCache.put(key, null);
    var type = visitor.visit(typeMirror, deep);
    if (type != null
        && (type.getCategory() == TypeCategory.ENTITY
            || type.getCategory() == TypeCategory.CUSTOM)) {
      EntityType entityType = getEntityType(typeMirror, deep);
      typeCache.put(key, entityType);
      return entityType;
    } else {
      typeCache.put(key, type);
      return type;
    }
  }

  // TODO : simplify
  private Type createClassType(DeclaredType declaredType, TypeElement typeElement, boolean deep) {
    // other
    var name = typeElement.getQualifiedName().toString();

    if (name.startsWith("java.")) {
      Iterator<? extends TypeMirror> i = declaredType.getTypeArguments().iterator();

      if (isAssignable(declaredType, mapType)) {
        return createMapType(i, deep);

      } else if (isAssignable(declaredType, listType)) {
        return createCollectionType(Types.LIST, i, deep);

      } else if (isAssignable(declaredType, setType)) {
        return createCollectionType(Types.SET, i, deep);

      } else if (isAssignable(declaredType, collectionType)) {
        return createCollectionType(Types.COLLECTION, i, deep);
      }
    }

    var typeCategory = TypeCategory.get(name);

    if (typeCategory != TypeCategory.NUMERIC
        && isAssignable(typeElement.asType(), comparableType)
        && isSubType(typeElement.asType(), numberType)) {
      typeCategory = TypeCategory.NUMERIC;

    } else if (!typeCategory.isSubCategoryOf(TypeCategory.COMPARABLE)
        && isAssignable(typeElement.asType(), comparableType)) {
      typeCategory = TypeCategory.COMPARABLE;
    }

    for (Class<? extends Annotation> entityAnn : entityAnnotations) {
      if (isSimpleTypeEntity(typeElement, entityAnn)) {
        typeCategory = TypeCategory.ENTITY;
      }
    }

    List<? extends TypeMirror> arguments = declaredType.getTypeArguments();

    // for intersection types etc
    if (name.isEmpty()) {
      var type = objectType;
      if (typeCategory == TypeCategory.COMPARABLE) {
        type = comparableType;
      }
      // find most specific type of superTypes which is a subtype of type
      List<? extends TypeMirror> superTypes = env.getTypeUtils().directSupertypes(declaredType);
      for (TypeMirror superType : superTypes) {
        if (env.getTypeUtils().isSubtype(superType, type)) {
          type = superType;
        }
      }
      typeElement = (TypeElement) env.getTypeUtils().asElement(type);
      if (type instanceof DeclaredType declaredType1) {
        arguments = declaredType1.getTypeArguments();
      }
    }

    var type = createType(typeElement, typeCategory, arguments, deep);

    var superType = typeElement.getSuperclass();
    TypeElement superTypeElement = null;
    if (superType instanceof DeclaredType declaredType1) {
      superTypeElement = (TypeElement) declaredType1.asElement();
    }

    // entity type
    for (Class<? extends Annotation> entityAnn : entityAnnotations) {
      if (typeElement.getAnnotation(entityAnn) != null
          || (superTypeElement != null && superTypeElement.getAnnotation(entityAnn) != null)) {
        var entityType = new EntityType(type, variableNameFunction);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        return entityType;
      }
    }
    return type;
  }

  public boolean isSimpleTypeEntity(
      TypeElement typeElement, Class<? extends Annotation> entityAnn) {
    return typeElement.getAnnotation(entityAnn) != null;
  }

  protected Type createMapType(Iterator<? extends TypeMirror> typeMirrors, boolean deep) {
    if (!typeMirrors.hasNext()) {
      return new SimpleType(Types.MAP, defaultType, defaultType);
    }

    Type keyType = getType(typeMirrors.next(), deep);
    if (keyType == null) {
      keyType = defaultType;
    }

    Type valueType = getType(typeMirrors.next(), deep);
    if (valueType == null) {
      valueType = defaultType;
    } else if (valueType.getParameters().isEmpty()) {
      var element = env.getElementUtils().getTypeElement(valueType.getFullName());
      if (element != null) {
        Type type = getType(element.asType(), deep);
        if (!type.getParameters().isEmpty()) {
          valueType = new SimpleType(valueType, new Type[type.getParameters().size()]);
        }
      }
    }
    return new SimpleType(Types.MAP, keyType, valueType);
  }

  private Type createCollectionType(
      Type baseType, Iterator<? extends TypeMirror> typeMirrors, boolean deep) {
    if (!typeMirrors.hasNext()) {
      return new SimpleType(baseType, defaultType);
    }

    Type componentType = getType(typeMirrors.next(), deep);
    if (componentType == null) {
      componentType = defaultType;
    } else if (componentType.getParameters().isEmpty()) {
      var element = env.getElementUtils().getTypeElement(componentType.getFullName());
      if (element != null) {
        Type type = getType(element.asType(), deep);
        if (!type.getParameters().isEmpty()) {
          componentType = new SimpleType(componentType, new Type[type.getParameters().size()]);
        }
      }
    }
    return new SimpleType(baseType, componentType);
  }

  @Nullable
  public EntityType getEntityType(TypeMirror typeMirror, boolean deep) {
    var key = keyBuilder.visit(typeMirror, true);
    // get from cache
    if (entityTypeCache.containsKey(key)) {
      var entityType = entityTypeCache.get(key);
      if (deep && entityType.getSuperTypes().isEmpty()) {
        for (Type superType : getSupertypes(typeMirror, deep)) {
          entityType.addSupertype(new Supertype(superType));
        }
      }
      return entityType;

      // create
    } else {
      return createEntityType(typeMirror, key, deep);
    }
  }

  @Nullable
  private EntityType createEntityType(TypeMirror typeMirror, List<String> key, boolean deep) {
    entityTypeCache.put(key, null);
    var value = visitor.visit(typeMirror, deep);
    if (value != null) {
      EntityType entityType = null;
      if (value instanceof EntityType) {
        entityType = (EntityType) value;
      } else {
        entityType = new EntityType(value, variableNameFunction);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
      }
      entityTypeCache.put(key, entityType);

      if (deep) {
        for (Type superType : getSupertypes(typeMirror, deep)) {
          entityType.addSupertype(new Supertype(superType));
        }
      }

      return entityType;
    } else {
      return null;
    }
  }

  private Type createEnumType(DeclaredType declaredType, TypeElement typeElement, boolean deep) {
    // fallback
    var enumType =
        createType(typeElement, TypeCategory.ENUM, declaredType.getTypeArguments(), deep);

    for (Class<? extends Annotation> entityAnn : entityAnnotations) {
      if (typeElement.getAnnotation(entityAnn) != null) {
        var entityType = new EntityType(enumType, variableNameFunction);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        return entityType;
      }
    }
    return enumType;
  }

  private Type createInterfaceType(
      DeclaredType declaredType, TypeElement typeElement, boolean deep) {
    // entity type
    for (Class<? extends Annotation> entityAnn : entityAnnotations) {
      if (typeElement.getAnnotation(entityAnn) != null) {
        return createType(typeElement, TypeCategory.ENTITY, declaredType.getTypeArguments(), deep);
      }
    }

    Iterator<? extends TypeMirror> i = declaredType.getTypeArguments().iterator();

    if (isAssignable(declaredType, mapType)) {
      return createMapType(i, deep);

    } else if (isAssignable(declaredType, listType)) {
      return createCollectionType(Types.LIST, i, deep);

    } else if (isAssignable(declaredType, setType)) {
      return createCollectionType(Types.SET, i, deep);

    } else if (isAssignable(declaredType, collectionType)) {
      return createCollectionType(Types.COLLECTION, i, deep);

    } else {
      var name = typeElement.getQualifiedName().toString();
      return createType(typeElement, TypeCategory.get(name), declaredType.getTypeArguments(), deep);
    }
  }

  private Set<Type> getSupertypes(TypeMirror typeMirror, boolean deep) {
    var doubleIndex = doubleIndexEntities;
    doubleIndexEntities = false;
    Set<Type> superTypes = Collections.emptySet();
    typeMirror = normalize(typeMirror);
    if (typeMirror.getKind() == TypeKind.DECLARED) {
      var declaredType = (DeclaredType) typeMirror;
      var e = (TypeElement) declaredType.asElement();
      // class
      if (e.getKind() == ElementKind.CLASS) {
        if (e.getSuperclass().getKind() != TypeKind.NONE) {
          var supertype = normalize(e.getSuperclass());
          if (supertype instanceof DeclaredType type
              && type.asElement().getAnnotation(QueryExclude.class) != null) {
            return Collections.emptySet();
          } else {
            Type superClass = getType(supertype, deep);
            if (superClass == null) {
              System.err.println("Got no type for " + supertype);
            } else if (!superClass.getFullName().startsWith("java")) {
              superTypes = Collections.singleton(getType(supertype, deep));
            }
          }
        }
        // interface
      } else {
        superTypes = new LinkedHashSet<>(e.getInterfaces().size());
        for (TypeMirror mirror : e.getInterfaces()) {
          Type iface = getType(mirror, deep);
          if (!iface.getFullName().startsWith("java")) {
            superTypes.add(iface);
          }
        }
      }

    } else {
      return Collections.emptySet();
    }
    doubleIndexEntities = doubleIndex;
    return superTypes;
  }

  private boolean isAssignable(TypeMirror type, TypeMirror iface) {
    return env.getTypeUtils().isAssignable(type, iface)
        // XXX Eclipse 3.6 support
        || env.getTypeUtils().erasure(type).toString().equals(iface.toString());
  }

  private boolean isSubType(TypeMirror type1, TypeMirror clazz) {
    return env.getTypeUtils().isSubtype(type1, clazz)
        // XXX Eclipse 3.6 support
        || env.getTypeUtils().directSupertypes(type1).contains(clazz);
  }

  private TypeMirror normalize(TypeMirror type) {
    if (type.getKind() == TypeKind.TYPEVAR) {
      var typeVar = (TypeVariable) type;
      if (typeVar.getUpperBound() != null) {
        return typeVar.getUpperBound();
      }
    } else if (type.getKind() == TypeKind.WILDCARD) {
      var wildcard = (WildcardType) type;
      if (wildcard.getExtendsBound() != null) {
        return wildcard.getExtendsBound();
      }
    }
    return type;
  }

  public void extendTypes() {
    for (EntityType entityType : entityTypeCache.values()) {
      if (entityType.getProperties().isEmpty()) {
        for (Map.Entry<List<String>, EntityType> entry : entityTypeCache.entrySet()) {
          if (entry.getKey().get(0).equals(entityType.getFullName())
              && !entry.getValue().getProperties().isEmpty()) {
            for (Property property : entry.getValue().getProperties()) {
              entityType.addProperty(property);
            }
            break;
          }
        }
      }
    }
  }
}
