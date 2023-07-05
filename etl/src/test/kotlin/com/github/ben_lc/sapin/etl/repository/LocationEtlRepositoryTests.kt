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
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))",
            srid = 4326)
    val location2 =
        LocationEtl(
            name = "Brazil",
            level = 1,
            isoId = "BRA",
            srcId = "BRA",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))",
            srid = 4326)
    locationRepo.saveAll(listOf(location1, location2).asFlow())

    assertThat(locationRepo.findByName("Argentina"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(LocationEtl(name = "Argentina", level = 1, isoId = "ARG"))

    assertThat(locationRepo.findByName("Brazil"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(LocationEtl(name = "Brazil", level = 1, isoId = "BRA"))
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
    assertThat(locationRepo.findByName("Bretagne"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            LocationEtl(
                name = "Bretagne",
                level = 2,
                isoId = "FR-BRE",
                levelNameEn = "Region",
                levelName = "Région"))

    assertThat(locationRepo.findByName("Bourgogne-Franche-Comté"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            LocationEtl(
                name = "Bourgogne-Franche-Comté",
                level = 2,
                isoId = "FR-BFC",
                levelNameEn = "Region",
                levelName = "Région"))
  }

  @Test
  fun `findByName should returns location by its name without fetching geom`(): Unit = runBlocking {
    assertThat(locationRepo.findByName("Italy"))
        .isEqualTo(LocationEtl(id = 1, name = "Italy", level = 1, isoId = "ITA", geom = null))

    assertThat(locationRepo.findByName("Nouvelle-Aquitaine"))
        .isEqualTo(
            LocationEtl(
                id = 4,
                name = "Nouvelle-Aquitaine",
                level = 2,
                levelName = "Région",
                levelNameEn = "Region",
                isoId = "FR-NAQ"))
  }
}
