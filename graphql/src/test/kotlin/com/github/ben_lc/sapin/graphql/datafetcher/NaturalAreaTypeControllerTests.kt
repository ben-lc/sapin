package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.model.NaturalAreaTypeEntity
import com.github.ben_lc.sapin.repository.NaturalAreaTypeRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester

@GraphQlTest(controllers = [NaturalAreaTypeController::class])
class NaturalAreaTypeControllerTests {

  @Autowired lateinit var tester: GraphQlTester
  @MockkBean lateinit var naturalAreaTypeRepo: NaturalAreaTypeRepository

  private val expected =
      mapOf(
          "natura2000" to
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
                  description = null))
  @Test
  fun `naturalAreaTypeById should return area type matching given id`() {
    coEvery { naturalAreaTypeRepo.findById(1) } returns expected["natura2000"]

    tester
        .document(
            """{ naturalAreaTypeById(id: "1") { id name code description } }""",
        )
        .execute()
        .path("data.naturalAreaTypeById")
        .matchesJsonStrictly(
            """{"id":"1","name":"Natura 2000","code":"NATURA2000","description":"Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l'Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu'ils contiennent"}""")
  }

  @Test
  fun `searchNaturalAreaTypes should return list of area types matching all supported search params`() {
    coEvery { naturalAreaTypeRepo.findAll("that name", listOf(1, 3), 2) } returns
        flowOf(expected["natura2000"]!!, expected["znieff"]!!)
    tester
        .document(
            """{ searchNaturalAreaTypes(name: "that name", locationIds: [1, 3], limit: 2) { id name code description } }""")
        .execute()
        .path("data.searchNaturalAreaTypes")
        .matchesJsonStrictly(
            """[{"id":"1","name":"Natura 2000","code":"NATURA2000","description":"Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l'Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu'ils contiennent"},{"id":"2","name":"Zone Naturelle d'intérêt écologique, faunistique et floristique","code":"ZNIEFF","description":null}]""")
  }
}
