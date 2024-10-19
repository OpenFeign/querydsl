package com.querydsl.ksp.codegen

class KspArgParser(
    private val baseName: String,
    baseArgs: Map<String, String>
) {
    private val usedArgs = mutableSetOf<String>()
    private val args = baseArgs.filter { it.key.startsWith("${baseName}.") }.mapKeys { it.key.drop(baseName.length + 1) }

    fun getString(key: String): String? {
        usedArgs.add(key)
        return args[key]
    }

    fun getBoolean(key: String): Boolean? {
        val value = getString(key)
        if (value == null) {
            return null
        } else {
            return value.toBooleanStrict()
        }
    }

    fun getCommaSeparatedList(key: String): List<String> {
        val value = getString(key)
        if (value == null) {
            return emptyList()
        } else {
            return value.split(",").map { it.trim() }
        }
    }

    fun throwUnusedArgs() {
        val unusedArgs = args.filter { !usedArgs.contains(it.key) }.map { it.key }
        if (unusedArgs.isNotEmpty()) {
            error("QueryDSL KSP args were provided but are not recognised or supported: ${unusedArgs.joinToString(", ")}")
        }
    }
}
