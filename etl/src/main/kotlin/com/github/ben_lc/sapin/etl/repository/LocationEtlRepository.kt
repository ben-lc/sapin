package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.repository.util.bindNullable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query.query
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/** Spring data R2DBC repository for Location in the context of the ETL loading tool. */
@Repository
class LocationEtlRepository(private val template: R2dbcEntityTemplate) {
  @Transactional
  suspend fun saveAll(locations: Flow<LocationEtl>) =
      locations.collect {
        template.databaseClient
            .sql(
                """INSERT INTO sapin.location (level, name, geom, level_name, level_name_en, iso_id, src_id, src_parent_id)
                   VALUES ($1, $2, ST_GeomFromText($3, $4), $5, $6, $7, $8, $9)""")
            .bind(0, it.level)
            .bind(1, it.name)
            .bind(2, it.geom!!)
            .bind(3, it.srid!!)
            .bindNullable<String>(4, it.levelName)
            .bindNullable<String>(5, it.levelNameEn)
            .bindNullable<String>(6, it.isoId)
            .bindNullable<String>(7, it.srcId)
            .bindNullable<String>(8, it.srcParentId)
            .await()
      }

  /** Get Location by its name, geom property is never fetched. */
  suspend fun findByName(name: String): LocationEtl? =
      template
          .selectOne(
              query(Criteria.where("name").`is`(name))
                  .columns(
                      "id", "parent_id", "level", "level_name", "level_name_en", "name", "iso_id"),
              LocationEtl::class.java)
          .awaitSingleOrNull()
}
