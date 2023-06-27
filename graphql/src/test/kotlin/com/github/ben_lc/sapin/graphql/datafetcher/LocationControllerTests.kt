package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.Location
import com.github.ben_lc.sapin.model.LocationEntity
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
    coEvery { locationRepo.findById(3) } returns LocationEntity(id = 3, name = "France", level = 1)

    tester
        .document(
            """{ locationById(id: "3") { id name } }""",
        )
        .execute()
        .path("data.locationById")
        .entity(Location::class.java)
        .isEqualTo(Location(id = "3", name = "France", level = 0))
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
            LocationEntity(id = 3, name = "France", level = 1),
            LocationEntity(id = 4, name = "Greece", level = 1))
    tester
        .document("""{ locationsBySimilarName(name: "trance", level: 1) { id name level } }""")
        .execute()
        .path("data.locationsBySimilarName[*]")
        .entityList(Location::class.java)
        .containsExactly(
            Location(id = "3", name = "France", level = 1),
            Location(id = "4", name = "Greece", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates for given level`() {
    coEvery { locationRepo.findAllByGeolocationAndLevel(1.0, 1.0, 1) } returns
        flowOf(LocationEntity(id = 3, name = "France", level = 1))
    tester
        .document(
            """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0, level: 1) { id name level } }""")
        .execute()
        .path("data.locationsByGeolocation[*]")
        .entityList(Location::class.java)
        .containsExactly(Location(id = "3", name = "France", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates`() {
    coEvery { locationRepo.findAllByGeolocation(1.0, 1.0) } returns
        flowOf(LocationEntity(id = 3, name = "France", level = 1))
    tester
        .document(
            """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0) { id name level } }""")
        .execute()
        .path("data.locationsByGeolocation[*]")
        .entityList(Location::class.java)
        .containsExactly(Location(id = "3", name = "France", level = 1))
  }

  @Test
  fun `children should returns batched list of children`() {
    coEvery { locationRepo.findAllBySimilarName("fr", 1) } returns
        flowOf(
            LocationEntity(id = 3, name = "France", level = 1),
            LocationEntity(id = 1, name = "Italy", level = 1))
    coEvery { locationRepo.findChildrenByIdIn(listOf(3, 1)) } returns
        flowOf(
            LocationEntity(id = 7, name = "Bretagne", level = 2, parentId = 3),
            LocationEntity(id = 8, name = "Bourgogne", level = 2, parentId = 3),
            LocationEntity(id = 9, name = "Lazio", level = 2, parentId = 1),
            LocationEntity(id = 10, name = "Toscana", level = 2, parentId = 1))

    tester
        .document(
            """{ locationsBySimilarName(name: "fr", level: 1) { name children { id name } } }""")
        .execute()
        .path("data.locationsBySimilarName")
        .matchesJsonStrictly(
            """[{"name":"France","children":[{"id":"7","name":"Bretagne"},{"id":"8","name":"Bourgogne"}]},{"name":"Italy","children":[{"id":"9","name":"Lazio"},{"id":"10","name":"Toscana"}]}]""")
  }

  @Test
  fun `parent should returns list of parents for given collection of location`() {
    coEvery { locationRepo.findAllBySimilarName("fr", 1) } returns
        flowOf(
            LocationEntity(id = 7, name = "Bretagne", level = 2, parentId = 3),
            LocationEntity(id = 9, name = "Lazio", level = 2, parentId = 1))
    coEvery { locationRepo.findAllByIdIn(listOf(3, 1)) } returns
        flowOf(
            LocationEntity(id = 3, name = "France", level = 1),
            LocationEntity(id = 1, name = "Italy", level = 1))

    tester
        .document(
            """{ locationsBySimilarName(name: "fr", level: 1) { name parent { id name } } }""")
        .execute()
        .path("data.locationsBySimilarName")
        .matchesJsonStrictly(
            """[{"name":"Bretagne","parent":{"id":"3","name":"France"}},{"name":"Lazio","parent":{"id":"1","name":"Italy"}}]""")
  }
}
