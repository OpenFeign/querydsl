package com.querydsl.ksp.codegen

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.dsl.*
import com.querydsl.ksp.codegen.Naming.toCamelCase
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object QueryModelRenderer {
    fun render(model: QueryModel): TypeSpec {
        return TypeSpec.classBuilder(model.className)
            .setEntitySuperclass(model)
            .addSuperProperty(model)
            .addProperties(model)
            .constructorForPath(model)
            .constructorForMetadata(model)
            .constructorForVariable(model)
            .constructorForTypeMetadata(model)
            .addInitializerCompanionObject(model)
            .addInheritedProperties(model)
            .build()
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
            }
        )
        return this
    }

    private fun TypeSpec.Builder.addSuperProperty(model: QueryModel): TypeSpec.Builder {
        model.superclass?.let { superclass ->
            val superProperty = PropertySpec
                .builder("_super", superclass.className)
                .delegate(
                    CodeBlock.builder()
                        .beginControlFlow("lazy")
                        .addStatement("${superclass.className}(this)")
                        .endControlFlow()
                        .build()
                )
                .build()
            addProperty(superProperty)
        }
        return this
    }

    private fun TypeSpec.Builder.addProperties(model: QueryModel): TypeSpec.Builder {
        model.properties
            .map { renderProperty(it) }
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
        return PropertySpec.builder(property.name, property.type.pathTypeName)
            .getter(
                FunSpec.getterBuilder()
                    .addCode("return _super.${property.name}")
                    .build()
            )
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
        return PropertySpec
            .builder(name, type.queryClassName)
            .delegate(
                CodeBlock.builder()
                    .beginControlFlow("lazy")
                    .addStatement("${type.queryClassName}(forProperty(\"${name}\"))")
                    .endControlFlow()
                    .build()
            )
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
}
