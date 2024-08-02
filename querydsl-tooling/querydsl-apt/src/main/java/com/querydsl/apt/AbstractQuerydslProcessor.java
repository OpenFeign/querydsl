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

import static com.querydsl.apt.APTOptions.QUERYDSL_CREATE_DEFAULT_VARIABLE;
import static com.querydsl.apt.APTOptions.QUERYDSL_ENTITY_ACCESSORS;
import static com.querydsl.apt.APTOptions.QUERYDSL_EXCLUDED_CLASSES;
import static com.querydsl.apt.APTOptions.QUERYDSL_EXCLUDED_PACKAGES;
import static com.querydsl.apt.APTOptions.QUERYDSL_GENERATED_ANNOTATION_CLASS;
import static com.querydsl.apt.APTOptions.QUERYDSL_INCLUDED_CLASSES;
import static com.querydsl.apt.APTOptions.QUERYDSL_INCLUDED_PACKAGES;
import static com.querydsl.apt.APTOptions.QUERYDSL_LIST_ACCESSORS;
import static com.querydsl.apt.APTOptions.QUERYDSL_LOG_INFO;
import static com.querydsl.apt.APTOptions.QUERYDSL_MAP_ACCESSORS;
import static com.querydsl.apt.APTOptions.QUERYDSL_PACKAGE_SUFFIX;
import static com.querydsl.apt.APTOptions.QUERYDSL_PREFIX;
import static com.querydsl.apt.APTOptions.QUERYDSL_SUFFIX;
import static com.querydsl.apt.APTOptions.QUERYDSL_UNKNOWN_AS_EMBEDDABLE;
import static com.querydsl.apt.APTOptions.QUERYDSL_USE_FIELDS;
import static com.querydsl.apt.APTOptions.QUERYDSL_USE_GETTERS;
import static com.querydsl.apt.APTOptions.QUERYDSL_VARIABLE_NAME_FUNCTION_CLASS;

import com.querydsl.codegen.Delegate;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.Serializer;
import com.querydsl.codegen.Supertype;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryExclude;
import com.querydsl.core.annotations.QueryProjection;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

/**
 * {@code AbstractQuerydslProcessor} is the base class for Querydsl annotation processors and
 * contains the main processing logic. The subclasses just provide the configuration.
 *
 * @author tiwe
 */
public abstract class AbstractQuerydslProcessor extends AbstractProcessor {

  public static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

  private final TypeExtractor typeExtractor = new TypeExtractor(true);

  private Configuration conf;

  private RoundEnvironment roundEnv;

  private ExtendedTypeFactory typeFactory;

  private TypeElementHandler elementHandler;

  private Context context;

  private boolean shouldLogInfo;

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    setLogInfo();
    logInfo("Running " + getClass().getSimpleName());

    if (roundEnv.processingOver() || annotations.size() == 0) {
      return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
      logInfo("No sources to process");
      return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    conf = createConfiguration(roundEnv);
    context = new Context();
    var entityAnnotations = conf.getEntityAnnotations();
    var typeMappings = conf.getTypeMappings();
    var queryTypeFactory = conf.getQueryTypeFactory();
    this.typeFactory = createTypeFactory(entityAnnotations, typeMappings, queryTypeFactory);
    elementHandler = createElementHandler(typeMappings, queryTypeFactory);
    this.roundEnv = roundEnv;

    // process annotations
    processAnnotations();

    validateMetaTypes();

    // serialize created types
    serializeMetaTypes();

    return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
  }

  protected TypeElementHandler createElementHandler(
      TypeMappings typeMappings, QueryTypeFactory queryTypeFactory) {
    return new TypeElementHandler(conf, typeFactory, typeMappings, queryTypeFactory);
  }

  protected ExtendedTypeFactory createTypeFactory(
      Set<Class<? extends Annotation>> entityAnnotations,
      TypeMappings typeMappings,
      QueryTypeFactory queryTypeFactory) {
    return new ExtendedTypeFactory(
        processingEnv,
        entityAnnotations,
        typeMappings,
        queryTypeFactory,
        conf.getVariableNameFunction());
  }

  protected void processAnnotations() {
    processExclusions();

    var elements = collectElements();

    // create meta models
    for (Element element : elements) {
      typeFactory.getEntityType(element.asType(), false);
    }
    for (Element element : elements) {
      typeFactory.getEntityType(element.asType(), true);
    }

    // add properties
    var embeddableAnn = conf.getEmbeddableAnnotation() != null;
    var altEntityAnn = conf.getAlternativeEntityAnnotation() != null;
    var superAnn = conf.getSuperTypeAnnotation() != null;
    for (TypeElement element : elements) {
      var entityType = elementHandler.handleEntityType(element);
      registerTypeElement(entityType.getFullName(), element);
      if (typeFactory.isSimpleTypeEntity(element, conf.getEntityAnnotation())) {
        context.entityTypes.put(entityType.getFullName(), entityType);
      } else if (altEntityAnn
          && element.getAnnotation(conf.getAlternativeEntityAnnotation()) != null) {
        context.entityTypes.put(entityType.getFullName(), entityType);
      } else if (embeddableAnn && element.getAnnotation(conf.getEmbeddableAnnotation()) != null) {
        context.embeddableTypes.put(entityType.getFullName(), entityType);
      } else if (superAnn && element.getAnnotation(conf.getSuperTypeAnnotation()) != null) {
        context.supertypes.put(entityType.getFullName(), entityType);
      } else if (!entityType.getDelegates().isEmpty()) {
        context.extensionTypes.put(entityType.getFullName(), entityType);
      } else {
        context.embeddableTypes.put(entityType.getFullName(), entityType);
      }
      context.allTypes.put(entityType.getFullName(), entityType);
    }

    // track also methods from external entity types
    for (EntityType entityType : new ArrayList<>(typeFactory.getEntityTypes())) {
      var fullName = entityType.getFullName();
      if (!context.allTypes.keySet().contains(fullName)) {
        var element = processingEnv.getElementUtils().getTypeElement(fullName);
        if (element != null) {
          elementHandler.handleEntityType(element);
        }
      }
    }

    // add external parents
    for (Element element : elements) {
      EntityType entityType = typeFactory.getEntityType(element.asType(), false);
      addExternalParents(entityType);
    }

    // add properties from parents
    Set<EntityType> handled = new HashSet<>();
    for (EntityType entityType : context.allTypes.values()) {
      addSupertypeFields(entityType, handled);
    }

    processProjectionTypes(elements);

    // extend entity types
    typeFactory.extendTypes();

    context.clean();
  }

  private void addExternalParents(EntityType entityType) {
    Deque<Type> superTypes = new ArrayDeque<>();
    if (entityType.getSuperType() != null) {
      superTypes.push(entityType.getSuperType().getType());
    }

    while (!superTypes.isEmpty()) {
      var superType = superTypes.pop();
      if (!context.allTypes.containsKey(superType.getFullName())) {
        var typeElement = processingEnv.getElementUtils().getTypeElement(superType.getFullName());
        if (typeElement == null) {
          throw new IllegalStateException("Found no type for " + superType.getFullName());
        }
        if (conf.isStrictMode()
            && !TypeUtils.hasAnnotationOfType(typeElement, conf.getEntityAnnotations())) {
          continue;
        }
        var superEntityType = elementHandler.handleEntityType(typeElement);
        if (superEntityType.getSuperType() != null) {
          superTypes.push(superEntityType.getSuperType().getType());
        }
        context.allTypes.put(superType.getFullName(), superEntityType);
      }
    }
  }

  protected Set<TypeElement> collectElements() {

    // from delegate methods
    Set<TypeElement> elements = new HashSet<>(processDelegateMethods());

    // from class annotations
    for (Class<? extends Annotation> annotation : conf.getEntityAnnotations()) {
      for (Element element : getElements(annotation)) {
        if (element instanceof TypeElement) {
          elements.add((TypeElement) element);
        }
      }
    }

    // from package annotations
    if (conf.getEntitiesAnnotation() != null) {
      for (Element element : getElements(conf.getEntitiesAnnotation())) {
        var mirror = TypeUtils.getAnnotationMirrorOfType(element, conf.getEntitiesAnnotation());
        elements.addAll(TypeUtils.getAnnotationValuesAsElements(mirror, "value"));
      }
    }

    // from embedded annotations
    if (conf.getEmbeddedAnnotation() != null) {
      elements.addAll(getEmbeddedTypes());
    }

    // from embedded
    if (conf.isUnknownAsEmbedded()) {
      elements.addAll(getTypeFromProperties(elements));
    }

    // from annotation less supertypes
    if (!conf.isStrictMode()) {
      elements.addAll(getAnnotationlessSupertypes(elements));
    }

    // register possible embedded types of non-tracked supertypes
    if (conf.getEmbeddedAnnotation() != null) {
      Class<? extends Annotation> embedded = conf.getEmbeddedAnnotation();
      Set<TypeElement> embeddedElements = new HashSet<>();
      for (TypeElement element : elements) {
        var superTypeMirror = element.getSuperclass();
        while (superTypeMirror != null) {
          var superTypeElement =
              (TypeElement) processingEnv.getTypeUtils().asElement(superTypeMirror);
          if (superTypeElement != null) {
            List<? extends Element> enclosed = superTypeElement.getEnclosedElements();
            for (Element child : enclosed) {
              if (child.getAnnotation(embedded) != null) {
                handleEmbeddedType(child, embeddedElements);
              }
            }
            superTypeMirror = superTypeElement.getSuperclass();
            if (superTypeMirror instanceof NoType) {
              superTypeMirror = null;
            }
          } else {
            superTypeMirror = null;
          }
        }
      }

      // register found elements
      for (TypeElement element : embeddedElements) {
        if (!elements.contains(element)) {
          elementHandler.handleEntityType(element);
        }
      }
    }

    return elements;
  }

  private Set<TypeElement> getAnnotationlessSupertypes(Set<TypeElement> elements) {
    Set<TypeElement> rv = new HashSet<>();
    for (TypeElement element : elements) {
      var superTypeMirror = element.getSuperclass();
      while (superTypeMirror != null) {
        var superTypeElement =
            (TypeElement) processingEnv.getTypeUtils().asElement(superTypeMirror);
        if (superTypeElement != null
            && !superTypeElement.toString().startsWith("java.lang.")
            && !TypeUtils.hasAnnotationOfType(superTypeElement, conf.getEntityAnnotations())) {
          rv.add(superTypeElement);
          superTypeMirror = superTypeElement.getSuperclass();
          if (superTypeMirror instanceof NoType) {
            superTypeMirror = null;
          }
        } else {
          superTypeMirror = null;
        }
      }
    }
    return rv;
  }

  private void registerTypeElement(String entityName, TypeElement element) {
    var elements =
        context.typeElements.computeIfAbsent(entityName, k -> new HashSet<TypeElement>());
    elements.add(element);
  }

  private void processProjectionTypes(Set<TypeElement> elements) {
    Set<Element> visited = new HashSet<>();
    for (Element element : getElements(QueryProjection.class)) {
      if (element.getKind() == ElementKind.CONSTRUCTOR) {
        var parent = element.getEnclosingElement();
        if (!elements.contains(parent) && !visited.contains(parent)) {
          var model = elementHandler.handleProjectionType((TypeElement) parent, true);
          registerTypeElement(model.getFullName(), (TypeElement) parent);
          context.projectionTypes.put(model.getFullName(), model);
          visited.add(parent);
        }
      }
      if (element.getKind().isClass() && !visited.contains(element)) {
        var model = elementHandler.handleProjectionType((TypeElement) element, false);
        registerTypeElement(model.getFullName(), (TypeElement) element);
        context.projectionTypes.put(model.getFullName(), model);
        visited.add(element);
      }
    }
  }

  private Set<TypeElement> getEmbeddedTypes() {
    Set<TypeElement> elements = new HashSet<>();
    // only creation
    for (Element element : getElements(conf.getEmbeddedAnnotation())) {
      handleEmbeddedType(element, elements);
    }
    return elements;
  }

  private void handleEmbeddedType(Element element, Set<TypeElement> elements) {
    var type = element.asType();
    if (element.getKind() == ElementKind.METHOD) {
      type = ((ExecutableElement) element).getReturnType();
    }
    var typeName = type.toString();

    if (typeName.startsWith(Collection.class.getName())
        || typeName.startsWith(List.class.getName())
        || typeName.startsWith(Set.class.getName())) {
      type = ((DeclaredType) type).getTypeArguments().get(0);

    } else if (typeName.startsWith(Map.class.getName())) {
      type = ((DeclaredType) type).getTypeArguments().get(1);
    }

    var typeElement = typeExtractor.visit(type);

    if (typeElement != null
        && !TypeUtils.hasAnnotationOfType(typeElement, conf.getEntityAnnotations())) {
      if (!typeElement.getQualifiedName().toString().startsWith("java.")) {
        elements.add(typeElement);
      }
    }
  }

  private Set<TypeElement> getTypeFromProperties(Set<TypeElement> parents) {
    Set<TypeElement> elements = new HashSet<>();
    for (Element element : parents) {
      if (element instanceof TypeElement) {
        processFromProperties((TypeElement) element, elements);
      }
    }

    var iterator = elements.iterator();
    while (iterator.hasNext()) {
      var element = iterator.next();
      var name = element.getQualifiedName().toString();
      if (name.startsWith("java.")) {
        iterator.remove();
      } else {
        var annotated = false;
        for (Class<? extends Annotation> annotation : conf.getEntityAnnotations()) {
          annotated |= element.getAnnotation(annotation) != null;
        }
        if (annotated) {
          iterator.remove();
        }
      }
    }

    return elements;
  }

  private void processFromProperties(TypeElement type, Set<TypeElement> types) {
    List<? extends Element> children = type.getEnclosedElements();
    var config = conf.getConfig(type, children);

    // fields
    if (config.visitFieldProperties()) {
      for (VariableElement field : ElementFilter.fieldsIn(children)) {
        var typeElement = typeExtractor.visit(field.asType());
        if (typeElement != null) {
          types.add(typeElement);
        }
      }
    }

    // getters
    if (config.visitMethodProperties()) {
      for (ExecutableElement method : ElementFilter.methodsIn(children)) {
        var name = method.getSimpleName().toString();
        if ((name.startsWith("get") || name.startsWith("is")) && method.getParameters().isEmpty()) {
          var typeElement = typeExtractor.visit(method.getReturnType());
          if (typeElement != null) {
            types.add(typeElement);
          }
        }
      }
    }
  }

  private void addSupertypeFields(EntityType model, Set<EntityType> handled) {
    if (handled.add(model)) {
      for (Supertype supertype : model.getSuperTypes()) {
        var entityType = context.allTypes.get(supertype.getType().getFullName());
        if (entityType != null) {
          addSupertypeFields(entityType, handled);
          supertype.setEntityType(entityType);
          model.include(supertype);
        }
      }
    }
  }

  private void processExclusions() {
    for (Element element : getElements(QueryExclude.class)) {
      if (element instanceof PackageElement) {
        conf.addExcludedPackage(((PackageElement) element).getQualifiedName().toString());
      } else if (element instanceof TypeElement) {
        conf.addExcludedClass(((TypeElement) element).getQualifiedName().toString());
      } else {
        throw new IllegalArgumentException(element.toString());
      }
    }
  }

  private Set<TypeElement> processDelegateMethods() {
    Set<? extends Element> delegateMethods = getElements(QueryDelegate.class);
    Set<TypeElement> typeElements = new HashSet<>();

    for (Element delegateMethod : delegateMethods) {
      var method = (ExecutableElement) delegateMethod;
      var element = delegateMethod.getEnclosingElement();
      var name = method.getSimpleName().toString();
      Type delegateType = typeFactory.getType(element.asType(), true);
      Type returnType = typeFactory.getType(method.getReturnType(), true);
      var parameters = elementHandler.transformParams(method.getParameters());
      // remove first element
      parameters = parameters.subList(1, parameters.size());

      EntityType entityType = null;
      for (AnnotationMirror annotation : delegateMethod.getAnnotationMirrors()) {
        if (TypeUtils.isAnnotationMirrorOfType(annotation, QueryDelegate.class)) {
          var type = TypeUtils.getAnnotationValueAsTypeMirror(annotation, "value");
          if (type != null) {
            entityType = typeFactory.getEntityType(type, true);
          }
        }
      }

      if (entityType != null) {
        registerTypeElement(entityType.getFullName(), (TypeElement) element);
        entityType.addDelegate(
            new Delegate(entityType, delegateType, name, parameters, returnType));
        var typeElement = processingEnv.getElementUtils().getTypeElement(entityType.getFullName());
        var isAnnotated = false;
        for (Class<? extends Annotation> ann : conf.getEntityAnnotations()) {
          if (typeElement.getAnnotation(ann) != null) {
            isAnnotated = true;
          }
        }
        if (isAnnotated) {
          // handle also properties of entity type
          typeElements.add(
              processingEnv.getElementUtils().getTypeElement(entityType.getFullName()));
        } else {
          // skip handling properties
          context.extensionTypes.put(entityType.getFullName(), entityType);
          context.allTypes.put(entityType.getFullName(), entityType);
        }
      }
    }

    return typeElements;
  }

  private void validateMetaTypes() {
    Stream.of(
            context.supertypes.values(),
            context.entityTypes.values(),
            context.extensionTypes.values(),
            context.embeddableTypes.values(),
            context.projectionTypes.values())
        .flatMap(Collection::stream)
        .forEach(
            entityType ->
                entityType.getProperties().stream()
                    .filter(
                        property -> property.getInits() != null && property.getInits().size() > 0)
                    .forEach(property -> validateInits(entityType, property)));
  }

  protected void validateInits(EntityType entityType, Property property) {
    for (String init : property.getInits()) {
      if (!init.startsWith("*") && property.getType() instanceof EntityType) {
        var initProperty = init.contains(".") ? init.substring(0, init.indexOf('.')) : init;
        var propertyNames = ((EntityType) property.getType()).getPropertyNames();
        if (!propertyNames.contains(initProperty)) {
          processingEnv
              .getMessager()
              .printMessage(
                  Kind.ERROR,
                  "Illegal inits of "
                      + entityType.getFullName()
                      + "."
                      + property.getName()
                      + ": "
                      + initProperty
                      + " not found in "
                      + propertyNames);
        }
      }
    }
  }

  private void serializeMetaTypes() {
    if (!context.supertypes.isEmpty()) {
      logInfo("Serializing Supertypes");
      serialize(conf.getSupertypeSerializer(), context.supertypes.values());
    }

    if (!context.entityTypes.isEmpty()) {
      logInfo("Serializing Entity types");
      serialize(conf.getEntitySerializer(), context.entityTypes.values());
    }

    if (!context.extensionTypes.isEmpty()) {
      logInfo("Serializing Extension types");
      serialize(conf.getEmbeddableSerializer(), context.extensionTypes.values());
    }

    if (!context.embeddableTypes.isEmpty()) {
      logInfo("Serializing Embeddable types");
      serialize(conf.getEmbeddableSerializer(), context.embeddableTypes.values());
    }

    if (!context.projectionTypes.isEmpty()) {
      logInfo("Serializing Projection types");
      serialize(conf.getDTOSerializer(), context.projectionTypes.values());
    }
  }

  private Set<? extends Element> getElements(Class<? extends Annotation> a) {
    return roundEnv.getElementsAnnotatedWith(a);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedOptions() {
    return new HashSet<>(
        Arrays.asList(
            QUERYDSL_CREATE_DEFAULT_VARIABLE,
            QUERYDSL_PREFIX,
            QUERYDSL_SUFFIX,
            QUERYDSL_PACKAGE_SUFFIX,
            QUERYDSL_MAP_ACCESSORS,
            QUERYDSL_LIST_ACCESSORS,
            QUERYDSL_ENTITY_ACCESSORS,
            QUERYDSL_USE_FIELDS,
            QUERYDSL_USE_GETTERS,
            QUERYDSL_EXCLUDED_PACKAGES,
            QUERYDSL_EXCLUDED_CLASSES,
            QUERYDSL_INCLUDED_PACKAGES,
            QUERYDSL_INCLUDED_CLASSES,
            QUERYDSL_UNKNOWN_AS_EMBEDDABLE,
            QUERYDSL_VARIABLE_NAME_FUNCTION_CLASS,
            QUERYDSL_LOG_INFO,
            QUERYDSL_GENERATED_ANNOTATION_CLASS));
  }

  private void setLogInfo() {
    var hasProperty = processingEnv.getOptions().containsKey(QUERYDSL_LOG_INFO);
    if (hasProperty) {
      var val = processingEnv.getOptions().get(QUERYDSL_LOG_INFO);
      shouldLogInfo = Boolean.parseBoolean(val);
    }
  }

  private void logInfo(String message) {
    if (shouldLogInfo) {
      processingEnv.getMessager().printMessage(Kind.NOTE, message);
    }
  }

  private void serialize(Serializer serializer, Collection<EntityType> models) {
    for (EntityType model : models) {
      try {
        var className = getClassName(model);

        // skip if type is excluded class or in excluded package
        if (conf.isExcludedPackage(model.getPackageName())
            || conf.isExcludedClass(model.getFullName())) {
          continue;
        }

        var elements = context.typeElements.get(model.getFullName());

        if (elements == null) {
          elements = new HashSet<>();
        }
        for (Property property : model.getProperties()) {
          if (property.getType().getCategory() == TypeCategory.CUSTOM) {
            var customElements = context.typeElements.get(property.getType().getFullName());
            if (customElements != null) {
              elements.addAll(customElements);
            }
          }
        }

        logInfo("Generating " + className + " for " + elements);
        try (var writer = conf.getFiler().createFile(processingEnv, className, elements)) {
          var serializerConfig = conf.getSerializerConfig(model);
          serializer.serialize(model, serializerConfig, new JavaWriter(writer));
        }

      } catch (IOException e) {
        System.err.println(e.getMessage());
        processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
      }
    }
  }

  protected String getClassName(EntityType model) {
    var type = conf.getTypeMappings().getPathType(model, model, true);
    var packageName = type.getPackageName();
    return packageName.isEmpty()
        ? type.getSimpleName()
        : (packageName + "." + type.getSimpleName());
  }

  protected abstract Configuration createConfiguration(RoundEnvironment roundEnv);
}
