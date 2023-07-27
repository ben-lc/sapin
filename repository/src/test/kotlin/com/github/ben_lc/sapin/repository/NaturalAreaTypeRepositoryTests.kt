package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.NaturalAreaTypeEntity
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
@Import(R2dbcConfig::class, NaturalAreaTypeRepository::class)
@Sql("load-natural-area-type-data.sql")
@Sql("clean-natural-area-type-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class NaturalAreaTypeRepositoryTests {

  @Autowired lateinit var naturalAreaTypeRepo: NaturalAreaTypeRepository

  private val expected =
      mapOf(
          "natura" to
              NaturalAreaTypeEntity(
                  id = 1,
                  name = "Natura 2000",
                  code = "NATURA2000",
                  description =
                      "Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l'Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu'ils contiennent"),
          "znieff" to
              NaturalAreaTypeEntity(
                  id = 2,
                  name = "Zone Naturelle d'intérêt écologique, faunistique et floristique",
                  code = "ZNIEFF",
                  description = null),
          "something" to
              NaturalAreaTypeEntity(
                  id = 3, name = "Something", code = "a natural area type", description = null))

  @Test
  fun `findById should return area type matching given id`(): Unit = runBlocking {
    Assertions.assertThat(naturalAreaTypeRepo.findById(1)).isEqualTo(expected["natura"])
  }
  @Test
  fun `findAllByIdIn should return flow of area types matching given ids`(): Unit = runBlocking {
    Assertions.assertThat(naturalAreaTypeRepo.findAllByIdIn(listOf(1, 2)).toList())
        .containsExactlyInAnyOrder(expected["natura"], expected["znieff"])
  }

  @Test
  fun `findAll by locationIds should return flow of area types present in given locationId`():
      Unit = runBlocking {
    Assertions.assertThat(naturalAreaTypeRepo.findAll(locationIds = listOf(3)).toList())
        .containsExactlyInAnyOrder(expected["natura"], expected["znieff"])
  }

  @Test
  fun `findAll by name should return flow of area types order by name similarity`(): Unit =
      runBlocking {
        Assertions.assertThat(naturalAreaTypeRepo.findAll(name = "floristique", limit = 2).toList())
            .containsExactly(expected["znieff"], expected["natura"])
      }

  @Test
  fun `findAll by empty params should return flow of all area types`(): Unit = runBlocking {
    Assertions.assertThat(naturalAreaTypeRepo.findAll().toList())
        .containsExactlyInAnyOrder(expected["natura"], expected["znieff"], expected["something"])
  }
}
