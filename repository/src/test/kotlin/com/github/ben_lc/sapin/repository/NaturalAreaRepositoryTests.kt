package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.model.NaturalAreaEntity.Domain
import com.github.ben_lc.sapin.repository.config.R2dbcConfig
import kotlinx.coroutines.flow.toList
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
@Import(R2dbcConfig::class, NaturalAreaRepository::class)
@Sql("load-natural-area-data.sql")
@Sql("clean-natural-area-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class NaturalAreaRepositoryTests {

  @Autowired lateinit var naturalAreaRepo: NaturalAreaRepository

  private val expected =
      mapOf(
          "oignons" to
              NaturalAreaEntity(
                  id = 1,
                  name = "Lande tourbeuse des Oignons",
                  typeId = 1,
                  domain = Domain.TERRESTRIAL,
                  srcId = "FR8201634",
                  description = null),
          "ridens" to
              NaturalAreaEntity(
                  id = 2,
                  name = "Ridens et dunes hydrauliques du détroit du Pas-de-Calais",
                  typeId = 1,
                  domain = Domain.MARINE,
                  srcId = "FR3102004",
                  description = "Très bel endroit"),
          "somewhere" to
              NaturalAreaEntity(
                  id = 3,
                  name = "somewhere",
                  typeId = 3,
                  domain = Domain.TERRESTRIAL,
                  srcId = "42",
                  description = "Amazing place"))
  @Test
  fun `findById should return area matching given id`(): Unit = runBlocking {
    Assertions.assertThat(naturalAreaRepo.findById(1)).isEqualTo(expected["oignons"])
  }

  @Test
  fun `findAll by locationId should return flow of area present in given locationId`(): Unit =
      runBlocking {
        Assertions.assertThat(naturalAreaRepo.findAll(locationId = 3).toList())
            .containsExactlyInAnyOrder(expected["oignons"], expected["ridens"])
      }

  @Test
  fun `findAll by locationId and name should return flow of area filtered by locationId and sorted by name similarity`():
      Unit = runBlocking {
    Assertions.assertThat(naturalAreaRepo.findAll(locationId = 3, name = "ridens").toList())
        .containsExactlyInAnyOrder(expected["ridens"], expected["oignons"])
  }

  @Test
  fun `findAll by locationId and typeIds should return flow of area having given type ids`(): Unit =
      runBlocking {
        Assertions.assertThat(naturalAreaRepo.findAll(locationId = 3, typeIds = listOf(1)).toList())
            .containsExactlyInAnyOrder(expected["ridens"], expected["oignons"])
      }

  @Test
  fun `findAllByGeolocation should return flow of area order by proximity to given point`(): Unit =
      runBlocking {
        Assertions.assertThat(
                naturalAreaRepo
                    .findAllByGeolocation(longitude = 3.954643, latitude = 43.804933)
                    .toList())
            .containsExactlyInAnyOrder(
                expected["oignons"], expected["ridens"], expected["somewhere"])
      }

  @Test
  fun `findAllByGeolocation and typeIds should return flow of area order by proximity to given point having given type id`():
      Unit = runBlocking {
    Assertions.assertThat(
            naturalAreaRepo
                .findAllByGeolocation(
                    longitude = 3.954643, latitude = 43.804933, typeIds = listOf(3))
                .toList())
        .containsExactlyInAnyOrder(expected["somewhere"])
  }
}
