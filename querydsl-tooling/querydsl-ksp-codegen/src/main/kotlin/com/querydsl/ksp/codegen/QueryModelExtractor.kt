package com.querydsl.ksp.codegen

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

class QueryModelExtractor(
    private val settings: KspSettings,
    private val store: QueryModelStore,
    private val allFiles: List<KSFile>
) {
    fun add(classDeclaration: KSClassDeclaration, type: QueryModelType): QueryModel {
        return store.lookupOrInsert(classDeclaration) { addNew(classDeclaration, type) }
            .model
    }

    fun add(classDeclaration: KSClassDeclaration): QueryModel {
        return store.lookupOrInsert(classDeclaration) {
            addNew(
                classDeclaration,
                QueryModelType.autodetect(classDeclaration)
                    ?: error("Unable to resolve type of entity for ${classDeclaration.qualifiedName!!.asString()}")
            )
        }.model
    }

    private fun addNew(
        classDeclaration: KSClassDeclaration,
        type: QueryModelType
    ): QueryModel {
        val queryModel = toQueryModel(classDeclaration, type)
        val superclass = classDeclaration.superclassOrNull()
        if (superclass != null) {
            queryModel.superclass = add(superclass)
        }
        return queryModel
    }

    private fun KSClassDeclaration.superclassOrNull(): KSClassDeclaration? {
        for (superType in superTypes) {
            val resolvedType = superType.resolve()
            val declaration = resolvedType.declaration
            if (declaration is KSClassDeclaration) {
                val superClassName = declaration.toClassName()
                if (declaration.classKind == ClassKind.CLASS && superClassName != Any::class.asClassName()) {
                    return declaration
                }
            }
        }
        return null
    }

    private fun toQueryModel(classDeclaration: KSClassDeclaration, type: QueryModelType): QueryModel {
        val sources = classDeclaration.containingFile.let { containingFile ->
            if (containingFile == null) {
                allFiles
            } else {
                listOf(containingFile)
            }
        }
        return QueryModel(
            originalClassName = classDeclaration.toClassName(),
            typeParameterCount = classDeclaration.typeParameters.size,
            className = queryClassName(classDeclaration, settings),
            type = type,
            sources = sources
        )
    }

    companion object {
        fun queryClassName(classDeclaration: KSClassDeclaration, settings: KspSettings): ClassName {
            return ClassName(
                "${classDeclaration.packageName.asString()}${settings.packageSuffix}",
                "${settings.prefix}${classDeclaration.simpleName.asString()}${settings.suffix}"
            )
        }
    }

    class ModelDeclaration(
        val declaration: KSClassDeclaration,
        val model: QueryModel
    )
}
