package com.github.ben_lc.sapin.graphql.type

import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity

data class Taxon(
    val id: String,
    val srcNameId: String,
    val rank: TaxonEntity.Rank,
    val acceptedName: String,
    val parentId: String? = null
) {
  constructor(
      taxon: TaxonEntity
  ) : this(
      taxon.id.toString(),
      taxon.srcNameId,
      taxon.rank,
      taxon.acceptedName,
      taxon.parentId?.toString())
}

data class TaxonScientificName(
    val id: String,
    override val taxonId: String,
    val srcId: String,
    val taxonomicStatus: TaxonScientificNameEntity.TaxonomicStatus,
    override val name: String,
    val acceptedNameId: String?
) : TaxonName() {
  constructor(
      scientificName: TaxonScientificNameEntity
  ) : this(
      scientificName.id.toString(),
      scientificName.taxonId.toString(),
      scientificName.srcId,
      scientificName.taxonomicStatus,
      scientificName.name,
      scientificName.acceptedNameId?.toString())
}

data class TaxonVernacularName(
    override val taxonId: String,
    override val name: String,
    val language: String
) : TaxonName() {
  constructor(
      vernacularName: TaxonEntity.VernacularName
  ) : this(vernacularName.taxonId.toString(), vernacularName.name, vernacularName.language)
}

abstract class TaxonName {
  abstract val taxonId: String
  abstract val name: String
}
