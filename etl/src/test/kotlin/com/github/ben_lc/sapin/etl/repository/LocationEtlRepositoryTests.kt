package com.github.ben_lc.sapin.etl.repository

import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.repository.R2dbcConfig
import kotlinx.coroutines.delay
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
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    val location2 =
        LocationEtl(
            name = "Brazil",
            level = 1,
            isoId = "BRA",
            srcId = "BRA",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    locationRepo.saveAll(listOf(location1, location2).asFlow())

    assertThat(locationRepo.findByName("Argentina"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(LocationEtl(name = "Argentina", level = 1, isoId = "ARG", geom = null))

    assertThat(locationRepo.findByName("Brazil"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(LocationEtl(name = "Brazil", level = 1, isoId = "BRA", geom = null))
  }

  @Test
  fun `saveAll should persists a flow of COUNTRY_SUBDIV_L1 level locations`(): Unit = runBlocking {
    val location1 =
        LocationEtl(
            name = "Bretagne",
            level = 2,
            isoId = "FR-BRE",
            levelLocalNameEn = "Region",
            levelLocalName = "Région",
            srcId = "FR.2",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    val location2 =
        LocationEtl(
            name = "Bourgogne-Franche-Comté",
            level = 2,
            isoId = "FR-BFC",
            levelLocalNameEn = "Region",
            levelLocalName = "Région",
            srcId = "FR.3",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    locationRepo.saveAll(listOf(location1, location2).asFlow())
    delay(10000L)
    assertThat(locationRepo.findByName("Bretagne"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            LocationEtl(
                name = "Bretagne",
                level = 2,
                isoId = "FR-BRE",
                levelLocalNameEn = "Region",
                levelLocalName = "Région",
                geom = null))

    assertThat(locationRepo.findByName("Bourgogne-Franche-Comté"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            LocationEtl(
                name = "Bourgogne-Franche-Comté",
                level = 2,
                isoId = "FR-BFC",
                levelLocalNameEn = "Region",
                levelLocalName = "Région",
                geom = null))
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
                levelLocalName = "Région",
                levelLocalNameEn = "Region",
                isoId = "FR-NAQ",
                geom = null))
  }
}
