package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.repository.config.R2dbcConfig
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql

@DataR2dbcTest
@ContextConfiguration(initializers = [DatabaseContextInitializer::class])
@Import(R2dbcConfig::class, LocationEtlRepository::class)
@Sql("location-data.sql")
@Sql("clean-location-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LocationEtlRepositoryTests {

  @Autowired lateinit var locationRepo: LocationEtlRepository

  @Test
  fun `saveAll should persists a flow of COUNTRY level locations`(): Unit = runBlocking {
    val location1 =
        LocationEtl(
            name = "Argentina",
            level = 1,
            isoId = "ARG",
            srcId = "ARG",
            geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
            srid = 4326)
    val location2 =
        LocationEtl(
            name = "Brazil",
            level = 1,
            isoId = "BRA",
            srcId = "BRA",
            geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
            srid = 2154)
    locationRepo.saveAll(listOf(location1, location2).asFlow())

    assertThat(locationRepo.findById(5))
        .isEqualTo(
            LocationEtl(
                id = 5,
                name = "Argentina",
                level = 1,
                isoId = "ARG",
                geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
                srid = 4326))

    assertThat(locationRepo.findById(6))
        .isEqualTo(
            LocationEtl(
                id = 6,
                name = "Brazil",
                level = 1,
                isoId = "BRA",
                geom =
                    "POLYGON((-1.363029328652153 -5.983666035110697,-1.36290492245362 -5.98365914607577,-1.362898041657712 -5.983783699172008,-1.363022447660842 -5.983790588183625,-1.363029328652153 -5.983666035110697))",
                srid = 4326))
  }

  @Test
  fun `saveAll should persists a flow of COUNTRY_SUBDIV_L1 level locations`(): Unit = runBlocking {
    val location1 =
        LocationEtl(
            name = "Bretagne",
            level = 2,
            isoId = "FR-BRE",
            levelNameEn = "Region",
            levelName = "Région",
            srcId = "FR.2",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))",
            srid = 4326)
    val location2 =
        LocationEtl(
            name = "Bourgogne-Franche-Comté",
            level = 2,
            isoId = "FR-BFC",
            levelNameEn = "Region",
            levelName = "Région",
            srcId = "FR.3",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))",
            srid = 4326)
    locationRepo.saveAll(listOf(location1, location2).asFlow())
    assertThat(locationRepo.findById(5))
        .isEqualTo(
            LocationEtl(
                id = 5,
                name = "Bretagne",
                level = 2,
                isoId = "FR-BRE",
                levelNameEn = "Region",
                levelName = "Région",
                geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
                srid = 4326))

    assertThat(locationRepo.findById(6))
        .isEqualTo(
            LocationEtl(
                id = 6,
                name = "Bourgogne-Franche-Comté",
                level = 2,
                isoId = "FR-BFC",
                levelNameEn = "Region",
                levelName = "Région",
                geom = "POLYGON((10 30,30 30,30 10,10 10,10 30))",
                srid = 4326))
  }
}
