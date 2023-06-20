package com.github.ben_lc.sapin.graphql.type

import com.github.ben_lc.sapin.model.Location

data class LocationType(
    val id: String,
    val name: String,
    val level: Short,
    val isoId: String? = null,
    val levelLocalName: String? = null,
    val levelLocalNameEn: String? = null
) {
  constructor(
      location: Location
  ) : this(
      location.id.toString(),
      location.name,
      location.level,
      location.isoId,
      location.levelLocalName,
      location.levelLocalNameEn)
}
