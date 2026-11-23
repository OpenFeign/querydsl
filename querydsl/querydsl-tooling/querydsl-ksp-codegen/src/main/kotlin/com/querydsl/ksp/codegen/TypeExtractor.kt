package com.querydsl.ksp.codegen

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import jakarta.persistence.Convert

class TypeExtractor(
    private val settings: KspSettings,
    private val fullPathName: String,
    private val annotations: Sequence<KSAnnotation>
) {
    fun extract(type: KSType): QPropertyType {
        when (val declaration = type.declaration) {
            is KSClassDeclaration if declaration.modifiers.contains(Modifier.VALUE) -> {
                val wrappedType = declaration
                    .primaryConstructor
                    ?.parameters
                    ?.firstOrNull()
                    ?.type
                    ?.resolve()
                    ?: throwError("Cannot resolve type: $declaration")

                return extract(wrappedType)
            }
            is KSTypeAlias -> {
                val innerType = declaration.type.resolve()
                return extract(innerType)
            }
            else -> {
                return userType(type)
                    ?: parameterType(type)
                    ?: simpleType(type)
                    ?: referenceType(type)
                    ?: collectionType(type)
                    ?: fallbackType(type)
            }
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

    private fun fallbackType(type: KSType): QPropertyType.Simple {
        val declaration = type.declaration
        val isComparable = if (declaration is KSClassDeclaration) {
            val comparableNames = listOfNotNull(
                Comparable::class.java.canonicalName,
                java.lang.Comparable::class.qualifiedName
            )
            // Match supertypes by their declaration's qualified name. KSType.toClassName()
            // (the kotlinpoet-ksp extension) throws IllegalStateException for parameterized
            // types — getAllSuperTypes() routinely yields Comparable<E>, Collection<E>,
            // Iterable<E>, etc., so iterating with toClassName() blows up on any entity that
            // implements a parameterized interface (Set<X>, List<X>, custom Comparable<X>
            // wrappers). The declaration's qualifiedName carries the same identity without
            // the type-args check.
            declaration.getAllSuperTypes().any { superType ->
                val superDecl = superType.declaration
                superDecl is KSClassDeclaration &&
                    comparableNames.contains(superDecl.qualifiedName?.asString())
            }
        } else {
            false
        }
        val className = type.toClassNameSimple()
        if (isComparable) {
            return QPropertyType.Simple(SimpleType.Comparable(className))
        } else {
            return QPropertyType.Simple(SimpleType.Simple(className))
        }
    }

    private fun simpleType(type: KSType): QPropertyType.Simple? {
        val className = type.toClassNameSimple()
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
        val classNames = types.map { it.toClassNameSimple() }
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
        } else if (classNames.any { it.isCollection() }) {
            // Plain Collection<E> / Iterable<E> (e.g. JPA `Collection<Degree>` from Java).
            // Checked AFTER List/Set so concrete sub-interfaces still produce ListPath/SetPath.
            // Walk type.arguments first (the original use-site), then any supertype that
            // carries the element type — handles cases where the entity field is declared
            // as Iterable<X> but resolves through a parameterized supertype chain.
            val carrier = types.firstOrNull { it.arguments.size == 1 } ?: type
            val arg = carrier.arguments.singleOrNull()?.type
                ?: throwError("Unable to resolve element type for collection")
            val innerType = extract(arg.resolve())
            QPropertyType.CollectionCollection(innerType)
        } else if (classNames.any { it.isArray() }) {
            throwError("Unable to process type Array, Consider using List or Set instead")
        } else {
            null
        }
    }

    private fun referenceType(type: KSType): QPropertyType? {
        val referencedDeclaration = type.declaration
        if (referencedDeclaration is KSClassDeclaration) {
            return when (referencedDeclaration.classKind) {
                ClassKind.ENUM_CLASS -> QPropertyType.EnumReference(type.toClassNameSimple())
                ClassKind.CLASS, ClassKind.INTERFACE -> {
                    if (isEntity(referencedDeclaration)) {
                        return QPropertyType.ObjectReference(
                            entityClassName = referencedDeclaration.toClassName(),
                            queryClassName = QueryModelExtractor.queryClassName(referencedDeclaration, settings),
                            typeArgs = type.arguments.map { it.toTypeName() }
                        )
                    } else {
                        return null
                    }
                }
                else -> null
            }
        } else {
            return null
        }
    }

    private fun isEntity(classDeclaration: KSClassDeclaration): Boolean {
        return QueryModelType.autodetect(classDeclaration) != null
    }

    private fun userType(type: KSType): QPropertyType.Unknown? {
        if (type.isEnum()) {
            return null
        }

        // Compare annotations by their fully-qualified name via the symbol API
        // rather than kotlinpoet-ksp's toClassName(): the latter goes through
        // KSType.declaration / isError() which traverses analysis-API lifetime
        // tokens and can throw KaInvalidLifetimeOwnerAccessException under KSP2
        // for any property whose annotations include @Convert / @Type / etc.
        // The shortName check is a cheap prefilter so the common (no annotation)
        // case never resolves at all.
        val userTypeFqns = listOf(
            "org.hibernate.annotations.Type",
            "org.hibernate.annotations.JdbcTypeCode",
            Convert::class.qualifiedName!!
        )
        val userSimpleNames = userTypeFqns.mapTo(mutableSetOf()) { it.substringAfterLast('.') }
        val match = annotations.any { ann ->
            if (ann.shortName.asString() !in userSimpleNames) return@any false
            val fqn = ann.annotationType.resolve().declaration.qualifiedName?.asString()
            fqn in userTypeFqns
        }
        return if (match) QPropertyType.Unknown(type.toClassNameSimple(), type.toTypeName()) else null
    }

    private fun assertTypeArgCount(parentType: KSType, collectionTypeName: String, count: Int) {
        if (parentType.arguments.size != count) {
            throwError("Type looks like a $collectionTypeName so expected $count type arguments, but got ${parentType.arguments.size}")
        }
    }

    private fun throwError(message: String): Nothing {
        error("Error processing $fullPathName: $message")
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

private fun ClassName.isCollection(): Boolean {
    return listOf(
        Collection::class.asClassName(),
        ClassName("kotlin.collections", "MutableCollection"),
        Iterable::class.asClassName(),
        ClassName("kotlin.collections", "MutableIterable")
    ).contains(this)
}

private fun ClassName.isArray(): Boolean {
    return this == Array::class.asClassName()
}

/**
 * Ignores arguments and nullability, returning simple ClassName.
 */
private fun KSType.toClassNameSimple(): ClassName {
    return when (val decl = declaration) {
        is KSClassDeclaration -> decl.toClassName()
        is KSTypeAlias -> decl.toClassName()
        is KSTypeParameter -> error("Cannot convert KSTypeParameter to ClassName: '$this'")
        else -> error("Could not compute ClassName for '$this'")
    }
}

private fun KSType.isEnum(): Boolean {
    val referencedDeclaration = declaration

    return referencedDeclaration is KSClassDeclaration && referencedDeclaration.classKind == ClassKind.ENUM_CLASS
}
