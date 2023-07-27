package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.model.NaturalAreaTypeEntity
import com.github.ben_lc.sapin.repository.NaturalAreaRepository
import com.github.ben_lc.sapin.repository.NaturalAreaTypeRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester

@GraphQlTest(controllers = [NaturalAreaController::class])
class NaturalAreaControllerTests {

  @Autowired lateinit var tester: GraphQlTester
  @MockkBean lateinit var naturalAreaRepo: NaturalAreaRepository

  @MockkBean lateinit var naturalAreaTypeRepo: NaturalAreaTypeRepository

  private val expected =
      mapOf(
          "oignons" to
              NaturalAreaEntity(
                  id = 1,
                  name = "Lande tourbeuse des Oignons",
                  typeId = 1,
                  domain = NaturalAreaEntity.Domain.TERRESTRIAL,
                  srcId = "FR8201634",
                  description = null),
          "ridens" to
              NaturalAreaEntity(
                  id = 2,
                  name = "Ridens et dunes hydrauliques du détroit du Pas-de-Calais",
                  typeId = 1,
                  domain = NaturalAreaEntity.Domain.MARINE,
                  srcId = "FR3102004",
                  description = "Très bel endroit"),
          "somewhere" to
              NaturalAreaEntity(
                  id = 3,
                  name = "somewhere",
                  typeId = 3,
                  domain = NaturalAreaEntity.Domain.TERRESTRIAL,
                  srcId = "42",
                  description = "Amazing place"))
  @Test
  fun `naturalAreaById should return area matching given id`() {
    coEvery { naturalAreaRepo.findById(1) } returns expected["ridens"]

    tester
        .document(
            """{ naturalAreaById(id: "1") { id name description } }""",
        )
        .execute()
        .path("data.naturalAreaById")
        .matchesJsonStrictly(
            """{"id":"2","name":"Ridens et dunes hydrauliques du détroit du Pas-de-Calais","description":"Très bel endroit"}""")
  }

  @Test
  fun `naturalAreaById should return null when id matches no entity`() {
    coEvery { naturalAreaRepo.findById(3) } returns null
    tester
        .document("""{ naturalAreaById(id: "3") { id name } }""")
        .execute()
        .path("data.naturalAreaById")
        .valueIsNull()
  }

  @Test
  fun `searchNaturalAreas should return list of areas matching all supported search params`() {
    coEvery { naturalAreaRepo.findAll(3, "that place", listOf(1, 3), 2) } returns
        flowOf(expected["oignons"]!!, expected["somewhere"]!!)
    tester
        .document(
            """{ searchNaturalAreas(locationId: 3, name: "that place", typeIds: [1,3], limit: 2) { id name description } }""")
        .execute()
        .path("data.searchNaturalAreas")
        .matchesJsonStrictly(
            """[{"id":"1","name":"Lande tourbeuse des Oignons","description":null},{"id":"3","name":"somewhere","description":"Amazing place"}]""")
  }

  @Test
  fun `naturalAreasByGeolocation should return list of areas matching all supported search params`() {
    coEvery { naturalAreaRepo.findAllByGeolocation(3.3, 4.4, listOf(1, 3), 2) } returns
        flowOf(expected["oignons"]!!, expected["somewhere"]!!)
    tester
        .document(
            """{ naturalAreasByGeolocation(longitude: 3.3, latitude: 4.4, typeIds: [1,3], limit: 2) { id name description } }""")
        .execute()
        .path("data.naturalAreasByGeolocation")
        .matchesJsonStrictly(
            """[{"id":"1","name":"Lande tourbeuse des Oignons","description":null},{"id":"3","name":"somewhere","description":"Amazing place"}]""")
  }

  @Test
  fun `type should return batched list of area types`() {
    coEvery { naturalAreaRepo.findAll(3) } returns
        flowOf(expected["oignons"]!!, expected["somewhere"]!!)
    coEvery { naturalAreaTypeRepo.findAllByIdIn(listOf(1, 3)) } returns
        flowOf(
            NaturalAreaTypeEntity(
                id = 1,
                name = "Natura 2000",
                code = "NATURA2000",
                description =
                    "Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l'Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu'ils contiennent"),
            NaturalAreaTypeEntity(
                id = 3, name = "Something", code = "a natural area type", description = null))

    tester
        .document(
            """{ searchNaturalAreas(locationId: "3") { id type { id name code description } } }""")
        .execute()
        .path("data.searchNaturalAreas")
        .matchesJsonStrictly(
            """[{"id":"1","type":{"id":"1","name":"Natura 2000","code":"NATURA2000","description":"Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l'Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu'ils contiennent"}},{"id":"3","type":{"id":"3","name":"Something","code":"a natural area type","description":null}}]""")
  }
}
