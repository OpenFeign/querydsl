package com.querydsl.ksp.codegen

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName

class QueryModel(
    val originalClassName: ClassName,
    val typeParameterCount: Int,
    val className: ClassName,
    val type: QueryModelType,
    val originatingFile: KSFile
) {
    var superclass: QueryModel? = null

    val properties = mutableListOf<QProperty>()
}
