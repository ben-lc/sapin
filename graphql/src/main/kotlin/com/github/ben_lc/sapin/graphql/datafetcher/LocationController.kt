package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.Location
import com.github.ben_lc.sapin.repository.LocationRepository
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
class LocationController(private val locationRepo: LocationRepository) {

  @QueryMapping
  suspend fun locationById(@Argument id: Int): Mono<Location> {
    return locationRepo.findById(id)?.let { Location(it) }.toMono()
  }

  @QueryMapping
  suspend fun locationsBySimilarName(
      @Argument name: String,
      @Argument level: Int,
      @Argument limit: Int
  ): Flux<Location> {
    return locationRepo.findAllBySimilarName(name, level, limit).map { Location(it) }.asFlux()
  }

  @QueryMapping
  suspend fun locationsByGeolocation(
      @Argument longitude: Double,
      @Argument latitude: Double,
      @Argument level: Int?
  ): Flux<Location> {
    return if (level == null)
        locationRepo.findAllByGeolocation(longitude, latitude).map { Location(it) }.asFlux()
    else
        locationRepo
            .findAllByGeolocationAndLevel(longitude, latitude, level)
            .map { Location(it) }
            .asFlux()
  }

  @BatchMapping
  suspend fun children(locations: List<Location>): Mono<Map<Location, List<Location>>> =
      locationRepo
          .findChildrenByIdIn(locations.map { it.id.toInt() })
          .toList()
          .groupBy({ children -> locations.first { it.id == children.parentId.toString() } }) {
            Location(it)
          }
          .toMono()

  @BatchMapping
  suspend fun parent(locations: List<Location>): Mono<Map<Location, Location>> {
    val parents =
        locationRepo
            .findAllById(locations.filter { it.parentId != null }.map { it.parentId!!.toInt() })
            .map { Location(it) }
            .toList()

    return locations
        .associateWith { child -> parents.first { it.id.toInt() == child.parentId } }
        .toMono()
  }
  @SchemaMapping
  suspend fun parents(location: Location): Flux<Location> =
      locationRepo.findParentsById(location.id.toInt()).map { Location(it) }.asFlux()
}
