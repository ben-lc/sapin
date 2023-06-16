package sapin.graphql.datafetcher

import com.github.ben_lc.sapin.generated.graphql.types.Location
import com.github.ben_lc.sapin.graphql.datafetcher.LocationDataFetcher
import com.github.ben_lc.sapin.model.LocationEntity
import com.github.ben_lc.sapin.repository.LocationRepository
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.reactive.internal.DefaultDgsReactiveQueryExecutor
import com.netflix.graphql.dgs.webflux.autoconfiguration.DgsWebFluxAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.config.WebFluxConfigurationSupport
import reactor.test.StepVerifier

@SpringBootTest(
    classes =
        [
            DgsAutoConfiguration::class,
            DgsWebFluxAutoConfiguration::class,
            WebFluxConfigurationSupport::class,
            LocationDataFetcher::class])
class LocationDataFetcherTests {

  @MockkBean lateinit var locationRepo: LocationRepository
  @Autowired lateinit var queryExecutor: DefaultDgsReactiveQueryExecutor

  @Test
  fun `locationById should return location matching given id`() {
    coEvery { locationRepo.findById(3) } returns LocationEntity(id = 3, name = "France", level = 1)

    assertThat(
            queryExecutor
                .executeAndExtractJsonPathAsObject(
                    """{ locationById(id: "3") { id name } }""",
                    "data.locationById",
                    Location::class.java)
                .block())
        .isEqualTo(Location(id = "3", name = "France", level = 0))
  }

  @Test
  fun `locationById should return null when id matches no entity`() {
    coEvery { locationRepo.findById(3) } returns null

    val result =
        queryExecutor.executeAndExtractJsonPath<String>(
            """{ locationById(id: "3") { id name } }""", "data.locationById")

    StepVerifier.create(result).verifyError()
  }

  @Test
  fun `locationBySimilarName should return list of location similar to given name`() {
    coEvery { locationRepo.findAllBySimilarName("trance", 1, 10) } returns
        flowOf(
            LocationEntity(id = 3, name = "France", level = 1),
            LocationEntity(id = 4, name = "Greece", level = 1))

    assertThat(
            queryExecutor
                .executeAndExtractJsonPathAsObject(
                    """{ locationsBySimilarName(name: "trance", level: 1) { id name level } }""",
                    "data.locationsBySimilarName[*]",
                    object : TypeRef<List<Location>>() {})
                .block())
        .containsExactly(
            Location(id = "3", name = "France", level = 1),
            Location(id = "4", name = "Greece", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates for given level`() {
    coEvery { locationRepo.findAllByGeolocationAndLevel(1.0, 1.0, 1) } returns
        flowOf(LocationEntity(id = 3, name = "France", level = 1))

    assertThat(
            queryExecutor
                .executeAndExtractJsonPathAsObject(
                    """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0, level: 1) { id name level } }""",
                    "data.locationsByGeolocation[*]",
                    object : TypeRef<List<Location>>() {})
                .block())
        .containsExactly(Location(id = "3", name = "France", level = 1))
  }

  @Test
  fun `locationByGeolocation should return list of location containing given coordinates`() {
    coEvery { locationRepo.findAllByGeolocation(1.0, 1.0) } returns
        flowOf(LocationEntity(id = 3, name = "France", level = 1))

    assertThat(
            queryExecutor
                .executeAndExtractJsonPathAsObject(
                    """{ locationsByGeolocation(longitude: 1.0 , latitude: 1.0) { id name level } }""",
                    "data.locationsByGeolocation[*]",
                    object : TypeRef<List<Location>>() {})
                .block())
        .containsExactly(Location(id = "3", name = "France", level = 1))
  }
}
