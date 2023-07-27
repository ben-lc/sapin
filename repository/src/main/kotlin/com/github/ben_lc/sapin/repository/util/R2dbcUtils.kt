package com.github.ben_lc.sapin.repository.util

import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec

/**
 * Extension for [GenericExecuteSpec] that directly supports binding of both null and non null
 * values.
 */
inline fun <reified T> GenericExecuteSpec.bindNullable(name: String, value: Any?) =
    if (value == null) this.bindNull(name, T::class.java) else this.bind(name, value)

fun GenericExecuteSpec.bindIfNotNull(name: String, value: Any?) =
    if (value == null) this else this.bind(name, value)

fun GenericExecuteSpec.bindIfNotNullOrEmpty(name: String, value: Collection<Any>?) =
    if (value.isNullOrEmpty()) this else this.bind(name, value)
