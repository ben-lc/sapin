package com.github.ben_lc.sapin.model

data class TaxonScientificNameEntity(
    val id: Int,
    val taxonId: Int,
    val srcId: String,
    val taxonomicStatus: TaxonomicStatus,
    val name: String,
    val acceptedNameId: Int? = null
) {
  enum class TaxonomicStatus {
    ACCEPTED,
    DOUBTFUL,
    SYNONYM
  }
}
