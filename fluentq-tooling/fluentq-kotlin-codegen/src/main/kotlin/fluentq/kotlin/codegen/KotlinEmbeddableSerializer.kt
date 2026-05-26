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

import fluentq.codegen.CodegenModule
import fluentq.codegen.EmbeddableSerializer
import fluentq.codegen.GeneratedAnnotationResolver
import fluentq.codegen.TypeMappings
import fluentq.core.types.Path
import fluentq.core.types.dsl.BeanPath
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlin.reflect.KClass

class KotlinEmbeddableSerializer @Inject constructor(
    mappings: TypeMappings,
    @Named(CodegenModule.KEYWORDS)
    keywords: Collection<String>,
    @Named(CodegenModule.GENERATED_ANNOTATION_CLASS)
    generatedAnnotationClass: Class<out Annotation> = GeneratedAnnotationResolver.resolveDefault()
) : KotlinEntitySerializer(mappings, keywords, generatedAnnotationClass), EmbeddableSerializer {
    override fun defaultSuperType(): KClass<out Path<*>> = BeanPath::class
}