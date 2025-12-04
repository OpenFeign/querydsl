package com.querydsl.example.ksp

import java.math.BigDecimal

data class Money(val value: BigDecimal) : Number(), Comparable<Money> {

    override fun toByte(): Byte = value.toByte()
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toInt(): Int = value.toInt()
    override fun toLong(): Long = value.toLong()
    override fun toShort(): Short = value.toShort()

    override fun compareTo(other: Money): Int = this.value.compareTo(other.value)
}

