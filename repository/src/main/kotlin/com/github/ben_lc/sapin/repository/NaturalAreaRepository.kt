package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.repository.util.bindIfNotNull
import com.github.ben_lc.sapin.repository.util.bindIfNotNullOrEmpty
import com.github.ben_lc.sapin.repository.util.unless
import com.github.ben_lc.sapin.repository.util.where
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class NaturalAreaRepository(private val databaseClient: DatabaseClient) {
  suspend fun findById(id: Int): NaturalAreaEntity? =
      databaseClient
          .sql(
              """
              SELECT
                $COLUMNS
              FROM
                sapin.natural_area
              WHERE
                id = :id
                """)
          .bind("id", id)
          .map(MAPPER)
          .awaitSingleOrNull()

  suspend fun findAll(
      locationId: Int,
      name: String? = null,
      typeIds: Collection<Int>? = null,
      limit: Int = 10
  ): Flow<NaturalAreaEntity> {
    return databaseClient
        .sql(
            """
              SELECT
                $COLUMNS
                ${", name <-> :name AS dist" unless  { name == null }}
              FROM
                sapin.natural_area na
              ${where {
                  and {
                    +"""EXISTS (
                          SELECT 1
                          FROM sapin.location loc
                          WHERE id = :locationId AND ST_Contains(loc.geom, na.geom)
                        )"""
                    +"type_id IN (:typeIds)".unless { typeIds.isNullOrEmpty() }
                  }
                }}
              ${"ORDER BY dist" unless { name == null }}
              LIMIT :limit
                """)
        .bind("locationId", locationId)
        .bindIfNotNull("name", name)
        .bindIfNotNullOrEmpty("typeIds", typeIds)
        .bind("limit", limit)
        .map(MAPPER)
        .flow()
  }

  suspend fun findAllByGeolocation(
      longitude: Double,
      latitude: Double,
      typeIds: Collection<Int>? = null,
      limit: Int = 10
  ): Flow<NaturalAreaEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $COLUMNS
              FROM
                sapin.natural_area
              ${where {
                  +"type_id IN (:typeIds)".unless { typeIds.isNullOrEmpty()  }
                }}
              ORDER BY
                geom <-> 'SRID=4326;POINT($longitude $latitude)'::geometry
              LIMIT
                :limit
                """)
          .bindIfNotNull("typeIds", typeIds)
          .bind("limit", limit)
          .map(MAPPER)
          .flow()
}

private val MAPPER: (Row, RowMetadata) -> NaturalAreaEntity = { row, _ ->
  NaturalAreaEntity(
      id = row.get("id") as Int,
      name = row.get("name") as String,
      domain = row.get("domain") as NaturalAreaEntity.Domain,
      typeId = row.get("type_id") as Int,
      srcId = row.get("src_id") as String,
      description = row.get("description") as String?)
}

private const val COLUMNS = "id, name, domain, type_id, src_id, description"
