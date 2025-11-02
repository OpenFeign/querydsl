package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunction
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import jakarta.persistence.Transient

class QueryModelExtractor(
    private val settings: KspSettings
) {
    private val transientClassName = Transient::class.asClassName()
    private val processed = mutableMapOf<String, ModelDeclaration>()

    fun addConstructor(classDeclaration: KSClassDeclaration, constructor: KSFunctionDeclaration): QueryModel {
        return lookupOrInsert(classDeclaration) { addNew(classDeclaration, QueryModelType.QUERY_PROJECTION, constructor) }
            .model
    }

    fun addClass(classDeclaration: KSClassDeclaration, type: QueryModelType): QueryModel {
        return lookupOrInsert(classDeclaration) { addNew(classDeclaration, type, null) }
            .model
    }

    fun addClass(classDeclaration: KSClassDeclaration): QueryModel {
        return lookupOrInsert(classDeclaration) {
            addNew(
                classDeclaration,
                QueryModelType.autodetect(classDeclaration)
                    ?: error("Unable to resolve type of entity for ${classDeclaration.qualifiedName!!.asString()}"),
                null
            )
        }.model
    }

    fun process(): List<QueryModel> {
        processProperties()
        return processed.values.map { it.model }
    }

    private fun lookupOrInsert(classDeclaration: KSClassDeclaration, create: () -> QueryModel): ModelDeclaration {
        val qualifiedName = classDeclaration.qualifiedName!!.asString()
        val processedType = processed[qualifiedName]
        if (processedType == null) {
            val newQueryModel = create()
            val declaration = ModelDeclaration(classDeclaration, newQueryModel)
            processed[qualifiedName] = declaration
            return declaration
        } else {
            return processedType
        }
    }

    private fun addNew(
        classDeclaration: KSClassDeclaration,
        type: QueryModelType,
        constructor: KSFunctionDeclaration?
    ): QueryModel {
        val queryModel = toQueryModel(classDeclaration, type, constructor)
        val superclass = classDeclaration.superclassOrNull()
        if (superclass != null) {
            queryModel.superclass = addClass(superclass)
        }
        return queryModel
    }

    private fun processProperties() {
        processed.values.map { modelDeclaration ->
            val constructor = modelDeclaration.model.constructor
            if (constructor == null) {
                val properties = extractPropertiesForClass(modelDeclaration.declaration)
                modelDeclaration.model.properties.addAll(properties)
            } else {
                val properties = extractPropertiesForConstructor(constructor)
                modelDeclaration.model.properties.addAll(properties)
            }
        }
    }

    private fun extractPropertiesForConstructor(declaration: KSFunctionDeclaration): List<QProperty> {
        return declaration
            .parameters
            .map { parameter ->
                val paramName = parameter.name!!.asString()
                val extractor = TypeExtractor(
                    settings,
                    "${declaration.parent?.location} - $paramName",
                )
                val type = extractor.extract(parameter.type.resolve())
                QProperty(paramName, type)
            }
            .toList()
    }

    private fun extractPropertiesForClass(declaration: KSClassDeclaration): List<QProperty> {
        return declaration
            .getDeclaredProperties()
            .filter { !it.isTransient() }
            .filter { !it.isGetterTransient() }
            .filter { it.hasBackingField }
            .map { property ->
                val propName = property.simpleName.asString()
                val extractor = TypeExtractor(
                    settings,
                    property.simpleName.asString(),
                )
                val type = extractor.extract(property.type.resolve())
                QProperty(propName, type)
            }
            .toList()
    }

    private fun KSPropertyDeclaration.isTransient(): Boolean {
        return annotations.any { it.annotationType.resolve().toClassName() == transientClassName }
    }

    private fun KSPropertyDeclaration.isGetterTransient(): Boolean {
        return this.getter?.let { getter ->
            getter.annotations.any { it.annotationType.resolve().toClassName() == transientClassName }
        } ?: false
    }

    private fun KSClassDeclaration.superclassOrNull(): KSClassDeclaration? {
        for (superType in superTypes) {
            val resolvedType = superType.resolve()
            val declaration = resolvedType.declaration
            if (declaration is KSClassDeclaration) {
                val superClassName = declaration.toClassName()
                if (declaration.classKind == ClassKind.CLASS && superClassName != Any::class.asClassName()) {
                    return declaration
                }
            }
        }
        return null
    }

    private fun toQueryModel(classDeclaration: KSClassDeclaration, type: QueryModelType, constructor: KSFunctionDeclaration?): QueryModel {
        return QueryModel(
            originalClassName = classDeclaration.toClassName(),
            typeParameterCount = classDeclaration.typeParameters.size,
            className = queryClassName(classDeclaration, settings),
            type = type,
            constructor = constructor,
            originatingFile = classDeclaration.containingFile
        )
    }

    companion object {
        fun queryClassName(classDeclaration: KSClassDeclaration, settings: KspSettings): ClassName {
            val simpleNames = generateSequence(classDeclaration) { it.parentDeclaration as? KSClassDeclaration }
                .map { it.simpleName.asString() }
                .toList()
                .reversed()

            val className = simpleNames.joinToString("_")

            return ClassName(
                "${classDeclaration.packageName.asString()}${settings.packageSuffix}",
                "${settings.prefix}${className}${settings.suffix}"
            )
        }
    }

    private class ModelDeclaration(
        val declaration: KSClassDeclaration,
        val model: QueryModel
    )
}
