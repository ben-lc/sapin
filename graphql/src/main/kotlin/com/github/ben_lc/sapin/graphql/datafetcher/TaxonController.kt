package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.Taxon
import com.github.ben_lc.sapin.graphql.type.TaxonScientificName
import com.github.ben_lc.sapin.graphql.type.TaxonVernacularName
import com.github.ben_lc.sapin.repository.TaxonRepository
import com.github.ben_lc.sapin.repository.TaxonScientificNameRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class TaxonController(
    private val taxonRepo: TaxonRepository,
    private val taxonNameRepo: TaxonScientificNameRepository
) {

  @QueryMapping
  suspend fun taxonById(@Argument id: Int): Mono<Taxon> =
      taxonRepo.findById(id)?.let { Taxon(it) }.toMono()

  @QueryMapping
  suspend fun taxaByIds(@Argument ids: List<Int>): Flux<Taxon> =
      taxonRepo.findAllByIdIn(ids).map { Taxon(it) }.asFlux()

  @BatchMapping
  suspend fun scientificNames(taxons: List<Taxon>): Mono<Map<Taxon, List<TaxonScientificName>>> =
      taxonNameRepo
          .findAllByTaxonIdIn(taxons.map { it.id.toInt() })
          .toList()
          .map { TaxonScientificName(it) }
          .groupBy { name -> taxons.first { it.id == name.taxonId } }
          .toMono()

  @BatchMapping
  suspend fun vernacularNames(taxons: List<Taxon>): Mono<Map<Taxon, List<TaxonVernacularName>>> =
      taxonRepo
          .findVernacularNamesByIdIn(taxons.map { it.id.toInt() })
          .toList()
          .map { TaxonVernacularName(it) }
          .groupBy { name -> taxons.first { it.id == name.taxonId } }
          .toMono()

  @BatchMapping
  suspend fun children(taxons: List<Taxon>): Mono<Map<Taxon, List<Taxon>>> =
      taxonRepo
          .findChildrenByIdIn(taxons.map { it.id.toInt() })
          .toList()
          .groupBy({ child -> taxons.first { it.id == child.parentId.toString() } }) { Taxon(it) }
          .toMono()

  @BatchMapping
  suspend fun parent(taxons: List<Taxon>): Mono<Map<Taxon, Taxon?>> {
    val parents =
        taxonRepo.findAllByIdIn(taxons.filter { it.parentId != null }.map { it.parentId!!.toInt() })

    return taxons
        .associateWith { child ->
          parents.firstOrNull { it.id.toString() == child.parentId }?.let { Taxon(it) }
        }
        .toMono()
  }

  @SchemaMapping
  suspend fun parents(taxon: Taxon): Flux<Taxon> =
      taxonRepo.findParentsById(taxon.id.toInt()).map { Taxon(it) }.asFlux()
}
