package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunction
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import jakarta.persistence.Transient

class QueryModelExtractor(
    private val settings: KspSettings
) {
    private val transientSimpleName = Transient::class.simpleName!!
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
                    parameter.annotations
                )
                val type = extractor.extract(parameter.type.resolve())
                QProperty(paramName, type)
            }
            .toList()
    }

    private fun extractPropertiesForClass(declaration: KSClassDeclaration): List<QProperty> {
        // Materialise the property sequence eagerly: KSP2's analysis-API
        // lifetime tokens can advance between lazy steps, so the chained
        // sequence form throws KaInvalidLifetimeOwnerAccessException at
        // type.resolve() time. A list-based iteration keeps each property's
        // resolution within a single live context.
        val properties = declaration.getDeclaredProperties().toList()
        val result = mutableListOf<QProperty>()
        for (property in properties) {
            if (property.isTransient() || property.isGetterTransient()) continue
            // Exclude Java `static` and `transient` (the keyword, not the
            // annotation) fields. KSP surfaces them as KSPropertyDeclaration
            // alongside instance fields, but they're not persisted.
            if (Modifier.JAVA_STATIC in property.modifiers) continue
            if (Modifier.JAVA_TRANSIENT in property.modifiers) continue
            if (!property.hasBackingField) continue
            val propName = property.simpleName.asString()
            val extractor = TypeExtractor(
                settings,
                propName,
                property.annotations
            )
            val type = extractor.extract(property.type.resolve())
            result += QProperty(propName, type)
        }
        return result
    }

    private fun KSPropertyDeclaration.isTransient(): Boolean {
        return annotations.any { it.hasSimpleName(transientSimpleName) }
    }

    private fun KSPropertyDeclaration.isGetterTransient(): Boolean {
        return this.getter?.let { getter ->
            getter.annotations.any { it.hasSimpleName(transientSimpleName) }
        } ?: false
    }

    // Match by KSAnnotation.shortName (a KSName) instead of resolving the
    // annotation type to a ClassName. Calling toClassName() / KSType.declaration
    // under KSP2 traverses analysis-API lifetime tokens that can throw
    // KaInvalidLifetimeOwnerAccessException ("PSI has changed since creation").
    // Simple-name match is accurate for the JPA annotations we recognise.
    private fun KSAnnotation.hasSimpleName(name: String): Boolean {
        return shortName.asString() == name
    }

    private fun KSClassDeclaration.superclassOrNull(): KSClassDeclaration? {
        for (superType in superTypes) {
            val resolvedType = superType.resolve()
            val declaration = resolvedType.declaration
            if (declaration is KSClassDeclaration) {
                // Compare against kotlin.Any via qualifiedName rather than
                // KotlinPoet's toClassName(): the latter calls isError() on the
                // resolved type, which invalidates KSP2 analysis-API lifetime
                // tokens for any KSType resolved later in the same process()
                // invocation (manifests as KaInvalidLifetimeOwnerAccessException
                // when we later resolve property types).
                val fqn = declaration.qualifiedName?.asString()
                if (declaration.classKind == ClassKind.CLASS && fqn != "kotlin.Any") {
                    return declaration
                }
            }
        }
        return null
    }

    private fun toQueryModel(classDeclaration: KSClassDeclaration, type: QueryModelType, constructor: KSFunctionDeclaration?): QueryModel {
        return QueryModel(
            // Build the ClassName from raw KSP names to avoid kotlinpoet-ksp's
            // toClassName(), which invalidates KSP2 lifetime tokens (see
            // superclassOrNull above for the same workaround).
            originalClassName = ClassName(
                classDeclaration.packageName.asString(),
                classDeclaration.simpleName.asString()
            ),
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
