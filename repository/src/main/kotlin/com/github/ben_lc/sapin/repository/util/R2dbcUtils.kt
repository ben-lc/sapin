package com.github.ben_lc.sapin.repository.util

import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec

/**
 * Extension for [GenericExecuteSpec] that directly supports binding of both null and non null
 * values.
 */
inline fun <reified T> GenericExecuteSpec.bindNullable(index: Int, value: Any?) =
    if (value == null) this.bindNull(index, T::class.java) else this.bind(index, value)

inline fun <reified T> GenericExecuteSpec.bindIfNotNull(name: String, value: Any?) =
    if (value == null) this else this.bind(name, value)
