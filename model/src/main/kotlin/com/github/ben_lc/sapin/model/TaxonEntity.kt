package com.github.ben_lc.sapin.model

data class TaxonEntity(
    val id: Int,
    val srcNameId: String,
    val parentId: Int? = null,
    val rank: Rank,
    val acceptedName: String
) {

  enum class Rank {
    KINGDOM,
    PHYLUM,
    CLASS,
    ORDER,
    FAMILY,
    GENUS,
    SPECIES,
    SUBSPECIES,
    VARIETY,
    FORM
  }

  data class VernacularName(val name: String, val language: String, val taxonId: Int)
}
