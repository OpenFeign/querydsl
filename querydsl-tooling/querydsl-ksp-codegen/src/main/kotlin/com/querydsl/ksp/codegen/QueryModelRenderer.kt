package com.querydsl.ksp.codegen

import com.querydsl.core.annotations.Generated
import com.querydsl.core.types.ExpressionException
import com.querydsl.core.types.FactoryExpressionBase
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Visitor
import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.*
import com.querydsl.ksp.codegen.Naming.toCamelCase
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object QueryModelRenderer {
    private val primitiveDefaults = mapOf(
        BOOLEAN to "false", BYTE to "0", SHORT to "0", INT to "0", LONG to "0L",
        FLOAT to "0.0f", DOUBLE to "0.0", CHAR to "'\\u0000'"
    )

    fun render(model: QueryModel): TypeSpec {
        return when (model.type) {
            QueryModelType.QUERY_PROJECTION -> TypeSpec.classBuilder(model.className)
                .setEntitySuperclass(model)
                .let { builder ->
                    if (model.usesDirectProjection()) builder.addDirectProjection(model)
                    else builder.addProjectionConstructors(model)
                }
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
                QueryModelType.QUERY_PROJECTION -> {
                    val base = if (model.usesDirectProjection()) FactoryExpressionBase::class else ConstructorExpression::class
                    base.asClassName().parameterizedBy(constraint)
                }
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

    private fun QueryModel.usesDirectProjection(): Boolean {
        val arguments = projectionConstructors.flatten().map { it.projectionArgument }
        return typeParameterCount == 0 && arguments.any { it is ProjectionArgument.StringValue } &&
            arguments.none { it is ProjectionArgument.UnsupportedValue }
    }

    private fun TypeSpec.Builder.addDirectProjection(model: QueryModel): TypeSpec.Builder {
        val expressionList = List::class.asClassName().parameterizedBy(Expression::class.asClassName().parameterizedBy(STAR))
        addProperty(PropertySpec.builder("projectionArgs", expressionList, KModifier.PRIVATE).build())
        addProperty(PropertySpec.builder("constructorIndex", INT, KModifier.PRIVATE).build())
        model.projectionConstructors.forEachIndexed { index, properties ->
            val constructor = FunSpec.constructorBuilder()
                .callSuperConstructor(CodeBlock.of("%T::class.java", model.originalClassName))
            properties.forEach {
                constructor.addParameter(it.name, Expression::class.asClassName().parameterizedBy(it.type.originalTypeName))
            }
            constructor.addStatement("this.constructorIndex = %L", index)
            val args = properties.map { CodeBlock.of("%N", it.name) }.joinToCode(", ")
            constructor.addStatement("this.projectionArgs = %T.unmodifiableList(listOf(%L))", java.util.Collections::class, args)
            addFunction(constructor.build())
        }
        addFunction(FunSpec.builder("getArgs").addModifiers(KModifier.OVERRIDE)
            .returns(expressionList).addStatement("return projectionArgs").build())
        val result = TypeVariableName("R")
        val context = TypeVariableName("C")
        addFunction(FunSpec.builder("accept").addModifiers(KModifier.OVERRIDE)
            .addTypeVariables(listOf(result, context))
            .addParameter("visitor", Visitor::class.asClassName().parameterizedBy(result, context))
            .addParameter("context", context.copy(nullable = true))
            .returns(result).addStatement("return visitor.visit(this, context)").build())
        listOf(Path::class.asClassName().parameterizedBy(model.originalClassName), STRING).forEach { aliasType ->
            addFunction(FunSpec.builder("as").addParameter("alias", aliasType)
                .returns(Expression::class.asClassName().parameterizedBy(model.originalClassName))
                .addStatement("return %T.`as`(this, alias)", ExpressionUtils::class).build())
        }
        val body = CodeBlock.builder().beginControlFlow("try").beginControlFlow("return when (constructorIndex)")
        model.projectionConstructors.forEachIndexed { index, properties ->
            val args = properties.mapIndexed { argIndex, property ->
                when (val argument = property.projectionArgument) {
                    is ProjectionArgument.StringValue -> CodeBlock.of("%T(args[%L] as %T)", argument.className, argIndex, STRING)
                    is ProjectionArgument.Direct -> {
                        val defaultValue = primitiveDefaults[argument.typeName]
                        if (defaultValue == null) CodeBlock.of("args[%L] as %T", argIndex, argument.typeName)
                        else CodeBlock.of("(args[%L] as %T ?: %L)", argIndex, argument.typeName.copy(nullable = true), defaultValue)
                    }
                    ProjectionArgument.UnsupportedValue -> error("Unsupported value class projection")
                }
            }.joinToCode(", ")
            body.addStatement("%L -> %T(%L)", index, model.originalClassName, args)
        }
        body.addStatement("else -> error(%S)", "Unknown projection constructor").endControlFlow()
            .nextControlFlow("catch (exception: %T)", Exception::class)
            .addStatement("throw %T(exception.message, exception)", ExpressionException::class)
            .endControlFlow()
        addFunction(FunSpec.builder("newInstance").addModifiers(KModifier.OVERRIDE)
            .addParameter("args", ANY.copy(nullable = true), KModifier.VARARG)
            .returns(model.originalClassName).addCode(body.build()).build())
        return this
    }

    private fun TypeSpec.Builder.addProjectionConstructors(model: QueryModel): TypeSpec.Builder {
        if (model.projectionConstructors.size <= 1) {
            return setPrimaryConstructor(model).addSuperConstructorParameter(model)
        }
        model.projectionConstructors.forEach { properties ->
            val constructor = FunSpec.constructorBuilder()
            properties.forEach {
                constructor.addParameter(
                    it.name,
                    Expression::class.asClassName().parameterizedBy(it.type.originalTypeName)
                )
            }
            val paramTypes = CodeBlock.builder().add("arrayOf(")
            properties.forEachIndexed { index, property ->
                if (index > 0) paramTypes.add(", ")
                paramTypes.add("%T::class.java", property.type.originalClassName)
            }
            paramTypes.add(")")
            constructor.callSuperConstructor(
                listOf(CodeBlock.of("%T::class.java", model.originalClassName), paramTypes.build()) +
                    properties.map { CodeBlock.of("%N", it.name) }
            )
            addFunction(constructor.build())
        }
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
