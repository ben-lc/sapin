package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.NaturalAreaEtl
import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.repository.config.R2dbcConfig
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql

@DataR2dbcTest
@ContextConfiguration(initializers = [DatabaseContextInitializer::class])
@Import(R2dbcConfig::class, NaturalAreaEtlRepository::class)
@Sql("natural-area-data.sql")
@Sql("clean-natural-area-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class NaturalAreaEtlRepositoryTests {
  @Autowired lateinit var natureAreaEtlRepo: NaturalAreaEtlRepository

  @Test
  fun `saveAll should persists a flow of natural area`(): Unit = runBlocking {
    val area1 =
        NaturalAreaEtl(
            name = "Massif de Fontainebleau",
            srcId = "FR1100795",
            geom = "POLYGON((10 30, 30 30, 30 10, 10 10, 10 30))",
            typeCode = "NATURA2000",
            domain = NaturalAreaEntity.Domain.TERRESTRIAL,
            srid = 4326)
    val area2 =
        NaturalAreaEtl(
            name = "Forêt de Rambouillet",
            srcId = "FR1100796",
            geom = "POLYGON((10 30, 30 30, 30 10, 10 10, 10 30))",
            typeCode = "NATURA2000",
            domain = NaturalAreaEntity.Domain.TERRESTRIAL,
            srid = 4326)
    natureAreaEtlRepo.saveAll(listOf(area1, area2).asFlow())

    Assertions.assertThat(natureAreaEtlRepo.findById(1))
        .isEqualTo(
            NaturalAreaEtl(
                id = 1,
                name = "Massif de Fontainebleau",
                srcId = "FR1100795",
                geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
                typeCode = "NATURA2000",
                domain = NaturalAreaEntity.Domain.TERRESTRIAL,
                srid = 4326))

    Assertions.assertThat(natureAreaEtlRepo.findById(2))
        .isEqualTo(
            NaturalAreaEtl(
                id = 2,
                name = "Forêt de Rambouillet",
                srcId = "FR1100796",
                geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
                typeCode = "NATURA2000",
                domain = NaturalAreaEntity.Domain.TERRESTRIAL,
                srid = 4326))
  }
}
