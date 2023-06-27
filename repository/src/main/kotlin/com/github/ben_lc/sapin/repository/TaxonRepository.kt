package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.TaxonEntity
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
class TaxonRepository(private val databaseClient: DatabaseClient) {

  suspend fun findById(id: Int): TaxonEntity? =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_TAXON_COLS
              FROM
                sapin.taxon
              WHERE
                id = :id
                """)
          .bind("id", id)
          .map(TAXON_MAPPER)
          .awaitSingleOrNull()

  suspend fun findAllByIdIn(ids: Collection<Int>): Flow<TaxonEntity> =
      if (ids.isEmpty()) emptyFlow()
      else
          databaseClient
              .sql(
                  """
                  SELECT
                    $SELECT_TAXON_COLS
                  FROM
                    sapin.taxon
                  WHERE
                    id IN (:ids)
                    """)
              .bind("ids", ids)
              .map(TAXON_MAPPER)
              .flow()

  suspend fun findVernacularNamesByIdIn(ids: Collection<Int>): Flow<TaxonEntity.VernacularName> =
      databaseClient
          .sql(
              """
              SELECT
                taxon_id,
                name,
                language
              FROM
                sapin.taxon_vernacular_name
              WHERE
                taxon_id IN (:ids)
                """)
          .bind("ids", ids)
          .map(VERNACULAR_NAME_MAPPER)
          .flow()
  suspend fun findVernacularNamesBySimilarName(
      name: String,
      language: String,
      rank: TaxonEntity.Rank? = null,
      gteRank: TaxonEntity.Rank? = null,
      size: Int = 10
  ): Flow<TaxonEntity.VernacularName> =
      databaseClient
          .sql(
              """
              SELECT
                name <-> :name AS dist,
                taxon_id,
                name,
                language
              FROM
                sapin.taxon_vernacular_name
              WHERE
                language = :language
                ${
                  when {
                    rank != null -> "AND rank = :rank::sapin.taxon_rank_enum"
                    gteRank != null -> "AND rank >= :gteRank::sapin.taxon_rank_enum"
                    else -> ""
                  }
                }
              ORDER BY
                dist
              LIMIT
                :size
                """)
          .bind("name", name)
          .bind("language", language)
          .bind("size", size)
          .bindIfNotNull<String>("rank", rank)
          .bindIfNotNull<String>("gteRank", gteRank)
          .map(VERNACULAR_NAME_MAPPER)
          .flow()

  suspend fun findParentsById(id: Int): Flow<TaxonEntity> =
      databaseClient
          .sql(
              """
              SELECT
                $SELECT_TAXON_COLS
              FROM
                sapin.taxon
              WHERE
                tree_path @> (SELECT tree_path FROM sapin.taxon WHERE id = :id)
                AND id != :id
              ORDER BY rank
              """)
          .bind("id", id)
          .map(TAXON_MAPPER)
          .flow()

  suspend fun findChildrenByIdIn(ids: Collection<Int>): Flow<TaxonEntity> =
      if (ids.isEmpty()) emptyFlow()
      else
          databaseClient
              .sql(
                  """
                  SELECT
                    $SELECT_TAXON_COLS
                  FROM
                    sapin.taxon
                  WHERE
                    parent_id IN (:ids)
                    """)
              .bind("ids", ids)
              .map(TAXON_MAPPER)
              .flow()
}

private val TAXON_MAPPER: (Row, RowMetadata) -> TaxonEntity = { row, _ ->
  TaxonEntity(
      id = row.get("id") as Int,
      srcNameId = row.get("src_name_id") as String,
      parentId = row.get("parent_id") as Int?,
      rank = row.get("rank") as TaxonEntity.Rank,
      acceptedName = row.get("accepted_name") as String)
}

private val VERNACULAR_NAME_MAPPER: (Row, RowMetadata) -> TaxonEntity.VernacularName = { row, _ ->
  TaxonEntity.VernacularName(
      name = row.get("name") as String,
      language = row.get("language") as String,
      taxonId = row.get("taxon_id") as Int)
}

private const val SELECT_TAXON_COLS =
    """
  id,
  src_name_id,
  parent_id,
  rank,
  accepted_name
"""
