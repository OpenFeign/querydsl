package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import jakarta.persistence.Convert
import kotlin.collections.get

class TypeExtractor(
    private val property: KSPropertyDeclaration,
    private val models: List<QueryModel>
) {
    fun extract(type: KSType): QPropertyType {
        val declaration = type.declaration
        if (declaration is KSTypeAlias) {
            val innerType = declaration.type.resolve()
            return extract(innerType)
        } else {
            return userType(type)
                ?: parameterType(type)
                ?: simpleType(type)
                ?: referenceType(type)
                ?: collectionType(type)
                ?: throwError("Type was not recognised, This may be an entity that has not been annotated with @Entity, or maybe you are using javax instead of jakarta.")
        }
    }

    private fun parameterType(type: KSType): QPropertyType? {
        val declaration = type.declaration
        if (declaration is KSTypeParameter) {
            val bounds = declaration.bounds.toList()
            if (bounds.isEmpty()) {
                return QPropertyType.Simple(SimpleType.Simple(Any::class.asClassName()))
            } else if (bounds.size == 1) {
                val boundType = bounds.single().resolve()
                return extract(boundType)
            } else {
                throwError("There is no support for type parameters with multiple bounds")
            }
        } else {
            return null
        }
    }

    private fun simpleType(type: KSType): QPropertyType.Simple? {
        val className = type.toClassName()
        val simpleType = SimpleType.Mapper.get(className)
        if (simpleType == null) {
            return null
        } else {
            return QPropertyType.Simple(simpleType)
        }
    }

    private fun collectionType(type: KSType): QPropertyType? {
        val declaration = type.declaration
        val types = mutableListOf<KSType>()
        types.add(type)
        if (declaration is KSClassDeclaration) {
            types.addAll(declaration.getAllSuperTypes())
        }
        val classNames = types.map { it.toClassName() }
        return if (classNames.any { it.isMap() }) {
            assertTypeArgCount(type, "map", 2)
            val keyType = extract(type.arguments[0].type!!.resolve())
            val valueType = extract(type.arguments[1].type!!.resolve())
            QPropertyType.MapCollection(keyType, valueType)
        } else if (classNames.any { it.isList() }) {
            assertTypeArgCount(type, "list", 1)
            val innerType = extract(type.arguments.single().type!!.resolve())
            QPropertyType.ListCollection(innerType)
        } else if (classNames.any { it.isSet() }) {
            assertTypeArgCount(type, "set", 1)
            val innerType = extract(type.arguments.single().type!!.resolve())
            return QPropertyType.SetCollection(innerType)
        } else if (classNames.any { it.isArray() }) {
            throwError("Unable to process type Array, Consider using List or Set instead")
        } else {
            null
        }
    }

    private fun referenceType(type: KSType): QPropertyType? {
        val className = type.toClassName()
        val referencedDeclaration = type.declaration
        if (referencedDeclaration is KSClassDeclaration) {
            return when (referencedDeclaration.classKind) {
                ClassKind.ENUM_CLASS -> QPropertyType.EnumReference(type.toClassName())
                ClassKind.CLASS, ClassKind.INTERFACE -> {
                    val target = models.singleOrNull { it.originalClassName == className }
                    if (target == null) {
                        return null
                    } else {
                        return QPropertyType.ObjectReference(target, type.arguments.map { it.toTypeName() })
                    }
                }
                else -> null
            }
        } else {
            return null
        }
    }

    private fun userType(type: KSType): QPropertyType.Unknown? {
        val userTypeAnnotations = listOf(
            ClassName("org.hibernate.annotations", "Type"),
            Convert::class.asClassName()
        )
        if (property.annotations.any { userTypeAnnotations.contains(it.annotationType.resolve().toClassName()) }) {
            return QPropertyType.Unknown(type.toClassName(), type.toTypeName())
        } else {
            return null
        }
    }

    private fun assertTypeArgCount(parentType: KSType, collectionTypeName: String, count: Int) {
        if (parentType.arguments.size != count) {
            throwError("Type looks like a $collectionTypeName so expected $count type arguments, but got ${parentType.arguments.size}")
        }
    }

    private fun throwError(message: String): Nothing {
        error("Error processing ${property.qualifiedName!!.asString()}: $message")
    }
}

private fun ClassName.isMap(): Boolean {
    return listOf(
        Map::class.asClassName(),
        ClassName("kotlin.collections", "MutableMap")
    ).contains(this)
}

private fun ClassName.isSet(): Boolean {
    return listOf(
        Set::class.asClassName(),
        ClassName("kotlin.collections", "MutableSet")
    ).contains(this)
}

private fun ClassName.isList(): Boolean {
    return listOf(
        List::class.asClassName(),
        ClassName("kotlin.collections", "MutableList")
    ).contains(this)
}

private fun ClassName.isArray(): Boolean {
    return this == Array::class.asClassName()
}
