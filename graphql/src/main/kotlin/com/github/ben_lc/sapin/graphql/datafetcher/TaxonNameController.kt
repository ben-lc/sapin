package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.Taxon
import com.github.ben_lc.sapin.graphql.type.TaxonName
import com.github.ben_lc.sapin.graphql.type.TaxonScientificName
import com.github.ben_lc.sapin.graphql.type.TaxonVernacularName
import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.repository.TaxonRepository
import com.github.ben_lc.sapin.repository.TaxonScientificNameRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.asFlux
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class TaxonNameController(
    private val taxonRepo: TaxonRepository,
    private val taxonNameRepo: TaxonScientificNameRepository
) {

  @OptIn(FlowPreview::class)
  @QueryMapping
  suspend fun searchTaxonNames(
      @Argument name: String,
      @Argument rank: TaxonEntity.Rank?,
      @Argument gteRank: TaxonEntity.Rank?,
      @Argument includeScientificName: Boolean,
      @Argument includeVernacularNameLang: String?,
      @Argument limit: Int = 10
  ): Flux<TaxonName> {
    val scientificNames =
        if (includeScientificName) {
          taxonNameRepo.findAllBySimilarName(name, rank, gteRank, limit).map {
            TaxonScientificName(it)
          }
        } else emptyFlow()
    val vernacularNames =
        if (includeVernacularNameLang != null) {
          taxonRepo
              .findVernacularNamesBySimilarName(
                  name, includeVernacularNameLang, rank, gteRank, limit)
              .map { TaxonVernacularName(it) }
        } else emptyFlow()
    return flowOf(scientificNames, vernacularNames).flattenConcat().asFlux()
  }

  @BatchMapping(typeName = "TaxonVernacularName", field = "taxon")
  suspend fun taxonFromVernacularName(
      taxonNames: List<TaxonVernacularName>
  ): Mono<Map<TaxonVernacularName, Taxon>> {
    val taxons = taxonRepo.findAllByIdIn(taxonNames.map { it.taxonId.toInt() })
    return taxonNames
        .associateWith { taxonName ->
          Taxon(taxons.first { it.id.toString() == taxonName.taxonId })
        }
        .toMono()
  }

  @BatchMapping(typeName = "TaxonScientificName", field = "taxon")
  suspend fun taxonFromScientificName(
      taxonNames: List<TaxonScientificName>
  ): Mono<Map<TaxonScientificName, Taxon>> {
    val taxons = taxonRepo.findAllByIdIn(taxonNames.map { it.taxonId.toInt() })
    return taxonNames
        .associateWith { taxonName ->
          Taxon(taxons.first { it.id.toString() == taxonName.taxonId })
        }
        .toMono()
  }

  @BatchMapping(typeName = "TaxonScientificName", field = "acceptedName")
  suspend fun acceptedName(
      taxonNames: List<TaxonScientificName>
  ): Mono<Map<TaxonScientificName, TaxonScientificName?>> {
    val acceptedNames =
        taxonNameRepo.findAllByIdIn(
            taxonNames.filter { it.acceptedNameId != null }.map { it.acceptedNameId!!.toInt() })

    return taxonNames
        .associateWith { synonym ->
          acceptedNames
              .firstOrNull { it.id.toString() == synonym.acceptedNameId }
              ?.let { TaxonScientificName(it) }
        }
        .toMono()
  }
}
