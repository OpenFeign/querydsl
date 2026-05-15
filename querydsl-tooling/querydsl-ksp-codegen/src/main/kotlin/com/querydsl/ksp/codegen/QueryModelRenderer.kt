package com.querydsl.ksp.codegen

import com.querydsl.core.annotations.Generated
import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.*
import com.querydsl.ksp.codegen.Naming.toCamelCase
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object QueryModelRenderer {
    fun render(model: QueryModel): TypeSpec {
        return when (model.type) {
            QueryModelType.QUERY_PROJECTION -> TypeSpec.classBuilder(model.className)
                .setPrimaryConstructor(model)
                .setEntitySuperclass(model)
                .addSuperConstructorParameter(model)
                .addAnnotation(Generated::class)
                .build()

            else -> TypeSpec.classBuilder(model.className)
                .setEntitySuperclass(model)
                .addSuperProperty(model)
                .addProperties(model)
                .constructorForPath(model)
                .constructorForMetadata(model)
                .constructorForVariable(model)
                .constructorForTypeMetadata(model)
                .addInitializerCompanionObject(model)
                .addInheritedProperties(model)
                .addAnnotation(Generated::class)
                .build()
        }
    }

    private fun TypeSpec.Builder.setEntitySuperclass(model: QueryModel): TypeSpec.Builder {
        val constraint: TypeName = if (model.typeParameterCount > 0) {
            val typeParams = (0..<model.typeParameterCount).map { STAR }
            model.originalClassName.parameterizedBy(typeParams)
        } else {
            model.originalClassName
        }
        superclass(
            when (model.type) {
                QueryModelType.ENTITY, QueryModelType.SUPERCLASS -> EntityPathBase::class.asClassName().parameterizedBy(constraint)
                QueryModelType.EMBEDDABLE -> BeanPath::class.asClassName().parameterizedBy(constraint)
                QueryModelType.QUERY_PROJECTION -> ConstructorExpression::class.asClassName().parameterizedBy(constraint)
            }
        )
        return this
    }

    private fun TypeSpec.Builder.addSuperProperty(model: QueryModel): TypeSpec.Builder {
        model.superclass?.let { superclass ->
            // Eager @JvmField: exposed as a Java field, and inherited
            // @JvmField properties below can read `_super.x` directly.
            // Construction recursion follows the inheritance chain (acyclic
            // by language rule), so eager init is safe.
            val superProperty = PropertySpec
                .builder("_super", superclass.className)
                .initializer("${superclass.className}(this)")
                .addAnnotation(JvmField::class)
                .build()
            addProperty(superProperty)
        }
        return this
    }

    private fun TypeSpec.Builder.addProperties(model: QueryModel): TypeSpec.Builder {
        model.properties
            .map {
                val propertySpec = renderProperty(it)
                when {
                    propertySpec.delegated -> propertySpec
                    else -> propertySpec.toBuilder().addAnnotation(JvmField::class).build()
                }
            }
            .forEach { addProperty(it) }
        return this
    }

    private fun TypeSpec.Builder.addInheritedProperties(model: QueryModel): TypeSpec.Builder {
        model.superclass?.let { superclass ->
            superclass.properties
                .map { renderInheritedProperty(it) }
                .forEach { addProperty(it) }
            addInheritedProperties(superclass)
        }
        return this
    }

    private fun renderInheritedProperty(property: QProperty): PropertySpec {
        val type = property.type
        if (type is QPropertyType.ObjectReference) {
            // Defer inherited object references with `by lazy`. An eager
            // `@JvmField val foo = _super.foo` reads the parent's per-instance
            // `by lazy` during the child's construction, which for a
            // mapped-superclass that `@ManyToOne`s back to a concrete
            // subclass (e.g. `Auditable.createdBy : User` with `User extends
            // Auditable`) triggers infinite construction recursion — every
            // new child level allocates a fresh parent whose own lazy then
            // allocates another child. Lazy here, paired with @get:JvmName
            // to drop the `get` prefix, mirrors how we render direct object
            // references and keeps the construction tree bounded to the
            // depth the caller actually navigates.
            val getterJvmName = AnnotationSpec.builder(JvmName::class)
                .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
                .addMember("%S", property.name)
                .build()
            return PropertySpec.builder(property.name, type.queryClassName)
                .delegate(
                    CodeBlock.builder()
                        .beginControlFlow("lazy")
                        .addStatement("_super.${property.name}")
                        .endControlFlow()
                        .build()
                )
                .addAnnotation(getterJvmName)
                .build()
        }
        // Eager @JvmField for scalars / enums / collections — these don't
        // recurse, so eager init is safe and Java consumers get field-style
        // access (`qChild.actived` rather than `qChild.getActived()`).
        return PropertySpec.builder(property.name, property.type.pathTypeName)
            .initializer("_super.${property.name}")
            .addAnnotation(JvmField::class)
            .build()
    }

    private fun renderProperty(property: QProperty): PropertySpec {
        val name = property.name
        val type = property.type
        return when (type) {
            is QPropertyType.Simple -> property.type.type.render(name)
            is QPropertyType.EnumReference -> renderEnumReference(name, type)
            is QPropertyType.ObjectReference -> renderObjectReference(name, type)
            is QPropertyType.Unknown -> renderUnknownProperty(name, type)
            is QPropertyType.ListCollection -> {
                val inner = type.innerType
                PropertySpec
                    .builder(name, ListPath::class.asClassName().parameterizedBy(inner.originalTypeName, inner.pathTypeName))
                    .initializer("createList(\"$name\", ${inner.originalClassName}::class.java, ${inner.pathClassName}::class.java, null)")
                    .build()
            }
            is QPropertyType.MapCollection -> {
                val keyType = type.keyType
                val valueType = type.valueType
                PropertySpec
                    .builder(name, MapPath::class.asClassName().parameterizedBy(keyType.originalTypeName, valueType.originalTypeName, valueType.pathTypeName))
                    .initializer("createMap(\"$name\", ${keyType.originalClassName}::class.java, ${valueType.originalClassName}::class.java, ${valueType.pathClassName}::class.java)")
                    .build()
            }
            is QPropertyType.SetCollection -> {
                val inner = type.innerType
                PropertySpec
                    .builder(name, SetPath::class.asClassName().parameterizedBy(inner.originalTypeName, inner.pathTypeName))
                    .initializer("createSet(\"$name\", ${inner.originalClassName}::class.java, ${inner.pathClassName}::class.java, null)")
                    .build()
            }
            is QPropertyType.CollectionCollection -> {
                val inner = type.innerType
                PropertySpec
                    .builder(name, CollectionPath::class.asClassName().parameterizedBy(inner.originalTypeName, inner.pathTypeName))
                    .initializer("createCollection(\"$name\", ${inner.originalClassName}::class.java, ${inner.pathClassName}::class.java, null)")
                    .build()
            }
        }
    }

    private fun renderUnknownProperty(name: String, type: QPropertyType.Unknown) : PropertySpec {
        return PropertySpec
            .builder(name, SimplePath::class.asClassName().parameterizedBy(type.originalTypeName))
            .initializer("createSimple(\"$name\", ${type.originalClassName}::class.java)")
            .build()
    }

    private fun renderEnumReference(name: String, type: QPropertyType.EnumReference): PropertySpec {
        return PropertySpec
            .builder(name, EnumPath::class.asClassName().parameterizedBy(type.enumClassName))
            .initializer("createEnum(\"${name}\", ${type.enumClassName}::class.java)")
            .build()
    }

    private fun renderObjectReference(name: String, type: QPropertyType.ObjectReference): PropertySpec {
        // Object references are `by lazy` — defers construction so self-referential
        // entities don't stack-overflow at init time, and the type stays non-null
        // for ergonomic Kotlin queries (`q.headOffice.id` with no `?.`).
        //
        // Drop the synthesised `get` prefix on the JVM getter so Java consumers see
        // `q.headOffice()` instead of `q.getHeadOffice()` — closer to the field-style
        // access they get from querydsl-apt-generated Q-classes.
        val getterJvmName = AnnotationSpec.builder(JvmName::class)
            .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
            .addMember("%S", name)
            .build()
        return PropertySpec
            .builder(name, type.queryClassName)
            .delegate(
                CodeBlock.builder()
                    .beginControlFlow("lazy")
                    .addStatement("${type.queryClassName}(forProperty(\"${name}\"))")
                    .endControlFlow()
                    .build()
            )
            .addAnnotation(getterJvmName)
            .build()
    }

    private fun TypeSpec.Builder.constructorForPath(model: QueryModel): TypeSpec.Builder {
        if (model.typeParameterCount > 0) {
            val typeParams = (0..<model.typeParameterCount).map { STAR }
            val source = model.originalClassName.parameterizedBy(typeParams)
            val spec = FunSpec.constructorBuilder()
                .addParameter("path", Path::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(source)))
                .callSuperConstructor("path.type, path.metadata")
                .build()
            addFunction(spec)
        } else {
            val source = model.originalClassName
            val spec = FunSpec.constructorBuilder()
                .addParameter("path", Path::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(source)))
                .callSuperConstructor("path.type, path.metadata")
                .build()
            addFunction(spec)
        }
        return this
    }

    private fun TypeSpec.Builder.constructorForMetadata(model: QueryModel): TypeSpec.Builder {
        val source = model.originalClassName
        val spec = FunSpec.constructorBuilder()
            .addParameter("metadata", PathMetadata::class)
            .callSuperConstructor("$source::class.java, metadata")
            .build()
        addFunction(spec)
        return this
    }

    private fun TypeSpec.Builder.constructorForVariable(model: QueryModel): TypeSpec.Builder {
        val spec = FunSpec.constructorBuilder()
            .addParameter("variable", String::class)
            .callSuperConstructor(
                "${model.originalClassName}::class.java",
                "${com.querydsl.core.types.PathMetadataFactory::class.qualifiedName!!}.forVariable(variable)"
            )
            .build()
        addFunction(spec)
        return this
    }

    private fun TypeSpec.Builder.constructorForTypeMetadata(model: QueryModel): TypeSpec.Builder {
        if (model.typeParameterCount > 0) {
            val typeParams = (0..<model.typeParameterCount).map { STAR }
            val source = model.originalClassName.parameterizedBy(typeParams)
            val spec = FunSpec.constructorBuilder()
                .addParameter("type", Class::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(source)))
                .addParameter("metadata", PathMetadata::class)
                .callSuperConstructor("type, metadata")
                .build()
            addFunction(spec)
        } else {
            val source = model.originalClassName
            val spec = FunSpec.constructorBuilder()
                .addParameter("type", Class::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(source)))
                .addParameter("metadata", PathMetadata::class)
                .callSuperConstructor("type, metadata")
                .build()
            addFunction(spec)
        }
        return this
    }

    private fun TypeSpec.Builder.addInitializerCompanionObject(model: QueryModel): TypeSpec.Builder {
        val source = model.originalClassName
        val qSource = model.className
        val name = source.simpleName.toCamelCase()
        val companionObject = TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder(name, qSource)
                    .initializer("$qSource(\"${name}\")")
                    .addAnnotation(JvmField::class)
                    .build()
            )
            .build()
        addType(companionObject)
        return this
    }

    private fun TypeSpec.Builder.setPrimaryConstructor(model: QueryModel): TypeSpec.Builder {
        val constructorSpec = FunSpec.constructorBuilder().apply {
            model.properties.forEach {
                addParameter(
                    it.name,
                    Expression::class.asClassName().parameterizedBy(it.type.originalTypeName)
                )
            }
        }.build()
        primaryConstructor(constructorSpec)
        return this
    }

    private fun TypeSpec.Builder.addSuperConstructorParameter(model: QueryModel): TypeSpec.Builder {
        val paramTypes = model.properties.joinToString(", ", prefix = "arrayOf(", postfix = ")") {
            "${it.type.originalClassName}::class.java"
        }
        val paramNames = model.properties.joinToString(", ") { it.name }
        addSuperclassConstructorParameter(
            "${model.originalClassName}::class.java, $paramTypes, $paramNames"
        )
        return this
    }
}
