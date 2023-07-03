package com.github.ben_lc.sapin.model

/** Data class for Location */
data class LocationEntity(
    val id: Int? = null,
    val parentId: Int? = null,
    val name: String,
    val level: Short,
    val levelName: String? = null,
    val levelNameEn: String? = null,
    val isoId: String? = null
)
