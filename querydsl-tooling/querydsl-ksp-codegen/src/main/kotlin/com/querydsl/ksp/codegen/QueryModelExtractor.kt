package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
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

    fun add(classDeclaration: KSClassDeclaration, type: QueryModelType): QueryModel {
        return lookupOrInsert(classDeclaration) { addNew(classDeclaration, type) }
            .model
    }

    fun add(classDeclaration: KSClassDeclaration): QueryModel {
        return lookupOrInsert(classDeclaration) {
            addNew(
                classDeclaration,
                QueryModelType.autodetect(classDeclaration)
                    ?: error("Unable to resolve type of entity for ${classDeclaration.qualifiedName!!.asString()}")
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
        type: QueryModelType
    ): QueryModel {
        val queryModel = toQueryModel(classDeclaration, type)
        val superclass = classDeclaration.superclassOrNull()
        if (superclass != null) {
            queryModel.superclass = add(superclass)
        }
        return queryModel
    }

    private fun processProperties() {
        processed.values.map { modelDeclaration ->
            val properties = extractProperties(modelDeclaration.declaration)
            modelDeclaration.model.properties.addAll(properties)
        }
    }

    private fun extractProperties(declaration: KSClassDeclaration): List<QProperty> {
        return declaration
            .getDeclaredProperties()
            .filter { !it.isTransient() }
            .filter { !it.isGetterTransient() }
            .filter { it.hasBackingField }
            .map { property ->
                val propName = property.simpleName.asString()
                val extractor = TypeExtractor(settings, property)
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

    private fun toQueryModel(classDeclaration: KSClassDeclaration, type: QueryModelType): QueryModel {
        return QueryModel(
            originalClassName = classDeclaration.toClassName(),
            typeParameterCount = classDeclaration.typeParameters.size,
            className = queryClassName(classDeclaration, settings),
            type = type,
            originatingFile = classDeclaration.containingFile
        )
    }

    companion object {
        fun queryClassName(classDeclaration: KSClassDeclaration, settings: KspSettings): ClassName {
            return ClassName(
                "${classDeclaration.packageName.asString()}${settings.packageSuffix}",
                "${settings.prefix}${classDeclaration.simpleName.asString()}${settings.suffix}"
            )
        }
    }

    private class ModelDeclaration(
        val declaration: KSClassDeclaration,
        val model: QueryModel
    )
}
