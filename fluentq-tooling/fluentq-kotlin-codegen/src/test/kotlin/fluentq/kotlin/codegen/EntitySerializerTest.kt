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

import fluentq.codegen.Delegate
import fluentq.codegen.EntitySerializer
import fluentq.codegen.EntityType
import fluentq.codegen.GeneratedAnnotationResolver
import fluentq.codegen.Property
import fluentq.codegen.QueryTypeFactory
import fluentq.codegen.QueryTypeFactoryImpl
import fluentq.codegen.SimpleSerializerConfig
import fluentq.codegen.Supertype
import fluentq.codegen.TypeMappings
import fluentq.codegen.utils.JavaWriter
import fluentq.codegen.utils.model.ClassType
import fluentq.codegen.utils.model.SimpleType
import fluentq.codegen.utils.model.TypeCategory
import fluentq.codegen.utils.model.Types
import fluentq.core.annotations.Generated
import fluentq.core.annotations.PropertyType
import fluentq.kotlin.codegen.CompileUtils.assertCompiles
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

import java.io.StringWriter
import java.sql.Time
import java.util.*

class EntitySerializerTest {
    private var queryTypeFactory: QueryTypeFactory = QueryTypeFactoryImpl("Q", "", "")
    private val typeMappings: TypeMappings = KotlinTypeMappings()
    private val serializer: EntitySerializer = KotlinEntitySerializer(typeMappings, emptySet())
    private val writer = StringWriter()

    class Entity

    @Test
    fun javadocs_for_innerClass() {
        val entityType = EntityType(ClassType(Entity::class.java))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("QEntitySerializerTest_Entity is a FluentQ query type for Entity"))
        assertCompiles("QEntitySerializerTest_Entity", writer.toString())
    }

    @Test
    fun different_package() {
        queryTypeFactory = QueryTypeFactoryImpl("Q", "", ".gen")
        val entityType = EntityType(ClassType(Entity::class.java))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("class QEntitySerializerTest_Entity : EntityPathBase<EntitySerializerTest.Entity>"))
        assertCompiles("QEntitySerializerTest_Entity", writer.toString())
    }

    @Test
    fun no_package() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("class QEntity : EntityPathBase<Entity> {"))
        assertCompiles("QEntity", writer.toString())
    }

    @Test
    fun original_category() {
        val categoryToSuperClass: MutableMap<TypeCategory, String> = EnumMap(TypeCategory::class.java)
        categoryToSuperClass[TypeCategory.COMPARABLE] = "ComparablePath<Entity>"
        categoryToSuperClass[TypeCategory.ENUM] = "EnumPath<Entity>"
        categoryToSuperClass[TypeCategory.DATE] = "DatePath<Entity>"
        categoryToSuperClass[TypeCategory.DATETIME] = "DateTimePath<Entity>"
        categoryToSuperClass[TypeCategory.TIME] = "TimePath<Entity>"
        categoryToSuperClass[TypeCategory.NUMERIC] = "NumberPath<Entity>"
        categoryToSuperClass[TypeCategory.STRING] = "StringPath"
        categoryToSuperClass[TypeCategory.BOOLEAN] = "BooleanPath"
        for (entry in categoryToSuperClass.entries) {
            val type = SimpleType(entry.key, "Entity", "", "Entity", false, false)
            val entityType = EntityType(type)
            typeMappings.register(entityType, queryTypeFactory.create(entityType))
            serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
            Assertions.assertTrue(writer.toString().contains("class QEntity : " + entry.value + " {"), entry.toString())
        }
    }

    @Test
    fun correct_superclass() {
        val type = SimpleType(TypeCategory.ENTITY, "java.util.Locale", "java.util", "Locale", false, false)
        val entityType = EntityType(type)
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("class QLocale : EntityPathBase<Locale> {"))
        assertCompiles("QLocale", writer.toString())
    }

    @Test
    fun primitive_array() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        entityType.addProperty(Property(entityType, "bytes", ClassType(ByteArray::class.java)))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("val bytes: SimplePath<ByteArray>"))
        assertCompiles("QEntity", writer.toString())
    }

    @Test
    fun object_array() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        entityType.addProperty(Property(entityType, "tags", ClassType(TypeCategory.ARRAY, Array<String>::class.java)))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("val tags: ArrayPath<Array<String>, String>"))
        Assertions.assertTrue(writer.toString().contains("createArray(\"tags\", Array<String>::class.java)"))
        assertCompiles("QEntity", writer.toString())
    }

    @Test
    fun include() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        entityType.addProperty(Property(entityType, "b", ClassType(TypeCategory.BOOLEAN, Boolean::class.java)))
        entityType.addProperty(Property(entityType, "c", ClassType(TypeCategory.COMPARABLE, String::class.java)))
        //entityType.addProperty(new Property(entityType, "cu", new ClassType(TypeCategory.CUSTOM, PropertyType.class)));
        entityType.addProperty(Property(entityType, "d", ClassType(TypeCategory.DATE, Date::class.java)))
        entityType.addProperty(Property(entityType, "e", ClassType(TypeCategory.ENUM, PropertyType::class.java)))
        entityType.addProperty(Property(entityType, "dt", ClassType(TypeCategory.DATETIME, Date::class.java)))
        entityType.addProperty(Property(entityType, "i", ClassType(TypeCategory.NUMERIC, Int::class.java)))
        entityType.addProperty(Property(entityType, "s", ClassType(TypeCategory.STRING, String::class.java)))
        entityType.addProperty(Property(entityType, "t", ClassType(TypeCategory.TIME, Time::class.java)))
        entityType.addProperty(Property(entityType, "o", ClassType(TypeCategory.SIMPLE, Object::class.java)))
        entityType.addProperty(Property(entityType, "a", ClassType(TypeCategory.SIMPLE, Any::class.java)))
        val subType = EntityType(SimpleType(TypeCategory.ENTITY, "Entity2", "", "Entity2", false, false))
        subType.include(Supertype(type, entityType))
        typeMappings.register(subType, queryTypeFactory.create(subType))
        serializer.serialize(subType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        assertCompiles("QEntity2", writer.toString())
    }

    @Test
    fun properties() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        entityType.addProperty(Property(entityType, "b", ClassType(TypeCategory.BOOLEAN, Boolean::class.java)))
        entityType.addProperty(Property(entityType, "c", ClassType(TypeCategory.COMPARABLE, String::class.java)))
        //entityType.addProperty(new Property(entityType, "cu", new ClassType(TypeCategory.CUSTOM, PropertyType.class)));
        entityType.addProperty(Property(entityType, "d", ClassType(TypeCategory.DATE, Date::class.java)))
        entityType.addProperty(Property(entityType, "e", ClassType(TypeCategory.ENUM, PropertyType::class.java)))
        entityType.addProperty(Property(entityType, "dt", ClassType(TypeCategory.DATETIME, Date::class.java)))
        entityType.addProperty(Property(entityType, "i", ClassType(TypeCategory.NUMERIC, Int::class.java)))
        entityType.addProperty(Property(entityType, "s", ClassType(TypeCategory.STRING, String::class.java)))
        entityType.addProperty(Property(entityType, "t", ClassType(TypeCategory.TIME, Time::class.java)))
        entityType.addProperty(Property(entityType, "o", ClassType(TypeCategory.SIMPLE, Object::class.java)))
        entityType.addProperty(Property(entityType, "a", ClassType(TypeCategory.SIMPLE, Any::class.java)))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        assertCompiles("QEntity", writer.toString())
    }

    @Test
    fun superType() {
        val superType = EntityType(SimpleType(TypeCategory.ENTITY, "Entity2", "", "Entity2", false, false))
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type, setOf(Supertype(superType, superType)))
        typeMappings.register(superType, queryTypeFactory.create(superType))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("val _super: QEntity2 by lazy {\n    QEntity2(this)\n  }"))
        //CompileUtils.assertCompiles("QEntity", writer.toString());
    }


    @Test
    @Disabled //TODO: Implement delegates. Or document that extensions need to be used instead?
    fun delegates() {
        val type = SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false)
        val entityType = EntityType(type)
        val delegate = Delegate(type, type, "test", emptyList(), Types.STRING)
        entityType.addDelegate(delegate)
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        Assertions.assertTrue(writer.toString().contains("return Entity.test(this)"))
        assertCompiles("QEntity", writer.toString())
    }

    @Test
    fun defaultGeneratedAnnotation() {
        val entityType = EntityType(ClassType(Entity::class.java))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        val generatedSourceCode = writer.toString()
        Assertions.assertTrue(generatedSourceCode.contains("import $generatedAnnotationImport"))
        Assertions.assertTrue(generatedSourceCode.contains("@Generated(\"fluentq.kotlin.codegen.KotlinEntitySerializer\")\npublic class"))
        assertCompiles("QEntitySerializerTest_Entity", generatedSourceCode)
    }

    @Test
    fun customGeneratedAnnotation() {
        val entityType = EntityType(ClassType(Entity::class.java))
        typeMappings.register(entityType, queryTypeFactory.create(entityType))
        KotlinEntitySerializer(typeMappings, emptySet(), Generated::class.java).serialize(entityType, SimpleSerializerConfig.DEFAULT, JavaWriter(writer))
        val generatedSourceCode = writer.toString()
        Assertions.assertTrue(generatedSourceCode.contains("import " + Generated::class.java.name))
        Assertions.assertTrue(generatedSourceCode.contains("@${Generated::class.java.simpleName}(\"fluentq.kotlin.codegen.KotlinEntitySerializer\")\npublic class"))
        assertCompiles("QEntitySerializerTest_Entity", generatedSourceCode)
    }
}