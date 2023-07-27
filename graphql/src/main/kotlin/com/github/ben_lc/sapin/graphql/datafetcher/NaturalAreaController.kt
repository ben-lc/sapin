package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.NaturalArea
import com.github.ben_lc.sapin.graphql.type.NaturalAreaType
import com.github.ben_lc.sapin.repository.NaturalAreaRepository
import com.github.ben_lc.sapin.repository.NaturalAreaTypeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class NaturalAreaController(
    val naturalAreaRepo: NaturalAreaRepository,
    val naturalAreaTypeRepo: NaturalAreaTypeRepository
) {

  @QueryMapping
  suspend fun naturalAreaById(@Argument id: Int): Mono<NaturalArea> =
      naturalAreaRepo.findById(id)?.let { NaturalArea(it) }.toMono()

  @QueryMapping
  suspend fun searchNaturalAreas(
      @Argument name: String?,
      @Argument locationId: String,
      @Argument typeIds: List<String>?,
      @Argument limit: Int = 10
  ): Flux<NaturalArea> =
      naturalAreaRepo
          .findAll(locationId.toInt(), name, typeIds?.map { it.toInt() }, limit)
          .map { NaturalArea(it) }
          .asFlux()

  @QueryMapping
  suspend fun naturalAreasByGeolocation(
      @Argument longitude: Double,
      @Argument latitude: Double,
      @Argument typeIds: List<String>?,
      @Argument limit: Int = 10
  ): Flux<NaturalArea> =
      naturalAreaRepo
          .findAllByGeolocation(longitude, latitude, typeIds?.map { it.toInt() }, limit)
          .map { NaturalArea(it) }
          .asFlux()

  @BatchMapping
  suspend fun type(areas: List<NaturalArea>): Mono<Map<NaturalArea, NaturalAreaType>> {
    val types = naturalAreaTypeRepo.findAllByIdIn(areas.map { it.typeId })

    return areas
        .associateWith { area -> NaturalAreaType(types.first { it.id == area.typeId }) }
        .toMono()
  }
}
