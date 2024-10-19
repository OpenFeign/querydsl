package com.querydsl.ksp.codegen

import com.querydsl.core.types.dsl.EnumPath
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.core.types.dsl.MapPath
import com.querydsl.core.types.dsl.SetPath
import com.querydsl.core.types.dsl.SimplePath
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName

sealed interface QPropertyType {
    val pathTypeName: TypeName
    val pathClassName: ClassName
    val originalClassName: ClassName
    val originalTypeName: TypeName

    class ListCollection(
        val innerType: QPropertyType
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = List::class.asClassName()

        override val originalTypeName: TypeName
            get() = List::class.asTypeName().parameterizedBy(innerType.originalTypeName)

        override val pathClassName: ClassName
            get() = ListPath::class.asClassName()

        override val pathTypeName: TypeName
            get() = ListPath::class.asClassName().parameterizedBy(innerType.originalTypeName, innerType.pathTypeName)
    }

    class SetCollection(
        val innerType: QPropertyType
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = Set::class.asClassName()

        override val originalTypeName: TypeName
            get() = Set::class.asTypeName().parameterizedBy(innerType.originalTypeName)

        override val pathClassName: ClassName
            get() = SetPath::class.asClassName()

        override val pathTypeName: TypeName
            get() = SetPath::class.asClassName().parameterizedBy(innerType.originalTypeName, innerType.pathTypeName)
    }

    class MapCollection(
        val keyType: QPropertyType,
        val valueType: QPropertyType
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = Map::class.asClassName()

        override val originalTypeName: TypeName
            get() = Map::class.asTypeName().parameterizedBy(keyType.originalTypeName, valueType.originalTypeName)

        override val pathClassName: ClassName
            get() = MapPath::class.asClassName()

        override val pathTypeName: TypeName
            get() = MapPath::class.asTypeName().parameterizedBy(keyType.originalTypeName, valueType.originalTypeName, valueType.pathTypeName)
    }

    class Simple(
        val type: SimpleType
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = type.className

        override val originalTypeName: TypeName
            get() = type.className

        override val pathClassName: ClassName
            get() = type.pathClassName

        override val pathTypeName: TypeName
            get() = type.pathTypeName
    }

    class Unknown(
        private val innerClassName: ClassName,
        private val innerTypeName: TypeName
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = innerClassName

        override val originalTypeName: TypeName
            get() = innerTypeName

        override val pathClassName: ClassName
            get() = SimplePath::class.asClassName()

        override val pathTypeName: TypeName
            get() = SimplePath::class.asTypeName().parameterizedBy(innerTypeName)
    }

    class EnumReference(
        val enumClassName: ClassName
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = enumClassName

        override val originalTypeName: TypeName
            get() = enumClassName

        override val pathClassName: ClassName
            get() = EnumPath::class.asClassName()

        override val pathTypeName: TypeName
            get() = EnumPath::class.asTypeName().parameterizedBy(enumClassName)
    }

    class ObjectReference(
        val target: QueryModel,
        val typeArgs: List<TypeName>
    ) : QPropertyType {
        override val originalClassName: ClassName
            get() = target.originalClassName

        override val originalTypeName: TypeName
            get() {
                if (typeArgs.isEmpty()) {
                    return target.originalClassName
                } else {
                    return target.originalClassName.parameterizedBy(typeArgs)
                }
            }

        override val pathClassName: ClassName
            get() = target.className

        override val pathTypeName: TypeName
            get() = target.className
    }
}
