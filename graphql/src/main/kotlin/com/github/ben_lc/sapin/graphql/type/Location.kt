package com.github.ben_lc.sapin.graphql.type

import com.github.ben_lc.sapin.model.LocationEntity

data class Location(
    val id: String,
    val name: String,
    val level: Short,
    val isoId: String? = null,
    val levelName: String? = null,
    val levelNameEn: String? = null,
    val parentId: String? = null
) {
  constructor(
      location: LocationEntity
  ) : this(
      location.id.toString(),
      location.name,
      location.level,
      location.isoId,
      location.levelName,
      location.levelNameEn,
      location.parentId?.toString())
}
