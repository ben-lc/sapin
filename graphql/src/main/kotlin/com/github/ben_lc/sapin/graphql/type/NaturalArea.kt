package com.github.ben_lc.sapin.graphql.type

import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.model.NaturalAreaTypeEntity

data class NaturalArea(
    val id: String,
    val name: String,
    val description: String?,
    val typeId: Int
) {
  constructor(
      natureArea: NaturalAreaEntity
  ) : this(natureArea.id.toString(), natureArea.name, natureArea.description, natureArea.typeId)
}

data class NaturalAreaType(
    val id: String,
    val name: String,
    val code: String,
    val description: String?
) {
  constructor(
      naturalAreaType: NaturalAreaTypeEntity
  ) : this(
      naturalAreaType.id.toString(),
      naturalAreaType.name,
      naturalAreaType.code,
      naturalAreaType.description)
}
