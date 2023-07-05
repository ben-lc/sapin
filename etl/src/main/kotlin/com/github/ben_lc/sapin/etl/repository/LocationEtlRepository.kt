package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.repository.util.bindNullable
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/** Spring data R2DBC repository for Location in the context of the ETL loading tool. */
@Repository
class LocationEtlRepository(private val databaseClient: DatabaseClient) {
  @Transactional
  suspend fun saveAll(locations: Flow<LocationEtl>) =
      locations.collect {
        databaseClient
            .sql(
                """
                INSERT INTO sapin.location (
                   level,
                   name,
                   geom,
                   level_name,
                   level_name_en,
                   iso_id,
                   src_id,
                   src_parent_id
                 )
                 VALUES (
                   :level,
                   :name,
                   ST_Transform(ST_GeomFromText(:geom, :srid), 4326),
                   :levelName,
                   :levelNameEn,
                   :isoId,
                   :srcId,
                   :srcParentId
                   )""")
            .bind("level", it.level)
            .bind("name", it.name)
            .bind("geom", it.geom)
            .bind("srid", it.srid)
            .bindNullable<String>("levelName", it.levelName)
            .bindNullable<String>("levelNameEn", it.levelNameEn)
            .bindNullable<String>("isoId", it.isoId)
            .bindNullable<String>("srcId", it.srcId)
            .bindNullable<String>("srcParentId", it.srcParentId)
            .await()
      }

  /** Get Location by its name, geom property is never fetched. */
  suspend fun findById(id: Int): LocationEtl? =
      databaseClient
          .sql(
              """
              SELECT
                id,
                parent_id,
                level,
                level_name,
                level_name_en,
                name,
                iso_id,
                ST_asText(geom) AS geom,
                ST_SRID(geom) AS srid
              FROM
                sapin.location
              WHERE
                id = :id
                """)
          .bind("id", id)
          .map { row ->
            LocationEtl(
                id = row.get("id") as Int,
                parentId = row.get("parent_id") as Int?,
                name = row.get("name") as String,
                level = row.get("level") as Short,
                levelName = row.get("level_name") as String?,
                levelNameEn = row.get("level_name_en") as String?,
                isoId = row.get("iso_id") as String?,
                geom = row.get("geom") as String,
                srid = row.get("srid") as Int)
          }
          .awaitOneOrNull()
}
