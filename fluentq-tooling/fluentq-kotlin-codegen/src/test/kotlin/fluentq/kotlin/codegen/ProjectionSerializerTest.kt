/*
 * Copyright 2021, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.kotlin.codegen

import fluentq.codegen.EntityType
import fluentq.codegen.GeneratedAnnotationResolver
import fluentq.codegen.ProjectionSerializer
import fluentq.codegen.SimpleSerializerConfig
import fluentq.codegen.utils.JavaWriter
import fluentq.codegen.utils.model.Constructor
import fluentq.codegen.utils.model.Parameter
import fluentq.codegen.utils.model.SimpleType
import fluentq.codegen.utils.model.Type
import fluentq.codegen.utils.model.TypeCategory
import fluentq.codegen.utils.model.Types
import fluentq.core.annotations.Generated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

import java.io.StringWriter
import java.io.Writer
import java.util.*

class ProjectionSerializerTest {
    @Test
    fun constructors() {
        val typeModel: Type = SimpleType(TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false)
        val type = EntityType(typeModel)

        // constructor
        val firstName = Parameter("firstName", Types.STRING)
        val lastName = Parameter("lastName", Types.STRING)
        val age = Parameter("age", Types.INTEGER)
        type.addConstructor(Constructor(Arrays.asList(firstName, lastName, age)))
        val writer: Writer = StringWriter()
        val serializer: ProjectionSerializer = KotlinProjectionSerializer(KotlinTypeMappings())
        serializer.serialize(type, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("firstName: Expression<String>"))
        Assertions.assertTrue(writer.toString().contains("lastName: Expression<String>"))
        Assertions.assertTrue(writer.toString().contains("age: Expression<Int>"))
    }

    @Test
    fun defaultGeneratedAnnotation() {
        val typeModel: Type = SimpleType(TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false)
        val type = EntityType(typeModel)
        val writer: Writer = StringWriter()
        val serializer: ProjectionSerializer = KotlinProjectionSerializer(KotlinTypeMappings())
        serializer.serialize(type, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        val generatedSource = writer.toString()
        assertThat(generatedSource).contains("import $generatedAnnotationImport")
        assertThat(generatedSource).contains("@Generated(\"fluentq.kotlin.codegen.KotlinProjectionSerializer\")\npublic class")
    }

    @Test
    fun customGeneratedAnnotation() {
        val typeModel: Type = SimpleType(TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false)
        val type = EntityType(typeModel)
        val writer: Writer = StringWriter()
        val serializer: ProjectionSerializer = KotlinProjectionSerializer(KotlinTypeMappings(), Generated::class.java)
        serializer.serialize(type, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        val generatedSource = writer.toString()
        assertThat(generatedSource).contains("import fluentq.core.annotations.Generated")
        assertThat(generatedSource).contains("@Generated(\"fluentq.kotlin.codegen.KotlinProjectionSerializer\")\npublic class")
    }
}