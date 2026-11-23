package com.querydsl.ksp.codegen

class KspSettings(
    val enable: Boolean,
    val indent: String,
    val prefix: String,
    val suffix: String,
    val packageSuffix: String,
    val excludedPackages: List<String>,
    val excludedClasses: List<String>,
    val includedPackages: List<String>,
    val includedClasses: List<String>,
) {
    constructor(args: KspArgParser) : this(
        enable = args.getBoolean("enable") ?: true,
        indent = args.getString("indent") ?: "    ",
        prefix = args.getString("prefix") ?: "Q",
        suffix = args.getString("suffix") ?: "",
        packageSuffix = args.getString("packageSuffix") ?: "",
        excludedPackages = args.getCommaSeparatedList("excludedPackages"),
        excludedClasses = args.getCommaSeparatedList("excludedClasses"),
        includedPackages = args.getCommaSeparatedList("includedPackages"),
        includedClasses = args.getCommaSeparatedList("includedClasses")
    )
}
