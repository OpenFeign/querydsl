package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

object QueryModelExtractor {
    private val transientClassName = Transient::class.asClassName()

    fun process(
        settings: KspSettings,
        declarations: List<ModelDeclaration>
    ): List<QueryModel> {
        val declarationToModelMap = declarations.associateWith { modelDeclaration ->
            val declaration = modelDeclaration.classDeclaration
            QueryModel(
                originalClassName = declaration.toClassName(),
                typeParameterCount = declaration.typeParameters.size,
                className = ClassName(
                    "${declaration.packageName.asString()}${settings.packageSuffix}",
                    "${settings.prefix}${declaration.simpleName.asString()}${settings.suffix}"
                ),
                type = modelDeclaration.type
            )
        }
        val models = declarationToModelMap.values.toList()
        declarationToModelMap.forEach { entry ->
            val classDeclaration = entry.key.classDeclaration
            val superclass = classDeclaration.superclassOrNull()
            if (superclass != null) {
                val match = models.singleOrNull { it.originalClassName == superclass }
                if (match == null) {
                    error("${classDeclaration.qualifiedName!!.asString()} has superclass ${superclass}, but this is not a processed entity")
                } else {
                    entry.value.superclass = match
                }
            }
        }
        declarationToModelMap.forEach { entry ->
            val model = entry.value
            val classDeclaration = entry.key.classDeclaration
            val properties = classDeclaration.getDeclaredProperties()
                .filter { !it.isTransient() }
                .filter { !it.isGetterTransient() }
                .filter { it.hasBackingField }
                .map { property ->
                    val propName = property.simpleName.asString()
                    val extractor = TypeExtractor(property, models)
                    val type = extractor.extract(property.type.resolve())
                    QProperty(propName, type)
                }
            model.properties.addAll(properties)
        }
        return models
    }

    private fun KSPropertyDeclaration.isTransient(): Boolean {
        return annotations.any { it.annotationType.resolve().toClassName() == transientClassName }
    }

    private fun KSPropertyDeclaration.isGetterTransient(): Boolean {
        return this.getter?.let { getter ->
            getter.annotations.any { it.annotationType.resolve().toClassName() == transientClassName }
        } ?: false
    }

    private fun KSClassDeclaration.superclassOrNull(): ClassName? {
        for (superType in superTypes) {
            val resolvedType = superType.resolve()
            val declaration = resolvedType.declaration
            if (declaration is KSClassDeclaration) {
                val superClassName = declaration.toClassName()
                if (declaration.classKind == ClassKind.CLASS && superClassName != Any::class.asClassName()) {
                    return superClassName
                }
            }
        }
        return null
    }

    class ModelDeclaration(
        val classDeclaration: KSClassDeclaration,
        val type: QueryModel.Type
    )
}
