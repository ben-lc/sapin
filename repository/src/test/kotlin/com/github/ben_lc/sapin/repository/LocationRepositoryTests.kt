package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.LocationEntity
import kotlinx.coroutines.flow.toList
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
@Import(R2dbcConfig::class)
@Sql("location-data.sql")
@Sql("clean-location-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LocationRepositoryTests {

  @Autowired lateinit var locationRepo: LocationRepository

  @Test
  fun `findBySimilarName should return locations based on name similarity`(): Unit = runBlocking {
    assertThat(locationRepo.findAllBySimilarName("jpan", 1, 1).toList())
        .containsExactly(LocationEntity(id = 2, name = "Japan", level = 1, isoId = "JPN"))

    assertThat(locationRepo.findAllBySimilarName("trince", 1, 1).toList())
        .containsExactly(LocationEntity(id = 3, name = "France", level = 1, isoId = "FRA"))
  }

  @Test
  fun `findByGeolocation should return locations based on coordinates`(): Unit = runBlocking {
    assertThat(locationRepo.findAllByGeolocation(-0.5759137982167051, 44.823353822211686).toList())
        .containsExactlyInAnyOrder(
            LocationEntity(id = 3, name = "France", level = 1, isoId = "FRA"),
            LocationEntity(
                id = 4,
                name = "Nouvelle-Aquitaine",
                level = 2,
                isoId = "FR-NAQ",
                levelLocalName = "Région",
                levelLocalNameEn = "Region"))
  }
  @Test
  fun `findByGeolocationAndLevel should return locations based on coordinates and level`(): Unit =
      runBlocking {
        assertThat(
                locationRepo
                    .findAllByGeolocationAndLevel(-0.5759137982167051, 44.823353822211686, 2)
                    .toList())
            .containsExactly(
                LocationEntity(
                    id = 4,
                    name = "Nouvelle-Aquitaine",
                    level = 2,
                    isoId = "FR-NAQ",
                    levelLocalName = "Région",
                    levelLocalNameEn = "Region"))
      }
}
