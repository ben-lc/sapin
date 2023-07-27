package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.NaturalAreaTypeEntity
import com.github.ben_lc.sapin.repository.util.bindIfNotNull
import com.github.ben_lc.sapin.repository.util.bindIfNotNullOrEmpty
import com.github.ben_lc.sapin.repository.util.unless
import com.github.ben_lc.sapin.repository.util.where
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class NaturalAreaTypeRepository(private val databaseClient: DatabaseClient) {

  suspend fun findById(id: Int): NaturalAreaTypeEntity? =
      databaseClient
          .sql(
              """
              SELECT
                $COLUMNS
              FROM
                sapin.natural_area_type
              WHERE
                id = :id
                """)
          .bind("id", id)
          .map(MAPPER)
          .awaitSingleOrNull()

  suspend fun findAllByIdIn(ids: Collection<Int>): Flow<NaturalAreaTypeEntity> =
      if (ids.isEmpty()) emptyFlow()
      else
          databaseClient
              .sql(
                  """
                  SELECT
                    $COLUMNS
                  FROM
                    sapin.natural_area_type
                  WHERE
                    id IN (:ids)
                    """)
              .bind("ids", ids)
              .map(MAPPER)
              .flow()

  suspend fun findAll(
      name: String? = null,
      locationIds: Collection<Int>? = null,
      limit: Int = 10
  ): Flow<NaturalAreaTypeEntity> =
      databaseClient
          .sql(
              """
                SELECT
                  $COLUMNS
                  ${", name <-> :name AS dist" unless { name == null }}
                FROM
                  sapin.natural_area_type nat
                ${where { 
                  +"""EXISTS (
                        SELECT 1
                        FROM sapin.natural_area_type_location_asso natla
                        WHERE natla.type_id = nat.id AND loc_id IN (:locationIds)
                      )""".unless { locationIds.isNullOrEmpty() }
                }}
                ${"ORDER BY dist" unless { name == null }}
                LIMIT :limit
                """)
          .bindIfNotNull("name", name)
          .bindIfNotNullOrEmpty("locationIds", locationIds)
          .bind("limit", limit)
          .map(MAPPER)
          .flow()
}

private val MAPPER: (Row, RowMetadata) -> NaturalAreaTypeEntity = { row, _ ->
  NaturalAreaTypeEntity(
      id = row.get("id") as Int,
      name = row.get("name") as String,
      code = row.get("code") as String,
      description = row.get("description") as String?)
}

private const val COLUMNS = "id, name, code, description"
