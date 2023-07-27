package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.NaturalAreaEtl
import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.repository.util.bindNullable
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class NaturalAreaEtlRepository(private val databaseClient: DatabaseClient) {

  @Transactional
  suspend fun saveAll(locations: Flow<NaturalAreaEtl>) =
      locations.collect {
        databaseClient
            .sql(
                """
                INSERT INTO sapin.natural_area (name, domain, src_id, type_id, description, geom)
                   VALUES (
                     :name,
                     :domain,
                     :srcId,
                     (SELECT id FROM sapin.natural_area_type WHERE code = :typeCode),
                     :description,
                     ST_Transform(ST_GeomFromText(:geom, :srid), 4326)
                   )
                   """)
            .bind("name", it.name)
            .bindNullable<NaturalAreaEntity.Domain>("domain", it.domain)
            .bindNullable<String>("srcId", it.srcId)
            .bind("typeCode", it.typeCode)
            .bindNullable<String>("description", it.description)
            .bind("geom", it.geom)
            .bind("srid", it.srid)
            .await()
      }

  suspend fun findById(id: Int): NaturalAreaEtl? =
      databaseClient
          .sql(
              """
              SELECT
                na.id,
                na.name,
                na.domain,
                na.src_id,
                nat.code AS type_code,
                na.description,
                ST_asText(na.geom) AS geom,
                ST_SRID(na.geom) AS srid
              FROM
                sapin.natural_area na
                JOIN sapin.natural_area_type nat ON na.type_id = nat.id 
              WHERE
                na.id = :id
                """)
          .bind("id", id)
          .map { row ->
            NaturalAreaEtl(
                id = row.get("id") as Int,
                name = row.get("name") as String,
                domain = row.get("domain") as NaturalAreaEntity.Domain,
                srcId = row.get("src_id") as String,
                typeCode = row.get("type_code") as String,
                description = row.get("description") as String?,
                geom = row.get("geom") as String,
                srid = row.get("srid") as Int)
          }
          .awaitOneOrNull()
}
