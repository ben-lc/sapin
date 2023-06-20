package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.Location
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class LocationRepository(private val databaseClient: DatabaseClient) {

  suspend fun findById(id: Int): Location? =
      databaseClient
          .sql(
              """
                SELECT
                  loc_id,
                  parent_loc_id,
                  level,
                  name,
                  iso_id,
                  level_local_name,
                  level_local_name_en
                FROM
                  sapin.location
                WHERE
                  loc_id = :id
                  """)
          .bind("id", id)
          .map(MAPPER)
          .awaitSingleOrNull()
  suspend fun findAllBySimilarName(name: String, level: Int, size: Int = 10): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                name <-> :name AS dist,
                loc_id,
                parent_loc_id,
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
                dist
              LIMIT
                :size
                """)
          .bind("name", name)
          .bind("level", level)
          .bind("size", size)
          .map(MAPPER)
          .flow()

  suspend fun findAllByGeolocation(longitude: Double, latitude: Double): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                loc_id,
                parent_loc_id,
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
          .bind("longitude", longitude)
          .bind("latitude", latitude)
          .map(MAPPER)
          .flow()

  suspend fun findAllByGeolocationAndLevel(
      longitude: Double,
      latitude: Double,
      level: Int
  ): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                loc_id,
                parent_loc_id,
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
          .bind("longitude", longitude)
          .bind("latitude", latitude)
          .bind("level", level)
          .map(MAPPER)
          .flow()

  suspend fun findParentsById(id: Int): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                loc_id,
                parent_loc_id,
                level,
                name,
                iso_id,
                level_local_name,
                level_local_name_en
              FROM
                sapin.location
              WHERE
                tree_path @> (SELECT tree_path FROM sapin.location WHERE loc_id = :id)
                AND loc_id != :id
              ORDER BY level
              """)
          .bind("id", id)
          .map(MAPPER)
          .flow()

  suspend fun findParentByIdIn(ids: Collection<Int>): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                l2.loc_id,
                l2.parent_loc_id,
                l2.level,
                l2.name,
                l2.iso_id,
                l2.level_local_name,
                l2.level_local_name_en
              FROM
                sapin.location l1
                JOIN sapin.location l2 ON l1.parent_loc_id = l2.loc_id
              WHERE
                l1.loc_id IN (:ids)
                """)
          .bind("ids", ids)
          .map(MAPPER)
          .flow()

  suspend fun findChildrenByIdIn(ids: Collection<Int>): Flow<Location> =
      databaseClient
          .sql(
              """
              SELECT
                loc_id,
                parent_loc_id,
                level,
                name,
                iso_id,
                level_local_name,
                level_local_name_en
              FROM
                sapin.location
              WHERE
                parent_loc_id IN (:ids)
                """)
          .bind("ids", ids)
          .map(MAPPER)
          .flow()
}

val MAPPER: (Row, RowMetadata) -> Location = { row, _ ->
  Location(
      id = row.get("loc_id") as Int,
      parentId = row.get("parent_loc_id") as Int?,
      name = row.get("name") as String,
      level = row.get("level") as Short,
      isoId = row.get("iso_id") as String?,
      levelLocalName = row.get("level_local_name") as String?,
      levelLocalNameEn = row.get("level_local_name_en") as String?)
}
