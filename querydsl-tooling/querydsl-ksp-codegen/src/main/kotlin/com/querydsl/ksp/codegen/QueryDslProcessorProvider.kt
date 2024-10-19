package com.querydsl.ksp.codegen

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class QueryDslProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): QueryDslProcessor {
        val parser = KspArgParser("querydsl", environment.options)
        val settings = KspSettings(parser)
        parser.throwUnusedArgs()
        return QueryDslProcessor(settings, environment.codeGenerator)
    }
}
