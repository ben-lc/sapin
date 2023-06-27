package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity
import com.github.ben_lc.sapin.repository.util.bindIfNotNull
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class TaxonScientificNameRepository(private val databaseClient: DatabaseClient) {

  suspend fun findById(id: Int): TaxonScientificNameEntity? =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_COLS
              FROM
                sapin.taxon_scientific_name
              WHERE
                id = :id
                """)
          .bind("id", id)
          .map(MAPPER)
          .awaitSingleOrNull()

  suspend fun findAllByIdIn(ids: Collection<Int>): Flow<TaxonScientificNameEntity> =
      if (ids.isEmpty()) emptyFlow()
      else
          databaseClient
              .sql(
                  """
                  SELECT
                    $SELECT_COLS
                  FROM
                    sapin.taxon_scientific_name
                  WHERE
                    id IN (:ids)
                    """)
              .bind("ids", ids)
              .map(MAPPER)
              .flow()

  suspend fun findAllBySimilarName(
      name: String,
      rank: TaxonEntity.Rank? = null,
      gteRank: TaxonEntity.Rank? = null,
      size: Int = 10
  ): Flow<TaxonScientificNameEntity> =
      databaseClient
          .sql(
              """
              SELECT
                name <-> :name AS dist,
                $SELECT_COLS
              FROM
                sapin.taxon_scientific_name
              ${
                when {
                  rank != null -> "WHERE rank = :rank::sapin.taxon_rank_enum"
                  gteRank != null -> "WHERE rank >= :gteRank::sapin.taxon_rank_enum"
                  else -> ""
                }
              }
              ORDER BY
                dist
              LIMIT
                :size
                """)
          .bind("name", name)
          .bind("size", size)
          .bindIfNotNull<String>("rank", rank)
          .bindIfNotNull<String>("gteRank", gteRank)
          .map(MAPPER)
          .flow()

  suspend fun findAllByTaxonIdIn(ids: Collection<Int>): Flow<TaxonScientificNameEntity> =
      if (ids.isEmpty()) emptyFlow()
      else
          databaseClient
              .sql(
                  """
                  SELECT
                    $SELECT_COLS
                  FROM
                    sapin.taxon_scientific_name
                  WHERE
                    taxon_id IN (:ids)
                    """)
              .bind("ids", ids)
              .map(MAPPER)
              .flow()
}

private val MAPPER: (Row, RowMetadata) -> TaxonScientificNameEntity = { row, _ ->
  TaxonScientificNameEntity(
      id = row.get("id") as Int,
      taxonId = row.get("taxon_id") as Int,
      srcId = row.get("src_id") as String,
      taxonomicStatus = row.get("taxonomic_status") as TaxonScientificNameEntity.TaxonomicStatus,
      name = row.get("name") as String,
      acceptedNameId = row.get("accepted_name_id") as Int?)
}

private const val SELECT_COLS =
    """
  id,
  taxon_id,
  src_id,
  taxonomic_status,
  name,
  accepted_name_id
"""
