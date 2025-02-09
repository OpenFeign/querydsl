package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.querydsl.ksp.codegen.QueryModelExtractor.ModelDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import jakarta.persistence.Transient

class QueryModelStore(
	private val settings: KspSettings
) {
	private val transientClassName = Transient::class.asClassName()
	private val processed = mutableMapOf<String, ModelDeclaration>()

	fun lookupOrInsert(classDeclaration: KSClassDeclaration, create: () -> QueryModel): ModelDeclaration {
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

	fun process(): List<QueryModel> {
		processProperties()
		return processed.values.map { it.model }
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
}
