package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.LocationEntity
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class LocationRepository(private val databaseClient: DatabaseClient) {

  suspend fun findById(id: Int): LocationEntity? =
      databaseClient
          .sql(
              """
                SELECT
                  $SELECT_COLS
                FROM
                  sapin.location
                WHERE
                  id = :id
                  """)
          .bind("id", id)
          .map(MAPPER)
          .awaitSingleOrNull()

  suspend fun findAllById(ids: Collection<Int>): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                id IN (:ids)
                """)
          .bind("ids", ids)
          .map(MAPPER)
          .flow()
  suspend fun findAllBySimilarName(name: String, level: Int, size: Int = 10): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                name <-> :name AS dist,
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                level = :level
              ORDER BY
                dist
              LIMIT
                :size
                """)
          .bind("name", name)
          .bind("level", level)
          .bind("size", size)
          .map(MAPPER)
          .flow()

  suspend fun findAllByGeolocation(longitude: Double, latitude: Double): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                ST_Contains(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
                """)
          .bind("longitude", longitude)
          .bind("latitude", latitude)
          .map(MAPPER)
          .flow()

  suspend fun findAllByGeolocationAndLevel(
      longitude: Double,
      latitude: Double,
      level: Int
  ): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                ST_Contains(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
                AND level = :level
                """)
          .bind("longitude", longitude)
          .bind("latitude", latitude)
          .bind("level", level)
          .map(MAPPER)
          .flow()

  suspend fun findParentsById(id: Int): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                tree_path @> (SELECT tree_path FROM sapin.location WHERE id = :id)
                AND id != :id
              ORDER BY level
              """)
          .bind("id", id)
          .map(MAPPER)
          .flow()

  suspend fun findChildrenByIdIn(ids: Collection<Int>): Flow<LocationEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.location
              WHERE
                parent_id IN (:ids)
                """)
          .bind("ids", ids)
          .map(MAPPER)
          .flow()
}

private val MAPPER: (Row, RowMetadata) -> LocationEntity = { row, _ ->
  LocationEntity(
      id = row.get("id") as Int,
      parentId = row.get("parent_id") as Int?,
      name = row.get("name") as String,
      level = row.get("level") as Short,
      isoId = row.get("iso_id") as String?,
      levelLocalName = row.get("level_local_name") as String?,
      levelLocalNameEn = row.get("level_local_name_en") as String?)
}

private val SELECT_COLS =
    """
  id,
  parent_id,
  level,
  name,
  iso_id,
  level_local_name,
  level_local_name_en
"""
        .trimIndent()
