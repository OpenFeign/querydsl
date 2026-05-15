package com.querydsl.ksp.codegen

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class QueryDslProcessor(
    private val settings: KspSettings,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    private val rendered = mutableSetOf<String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!settings.enable) return emptyList()
        // Fresh extractor per round: under KSP2 the analysis-API lifetime is
        // scoped to one process() invocation, so KSClassDeclaration references
        // captured in a previous round are invalid here.
        val typeProcessor = QueryModelExtractor(settings)

        // KSP2 invalidates analysis-API lifetime tokens for symbols whose PSI is
        // not yet stable; reading those throws KaInvalidLifetimeOwnerAccessException
        // ("PSI has changed since creation"). The canonical fix is to call
        // validate() on each symbol and defer the unprocessable ones to a later
        // round. Deferred symbols are returned from process() and KSP re-invokes
        // us once their PSI has settled.
        val deferred = mutableListOf<KSAnnotated>()

        QueryModelType.entries.forEach { type ->
			type.associatedAnnotations.forEach { associatedAnnotation ->
				resolver.getSymbolsWithAnnotation(associatedAnnotation)
					.forEach { declaration ->
						if (!declaration.validate()) {
							deferred += declaration
							return@forEach
						}
						when {
							type == QueryModelType.QUERY_PROJECTION -> {
								val errorMessage = "$associatedAnnotation annotation" +
										" must be declared on a constructor function or class"
								when (declaration) {
									is KSFunctionDeclaration -> {
										if (!declaration.isConstructor()) error(errorMessage)
										val parentDeclaration = declaration.parent as? KSClassDeclaration
											?: error(errorMessage)
										if (isIncluded(parentDeclaration)) {
											typeProcessor.addConstructor(parentDeclaration, declaration)
										}
									}
									is KSClassDeclaration -> {
										if (isIncluded(declaration)) {
											typeProcessor.addClass(declaration, type)
										}
									}
									else -> error(errorMessage)
								}
							}
							declaration is KSClassDeclaration -> {
								if (isIncluded(declaration)) {
									typeProcessor.addClass(declaration, type)
								}
							}
							else -> {
								error("Annotated element was expected to be class or constructor, instead got ${declaration}")
							}
						}
					}
			}
        }

        // Extract properties and render now, while the analysis-API session is still live.
        // KSP2 invalidates symbol references after process() returns, so deferring this work
        // to finish() throws KaInvalidLifetimeOwnerAccessException.
        val models = typeProcessor.process()
        models.forEach { model ->
            if (model.originatingFile == null) {
                // Skip models without an originating file. This happens when the model comes
                // from a compiled dependency rather than a source file in the current module.
                // The Q-class is expected to be packaged alongside the compiled dependency.
                return@forEach
            }
            val canonical = model.className.canonicalName
            if (!rendered.add(canonical)) return@forEach

            val typeSpec = QueryModelRenderer.render(model)
            FileSpec.builder(model.className)
                .indent(settings.indent)
                .addType(typeSpec)
                .build()
                .writeTo(
                    codeGenerator = codeGenerator,
                    aggregating = false,
                    originatingKSFiles = listOf(model.originatingFile)
                )
        }

        return deferred
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
