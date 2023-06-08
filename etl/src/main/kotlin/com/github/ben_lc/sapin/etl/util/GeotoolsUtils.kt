package com.github.ben_lc.sapin.etl.util

import org.opengis.feature.simple.SimpleFeature

inline fun <reified T> SimpleFeature.getAttribute(name: String?): T? {
  if (name.isNullOrEmpty()) return null
  val value = this.getAttribute(name)
  return if (value == null) null else value as T
}

inline fun <reified T> SimpleFeature.getAttributeOrNullForValue(name: String?, nullValue: Any): T? {
  val value = this.getAttribute<T>(name)
  return if (value != null && value == nullValue) null else value
}
