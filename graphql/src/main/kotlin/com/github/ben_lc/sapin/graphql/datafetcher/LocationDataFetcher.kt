package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.generated.graphql.types.Location
import com.github.ben_lc.sapin.graphql.type.toGraphqlType
import com.github.ben_lc.sapin.repository.LocationRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@DgsComponent
class LocationDataFetcher(private val locationRepo: LocationRepository) {

  @DgsQuery
  suspend fun locationById(@InputArgument id: Int): Location? {
    return locationRepo.findById(id)?.toGraphqlType()
  }
  @DgsQuery
  suspend fun locationsBySimilarName(
      @InputArgument name: String,
      @InputArgument level: Int,
      @InputArgument limit: Int
  ): List<Location> {
    return locationRepo.findAllBySimilarName(name, level, limit).map { it.toGraphqlType() }.toList()
  }

  @DgsQuery
  suspend fun locationsByGeolocation(
      @InputArgument longitude: Double,
      @InputArgument latitude: Double,
      @InputArgument level: Int?
  ): List<Location> {
    return if (level == null)
        locationRepo.findAllByGeolocation(longitude, latitude).map { it.toGraphqlType() }.toList()
    else
        locationRepo
            .findAllByGeolocationAndLevel(longitude, latitude, level)
            .map { it.toGraphqlType() }
            .toList()
  }
}
