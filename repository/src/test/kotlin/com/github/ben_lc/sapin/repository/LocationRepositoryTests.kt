package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.LocationEntity
import com.github.ben_lc.sapin.repository.config.R2dbcConfig
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
@Import(R2dbcConfig::class, LocationRepository::class)
@Sql("load-location-data.sql")
@Sql("clean-location-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LocationRepositoryTests {

  @Autowired lateinit var locationRepo: LocationRepository

  @Test
  fun `findById should return location matching given id`(): Unit = runBlocking {
    assertThat(locationRepo.findById(2))
        .isEqualTo(LocationEntity(id = 2, name = "Japan", level = 1, isoId = "JPN"))
  }

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
        .containsExactly(
            LocationEntity(id = 3, name = "France", level = 1, isoId = "FRA"),
            LocationEntity(
                id = 4,
                parentId = 3,
                name = "Nouvelle-Aquitaine",
                level = 2,
                isoId = "FR-NAQ",
                levelName = "Région",
                levelNameEn = "Region"),
            LocationEntity(
                id = 5,
                parentId = 4,
                name = "Gironde",
                level = 3,
                isoId = "FR-33",
                levelName = "Département",
                levelNameEn = "Department"))
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
                    parentId = 3,
                    name = "Nouvelle-Aquitaine",
                    level = 2,
                    isoId = "FR-NAQ",
                    levelName = "Région",
                    levelNameEn = "Region"))
      }

  @Test
  fun `findChildrenByIdIn should return children of given location id`(): Unit = runBlocking {
    assertThat(locationRepo.findChildrenByIdIn(listOf(3)).toList())
        .containsExactly(
            LocationEntity(
                id = 4,
                parentId = 3,
                name = "Nouvelle-Aquitaine",
                level = 2,
                isoId = "FR-NAQ",
                levelName = "Région",
                levelNameEn = "Region"))
  }

  @Test
  fun `findParentsById should return ancestors of given location id`(): Unit = runBlocking {
    assertThat(locationRepo.findParentsById(4).toList())
        .containsExactly(LocationEntity(id = 3, name = "France", level = 1, isoId = "FRA"))
  }

  @Test
  fun `findAllById should return list of locations matching ids`(): Unit = runBlocking {
    assertThat(locationRepo.findAllByIdIn(listOf(3, 1)).toList())
        .containsExactlyInAnyOrder(
            LocationEntity(id = 3, name = "France", level = 1, isoId = "FRA"),
            LocationEntity(id = 1, name = "Italy", level = 1, isoId = "ITA"))
  }
}
