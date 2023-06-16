package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : CoroutineCrudRepository<LocationEntity, Int> {

  @Query(
      """SELECT
           name <-> :name AS dist,
           loc_id,
           level,
           name,
           iso_id,
           level_local_name,
           level_local_name_en
         FROM
           sapin.location
         WHERE
           level = :level
         ORDER BY
           dist LIMIT :size
      """)
  suspend fun findAllBySimilarName(name: String, level: Int, size: Int? = 10): Flow<LocationEntity>

  @Query(
      """SELECT
           loc_id,
           level,
           name,
           iso_id,
           level_local_name,
           level_local_name_en
         FROM
           sapin.location
         WHERE
           ST_Contains(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
      """)
  suspend fun findAllByGeolocation(longitude: Double, latitude: Double): Flow<LocationEntity>

  @Query(
      """SELECT
           loc_id,
           level,
           name,
           iso_id,
           level_local_name,
           level_local_name_en
         FROM
           sapin.location
         WHERE
           ST_Contains(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
           AND level = :level
     """)
  suspend fun findAllByGeolocationAndLevel(
      longitude: Double,
      latitude: Double,
      level: Int
  ): Flow<LocationEntity>
}
