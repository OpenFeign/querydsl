/*
 * Copyright 2021, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.kotlin.codegen

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

object CompileUtils {
    fun assertCompiles(name: String, code: String) {
        val workDir = createTempDirectory("querydsl-kotlin-codegen")
        try {
            val source = workDir.resolve("$name.kt")
            source.writeText(code)
            val messages = ByteArrayOutputStream()
            val exitCode = K2JVMCompiler().exec(
                PrintStream(messages),
                source.toString(),
                "-d", workDir.resolve("classes").toString(),
                "-classpath", System.getProperty("java.class.path"),
                "-no-stdlib",
                "-no-reflect",
                "-nowarn",
            )
            if (exitCode != ExitCode.OK) {
                throw AssertionError("$name did not compile:\n$messages\n$code")
            }
        } finally {
            workDir.toFile().deleteRecursively()
        }
    }
}
