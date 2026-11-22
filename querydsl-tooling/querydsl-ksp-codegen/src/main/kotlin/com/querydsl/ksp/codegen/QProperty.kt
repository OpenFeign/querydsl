package com.querydsl.ksp.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class QProperty(
    val name: String,
    val type: QPropertyType,
    val projectionArgument: ProjectionArgument = ProjectionArgument.Direct(type.originalTypeName)
)

sealed interface ProjectionArgument {
    class Direct(val typeName: TypeName) : ProjectionArgument
    class StringValue(val className: ClassName) : ProjectionArgument
    data object UnsupportedValue : ProjectionArgument
}
