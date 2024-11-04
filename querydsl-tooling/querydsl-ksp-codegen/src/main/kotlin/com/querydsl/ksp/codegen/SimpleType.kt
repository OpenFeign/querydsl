package com.querydsl.ksp.codegen

import com.querydsl.core.types.dsl.ArrayPath
import com.querydsl.core.types.dsl.BooleanPath
import com.querydsl.core.types.dsl.ComparablePath
import com.querydsl.core.types.dsl.DatePath
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.SimplePath
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.core.types.dsl.TimePath
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.NClob
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import java.util.Calendar
import java.util.Currency
import java.util.TimeZone
import java.util.UUID
import kotlin.reflect.KClass

sealed interface SimpleType {
    val className: ClassName
    val pathClassName: ClassName
    val pathTypeName: TypeName
    fun render(name: String): PropertySpec

    class Array(
        private val collectionType: ClassName,
        private val singleType: ClassName
    ) : SimpleType {
        override val className = collectionType
        override val pathClassName = ArrayPath::class.asClassName()
        override val pathTypeName = ArrayPath::class.asClassName().parameterizedBy(collectionType, singleType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createArray(\"$name\", ${collectionType}::class.java)")
                .build()
        }
    }

    class Simple(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = SimplePath::class.asClassName()
        override val pathTypeName = SimplePath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name,pathTypeName)
                .initializer("createSimple(\"$name\", ${innerType}::class.java)")
                .build()
        }
    }

    class Comparable(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = ComparablePath::class.asClassName()
        override val pathTypeName = ComparablePath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createComparable(\"$name\", ${innerType}::class.java)")
                .build()
        }
    }

    class QNumber(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = NumberPath::class.asClassName()
        override val pathTypeName = NumberPath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createNumber(\"$name\", ${innerType}::class.javaObjectType)")
                .build()
        }
    }

    class Date(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = DatePath::class.asClassName()
        override val pathTypeName = DatePath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createDate(\"$name\", ${innerType}::class.java)")
                .build()
        }
    }

    class DateTime(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = DateTimePath::class.asClassName()
        override val pathTypeName = DateTimePath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createDateTime(\"$name\", ${innerType}::class.java)")
                .build()
        }
    }

    class Time(private val innerType: ClassName) : SimpleType {
        override val className = innerType
        override val pathClassName = TimePath::class.asClassName()
        override val pathTypeName = TimePath::class.asClassName().parameterizedBy(innerType)

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createTime(\"$name\", ${innerType}::class.java)")
                .build()
        }
    }

    object QString : SimpleType {
        override val className = String::class.asClassName()
        override val pathClassName = StringPath::class.asClassName()
        override val pathTypeName = StringPath::class.asTypeName()

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createString(\"$name\")")
                .build()
        }
    }

    object QBoolean : SimpleType {
        override val className = Boolean::class.asClassName()
        override val pathClassName = BooleanPath::class.asClassName()
        override val pathTypeName = BooleanPath::class.asTypeName()

        override fun render(name: String): PropertySpec {
            return PropertySpec
                .builder(name, pathTypeName)
                .initializer("createBoolean(\"$name\")")
                .build()
        }
    }

    object Mapper {
        private val typeMap: Map<ClassName, SimpleType> = mutableMapOf<KClass<*>, SimpleType>()
            .apply {
                this[Any::class] = Simple(Any::class.asClassName())
                this[Char::class] = Comparable(Char::class.asClassName())
                this[String::class] = QString
                this[Boolean::class] = QBoolean

                this[Byte::class] = QNumber(Byte::class.asClassName())
                this[UByte::class] = QNumber(Byte::class.asClassName())
                this[Short::class] = QNumber(Short::class.asClassName())
                this[UShort::class] = QNumber(Short::class.asClassName())
                this[Int::class] = QNumber(Int::class.asClassName())
                this[UInt::class] = QNumber(Int::class.asClassName())
                this[Long::class] = QNumber(Long::class.asClassName())
                this[ULong::class] = QNumber(Long::class.asClassName())
                this[Float::class] = QNumber(Float::class.asClassName())
                this[Double::class] = QNumber(Double::class.asClassName())

                this[BigInteger::class] = QNumber(BigInteger::class.asClassName())
                this[BigDecimal::class] = QNumber(BigDecimal::class.asClassName())
                this[UUID::class] = Comparable(UUID::class.asClassName())

                this[LocalDate::class] = Date(LocalDate::class.asClassName())
                this[ZonedDateTime::class] = DateTime(ZonedDateTime::class.asClassName())
                this[LocalDateTime::class] = DateTime(LocalDateTime::class.asClassName())
                this[LocalTime::class] = Time(LocalTime::class.asClassName())
                this[Locale::class] = Simple(Locale::class.asClassName())
                this[ByteArray::class] = Array(ByteArray::class.asClassName(), Byte::class.asClassName())
                this[CharArray::class] = Array(CharArray::class.asClassName(), Char::class.asClassName())
                this[ZoneId::class] = Simple(ZoneId::class.asClassName())
                this[ZoneOffset::class] = Comparable(ZoneOffset::class.asClassName())
                this[Year::class] = Comparable(Year::class.asClassName())
                this[OffsetDateTime::class] = DateTime(OffsetDateTime::class.asClassName())
                this[Instant::class] = DateTime(Instant::class.asClassName())
                this[java.util.Date::class] = DateTime(java.util.Date::class.asClassName())
                this[Calendar::class] = DateTime(Calendar::class.asClassName())
                this[java.sql.Date::class] = Date(java.sql.Date::class.asClassName())
                this[java.sql.Time::class] = Time(java.sql.Time::class.asClassName())
                this[Timestamp::class] = DateTime(Timestamp::class.asClassName())
                this[Duration::class] = Comparable(Duration::class.asClassName())
                this[Blob::class] = Simple(Blob::class.asClassName())
                this[Clob::class] = Simple(Clob::class.asClassName())
                this[NClob::class] = Simple(NClob::class.asClassName())
                this[Currency::class] = Simple(Currency::class.asClassName())
                this[TimeZone::class] = Simple(TimeZone::class.asClassName())
                this[URL::class] = Simple(URL::class.asClassName())
            }
            .mapKeys { it.key.asClassName() }

        fun get(className: ClassName): SimpleType? {
            return typeMap[className]
        }
    }
}
