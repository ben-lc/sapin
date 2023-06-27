package com.github.ben_lc.sapin.graphql.datafetcher

import com.github.ben_lc.sapin.graphql.type.Taxon
import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity
import com.github.ben_lc.sapin.repository.TaxonRepository
import com.github.ben_lc.sapin.repository.TaxonScientificNameRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester

@GraphQlTest(controllers = [TaxonController::class])
class TaxonControllerTests {

  @Autowired lateinit var tester: GraphQlTester
  @MockkBean lateinit var taxonRepo: TaxonRepository
  @MockkBean lateinit var taxonNameRepo: TaxonScientificNameRepository
  @Test
  fun `taxonById should return taxon matching given id`() {
    coEvery { taxonRepo.findById(753968) } returns
        TaxonEntity(
            id = 753968,
            srcNameId = "2480449",
            parentId = 1847746,
            rank = TaxonEntity.Rank.SPECIES,
            acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)")

    tester
        .document(
            """{ taxonById(id: "753968") { id srcNameId rank acceptedName } }""",
        )
        .execute()
        .path("data.taxonById")
        .entity(Taxon::class.java)
        .isEqualTo(
            Taxon(
                id = "753968",
                srcNameId = "2480449",
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"))
  }

  @Test
  fun `taxaByIds should return list of taxon matching given ids`() {
    coEvery { taxonRepo.findAllByIdIn(listOf(753968, 1892778)) } returns
        flowOf(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 1892778,
                srcNameId = "2480996",
                parentId = 2130593,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Falco Linnaeus, 1758"))

    tester
        .document(
            """{ taxaByIds(ids: ["753968","1892778"]) { id srcNameId rank acceptedName } }""",
        )
        .execute()
        .path("data.taxaByIds")
        .matchesJsonStrictly(
            """[{"id":"753968","srcNameId":"2480449","rank":"SPECIES","acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)"},{"id":"1892778","srcNameId":"2480996","rank":"GENUS","acceptedName":"Falco Linnaeus, 1758"}]""")
  }

  @Test
  fun `vernacularNames should return vernacular names matching given taxon`() {
    coEvery { taxonRepo.findById(753968) } returns
        TaxonEntity(
            id = 753968,
            srcNameId = "2480449",
            parentId = 1847746,
            rank = TaxonEntity.Rank.SPECIES,
            acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)")
    coEvery { taxonRepo.findVernacularNamesByIdIn(listOf(753968)) } returns
        flowOf(
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968),
            TaxonEntity.VernacularName(
                name = "pygargue à queue blanche", language = "fr", taxonId = 753968))

    tester
        .document(
            """{ taxonById(id: "753968") { vernacularNames { name language } } }""",
        )
        .execute()
        .path("data.taxonById")
        .matchesJsonStrictly(
            """{"vernacularNames":[{"name":"White-tailed Eagle","language":"en"},{"name":"pygargue à queue blanche","language":"fr"}]}""")
  }

  @Test
  fun `scientificNames should return scientific names matching given taxon`() {
    coEvery { taxonRepo.findById(753968) } returns
        TaxonEntity(
            id = 753968,
            srcNameId = "2480449",
            parentId = 1847746,
            rank = TaxonEntity.Rank.SPECIES,
            acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)")
    coEvery { taxonNameRepo.findAllByTaxonIdIn(listOf(753968)) } returns
        flowOf(
            TaxonScientificNameEntity(
                id = 2280150,
                taxonId = 753968,
                srcId = "4408411",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Falco albicilla Linnaeus, 1758",
                acceptedNameId = 2576157),
            TaxonScientificNameEntity(
                id = 2576157,
                taxonId = 753968,
                srcId = "2480449",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                acceptedNameId = null))

    tester
        .document(
            """{ taxonById(id: "753968") { acceptedName scientificNames { id name srcId taxonomicStatus } } }""",
        )
        .execute()
        .path("data.taxonById")
        .matchesJsonStrictly(
            """{"acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)","scientificNames":[{"id":"2280150","name":"Falco albicilla Linnaeus, 1758","srcId":"4408411","taxonomicStatus":"SYNONYM"},{"id":"2576157","name":"Haliaeetus albicilla (Linnaeus, 1758)","srcId":"2480449","taxonomicStatus":"ACCEPTED"}]}""")
  }

  @Test
  fun `parent should return taxon parent for given list of taxa`() {
    coEvery { taxonRepo.findAllByIdIn(listOf(753968, 1892778)) } returns
        flowOf(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 1892778,
                srcNameId = "2480996",
                parentId = 2130593,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Falco Linnaeus, 1758"))
    coEvery { taxonRepo.findAllByIdIn(listOf(1847746, 2130593)) } returns
        flowOf(
            TaxonEntity(
                id = 1847746,
                srcNameId = "2480449",
                parentId = 522064,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Haliaeetus Savigny, 1809"),
            TaxonEntity(
                id = 2130593,
                srcNameId = "5240",
                parentId = 2034352,
                rank = TaxonEntity.Rank.FAMILY,
                acceptedName = "Falconidae"))

    tester
        .document(
            """{ taxaByIds(ids: ["753968","1892778"]) { acceptedName parent { id acceptedName } } }""",
        )
        .execute()
        .path("data.taxaByIds")
        .matchesJsonStrictly(
            """[{"acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)","parent":{"id":"1847746","acceptedName":"Haliaeetus Savigny, 1809"}},{"acceptedName":"Falco Linnaeus, 1758","parent":{"id":"2130593","acceptedName":"Falconidae"}}]""")
  }

  @Test
  fun `parents should return taxon list of parents`() {
    coEvery { taxonRepo.findById(753968) } returns
        TaxonEntity(
            id = 753968,
            srcNameId = "2480449",
            parentId = 1847746,
            rank = TaxonEntity.Rank.SPECIES,
            acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)")
    coEvery { taxonRepo.findParentsById(753968) } returns
        flowOf(
            TaxonEntity(
                id = 529475,
                srcNameId = "7191147",
                parentId = 1035228,
                rank = TaxonEntity.Rank.ORDER,
                acceptedName = "Accipitriformes"),
            TaxonEntity(
                id = 522064,
                srcNameId = "2877",
                parentId = 529475,
                rank = TaxonEntity.Rank.FAMILY,
                acceptedName = "Accipitridae"),
            TaxonEntity(
                id = 1847746,
                srcNameId = "2480449",
                parentId = 522064,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Haliaeetus Savigny, 1809"))

    tester
        .document(
            """{ taxonById(id: "753968") { parents { id acceptedName } } }""",
        )
        .execute()
        .path("data.taxonById")
        .matchesJsonStrictly(
            """{"parents":[{"id":"529475","acceptedName":"Accipitriformes"},{"id":"522064","acceptedName":"Accipitridae"},{"id":"1847746","acceptedName":"Haliaeetus Savigny, 1809"}]}""")
  }

  @Test
  fun `children should return list of taxon children for given list of taxa`() {
    coEvery { taxonRepo.findAllByIdIn(listOf(753968, 1892778)) } returns
        flowOf(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 1892778,
                srcNameId = "2480996",
                parentId = 2130593,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Falco Linnaeus, 1758"))
    coEvery { taxonRepo.findChildrenByIdIn(listOf(753968, 1892778)) } returns
        flowOf(
            TaxonEntity(
                id = 2544750,
                srcNameId = "7059939",
                parentId = 753968,
                rank = TaxonEntity.Rank.SUBSPECIES,
                acceptedName = "Haliaeetus albicilla albicilla"),
            TaxonEntity(
                id = 162022,
                srcNameId = "2481047",
                parentId = 1892778,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Falco peregrinus Tunstall, 1771"))

    tester
        .document(
            """{ taxaByIds(ids: ["753968","1892778"]) { acceptedName children { id acceptedName } } }""",
        )
        .execute()
        .path("data.taxaByIds")
        .matchesJsonStrictly(
            """[{"acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)","children":[{"id":"2544750","acceptedName":"Haliaeetus albicilla albicilla"}]},{"acceptedName":"Falco Linnaeus, 1758","children":[{"id":"162022","acceptedName":"Falco peregrinus Tunstall, 1771"}]}]""")
  }
}
