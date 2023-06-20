package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.LocationType
import com.github.ben_lc.sapin.model.Location
import com.github.ben_lc.sapin.repository.LocationRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester

@GraphQlTest(controllers = [LocationController::class])
class LocationControllerTests {

  @Autowired lateinit var tester: GraphQlTester
  @MockkBean lateinit var locationRepo: LocationRepository

  @Test
  fun `locationById should return location matching given id`() {
    coEvery { locationRepo.findById(3) } returns Location(id = 3, name = "France", level = 1)

    tester
        .document(
            """{ locationById(id: "3") { id name } }""",
        )
        .execute()
        .path("data.locationById")
        .entity(LocationType::class.java)
        .isEqualTo(LocationType(id = "3", name = "France", level = 0))
  }

  @Test
  fun `locationById should return null when id matches no entity`() {
    coEvery { locationRepo.findById(3) } returns null
    tester
        .document("""{ locationById(id: "3") { id name } }""")
        .execute()
        .path("data.locationById")
        .valueIsNull()
  }

  @Test
  fun `locationBySimilarName should return list of location similar to given name`() {
    coEvery { locationRepo.findAllBySimilarName("trance", 1, 10) } returns
        flowOf(
            Location(id = 3, name = "France", level = 1),
            Location(id = 4, name = "Greece", level = 1))
    tester
        .document("""{ locationsBySimilarName(name: "trance", level: 1) { id name level } }""")
        .execute()
        .path("data.locationsBySimilarName[*]")
        .entityList(LocationType::class.java)
        .containsExactly(
            LocationType(id = "3", name = "France", level = 1),
            LocationType(id = "4", name = "Greece", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates for given level`() {
    coEvery { locationRepo.findAllByGeolocationAndLevel(1.0, 1.0, 1) } returns
        flowOf(Location(id = 3, name = "France", level = 1))
    tester
        .document(
            """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0, level: 1) { id name level } }""")
        .execute()
        .path("data.locationsByGeolocation[*]")
        .entityList(LocationType::class.java)
        .containsExactly(LocationType(id = "3", name = "France", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates`() {
    coEvery { locationRepo.findAllByGeolocation(1.0, 1.0) } returns
        flowOf(Location(id = 3, name = "France", level = 1))
    tester
        .document(
            """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0) { id name level } }""")
        .execute()
        .path("data.locationsByGeolocation[*]")
        .entityList(LocationType::class.java)
        .containsExactly(LocationType(id = "3", name = "France", level = 1))
  }
}
