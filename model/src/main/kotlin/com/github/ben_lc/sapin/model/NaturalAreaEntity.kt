package com.github.ben_lc.sapin.model

data class NaturalAreaEntity(
    val id: Int,
    val name: String,
    val typeId: Int,
    val srcId: String?,
    val description: String?
) {
  enum class Domain {
    TERRESTRIAL,
    MARINE
  }
}
