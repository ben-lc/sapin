package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.Location
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
@Import(R2dbcConfig::class, LocationRepository::class)
@Sql("location-data.sql")
class LocationRepositoryTests {

  @Autowired lateinit var locationRepo: LocationRepository

  @Test
  fun `saveAll() correctly persists a flow of locations`(): Unit = runBlocking {
    val location1 =
        Location(
            name = "France",
            level = Location.Level.COUNTRY,
            isoId = "FRA",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    val location2 =
        Location(
            name = "Brazil",
            level = Location.Level.COUNTRY,
            isoId = "BRA",
            geom = "POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))")
    locationRepo.saveAll(listOf(location1, location2).asFlow())

    assertThat(locationRepo.findByName("France"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            Location(name = "France", level = Location.Level.COUNTRY, isoId = "FRA", geom = null))

    assertThat(locationRepo.findByName("Brazil"))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            Location(name = "Brazil", level = Location.Level.COUNTRY, isoId = "BRA", geom = null))
  }

  @Test
  fun `findByName() returns location by its name without fetching geom`(): Unit = runBlocking {
    assertThat(locationRepo.findByName("Italy"))
        .isEqualTo(
            Location(
                id = 1, name = "Italy", level = Location.Level.COUNTRY, isoId = "ITA", geom = null))
  }
}
