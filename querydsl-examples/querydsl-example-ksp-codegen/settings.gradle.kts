pluginManagement {
    val properties = java.util.Properties().apply {
        load(file("gradle.properties").reader())
    }

    val kotlinVersion = properties["kotlin.version"] as String
    val kspVersion = properties["ksp.version"] as String

    plugins {
        kotlin("jvm") version kotlinVersion
        id("com.google.devtools.ksp") version kspVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}
