package com.querydsl.ksp.codegen

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.querydsl.core.annotations.QueryProjection
import com.squareup.kotlinpoet.ksp.toTypeName
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass

enum class QueryModelType(
    val associatedAnnotation: String
) {
    ENTITY(Entity::class.qualifiedName!!),
    EMBEDDABLE(Embeddable::class.qualifiedName!!),
    SUPERCLASS(MappedSuperclass::class.qualifiedName!!),
    QUERY_PROJECTION(QueryProjection::class.qualifiedName!!);

    companion object {
        fun autodetect(classDeclaration: KSClassDeclaration): QueryModelType? {
            for (annotation in classDeclaration.annotations) {
                for (type in QueryModelType.entries) {
                    if (annotation.isEqualTo(type.associatedAnnotation)) {
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
