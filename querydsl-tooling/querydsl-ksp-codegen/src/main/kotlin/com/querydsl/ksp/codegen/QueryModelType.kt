package com.querydsl.ksp.codegen

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.querydsl.core.annotations.QueryProjection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass

enum class QueryModelType(
    val associatedAnnotations: List<String>
) {
    ENTITY(
		listOf(
			Entity::class.qualifiedName!!,
			"com.querydsl.core.annotations.QueryEntity",
			"org.springframework.data.mongodb.core.mapping.Document"
		)
	),
    EMBEDDABLE(listOf(Embeddable::class.qualifiedName!!)),
    SUPERCLASS(listOf(MappedSuperclass::class.qualifiedName!!)),
    QUERY_PROJECTION(listOf(QueryProjection::class.qualifiedName!!));

    companion object {
        fun autodetect(classDeclaration: KSClassDeclaration): QueryModelType? {
            for (annotation in classDeclaration.annotations) {
                for (type in QueryModelType.entries) {
					if (type.associatedAnnotations.any { ann -> annotation.matches(ann) }) {
						return type
					}
                }
            }
            return null
        }

        /**
         * Match a [KSAnnotation] against a fully-qualified annotation name. This is
         * called for *referenced* classes (during property type resolution and
         * superclass lookup), where we have no FQN pre-filter — a user's own
         * `@com.example.Entity` must not be mistaken for `@jakarta.persistence.Entity`.
         *
         * Uses the symbol API's qualifiedName rather than kotlinpoet-ksp's
         * `toClassName()`/`toTypeName()`: those traverse analysis-API lifetime
         * tokens and can throw `KaInvalidLifetimeOwnerAccessException` under KSP2.
         * The shortName check is a cheap prefilter that avoids the resolve+lookup
         * for the common case.
         */
        private fun KSAnnotation.matches(annotationQualifiedName: String): Boolean {
            val simpleName = annotationQualifiedName.substringAfterLast('.')
            if (shortName.asString() != simpleName) return false
            return annotationType.resolve().declaration.qualifiedName?.asString() == annotationQualifiedName
        }
    }
}
