package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.NaturalAreaType
import com.github.ben_lc.sapin.repository.NaturalAreaTypeRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class NaturalAreaTypeController(private val naturalAreaTypeRepo: NaturalAreaTypeRepository) {

  @QueryMapping
  suspend fun naturalAreaTypeById(@Argument id: Int): Mono<NaturalAreaType> =
      naturalAreaTypeRepo.findById(id)?.let { NaturalAreaType(it) }.toMono()

  @QueryMapping
  suspend fun searchNaturalAreaTypes(
      @Argument name: String,
      @Argument locationIds: List<String>,
      @Argument limit: Int
  ): Flux<NaturalAreaType> =
      naturalAreaTypeRepo
          .findAll(name, locationIds.map { it.toInt() }, limit)
          .map { NaturalAreaType(it) }
          .asFlux()
}
