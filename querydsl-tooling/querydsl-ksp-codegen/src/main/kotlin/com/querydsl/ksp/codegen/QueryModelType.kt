package com.querydsl.ksp.codegen

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.querydsl.core.annotations.QueryProjection
import com.squareup.kotlinpoet.ksp.toTypeName
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
					if (type.associatedAnnotations.any { ann -> annotation.isEqualTo(ann) }) {
						return type
					}
                }
            }
            return null
        }

        private fun KSAnnotation.isEqualTo(annotationQualifiedName: String): Boolean {
            return annotationType.toTypeName().toString() == annotationQualifiedName
        }
    }
}
