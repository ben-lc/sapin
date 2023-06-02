package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query.query
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/** Spring data R2DBC interface for Location. */
@Repository
class LocationRepository(private val template: R2dbcEntityTemplate) {
  @Transactional
  suspend fun saveAll(locations: Flow<Location>) =
      locations.collect {
        template.databaseClient
            .sql(
                "INSERT INTO sapin.location (level, name, iso_id, geom) VALUES ($1, $2, $3, ST_GeomFromText($4))")
            .bind(0, it.level)
            .bind(1, it.name)
            .bind(2, it.isoId!!)
            .bind(3, it.geom!!)
            .await()
      }

  /** Get Location by its name, geom property is never fetched. */
  suspend fun findByName(name: String): Location? =
      template
          .selectOne(
              query(Criteria.where("name").`is`(name)).columns("loc_id", "level", "name", "iso_id"),
              Location::class.java)
          .awaitSingleOrNull()
}
