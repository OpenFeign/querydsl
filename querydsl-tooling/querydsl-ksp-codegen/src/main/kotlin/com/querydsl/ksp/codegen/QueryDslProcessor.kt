package com.querydsl.ksp.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass

class QueryDslProcessor(
    private val settings: KspSettings,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (settings.enable) {
            processImpl(resolver)
        }
        return emptyList()
    }

    private fun processImpl(resolver: Resolver) {
        val declarations = mutableMapOf<KSClassDeclaration, QueryModel.Type>()

        resolver.getSymbolsWithAnnotation(Embeddable::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .filter { isIncluded(it) }
            .forEach { declarations.putIfAbsent(it, QueryModel.Type.EMBEDDABLE) }

        resolver.getSymbolsWithAnnotation(MappedSuperclass::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .filter { isIncluded(it) }
            .forEach { declarations.putIfAbsent(it, QueryModel.Type.SUPERCLASS) }

        resolver.getSymbolsWithAnnotation(Entity::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .filter { isIncluded(it) }
            .forEach { declarations.putIfAbsent(it, QueryModel.Type.ENTITY) }

        val allDeclarations = declarations.map { QueryModelExtractor.ModelDeclaration(it.key, it.value) }
        val models = QueryModelExtractor.process(settings, allDeclarations)
        models.forEach { model ->
            val typeSpec = QueryModelRenderer.render(model)
            FileSpec.builder(model.className)
                .indent(settings.indent)
                .addType(typeSpec)
                .build()
                .writeTo(codeGenerator, false)
        }
    }

    private fun isIncluded(declaration: KSClassDeclaration): Boolean {
        val className = declaration.qualifiedName!!.asString()
        if (settings.excludedPackages.any { className.startsWith(it) }) {
            return false
        } else if (settings.excludedClasses.any { it == className }) {
            return false
        } else if (settings.includedClasses.isNotEmpty()) {
            return settings.includedClasses.any { it == className }
        } else if (settings.includedPackages.isNotEmpty()) {
            return settings.includedPackages.any { className.startsWith(it) }
        } else {
            return true
        }
    }
}
