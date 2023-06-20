package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.LocationType
import com.github.ben_lc.sapin.repository.LocationRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class LocationController(private val locationRepo: LocationRepository) {

  @QueryMapping
  suspend fun locationById(@Argument id: Int): Mono<LocationType> {
    return locationRepo.findById(id)?.let { LocationType(it) }.toMono()
  }

  @QueryMapping
  suspend fun locationsBySimilarName(
      @Argument name: String,
      @Argument level: Int,
      @Argument limit: Int
  ): Flux<LocationType> {
    return locationRepo.findAllBySimilarName(name, level, limit).map { LocationType(it) }.asFlux()
  }

  @QueryMapping
  suspend fun locationsByGeolocation(
      @Argument longitude: Double,
      @Argument latitude: Double,
      @Argument level: Int?
  ): Flux<LocationType> {
    return if (level == null)
        locationRepo.findAllByGeolocation(longitude, latitude).map { LocationType(it) }.asFlux()
    else
        locationRepo
            .findAllByGeolocationAndLevel(longitude, latitude, level)
            .map { LocationType(it) }
            .asFlux()
  }
}
