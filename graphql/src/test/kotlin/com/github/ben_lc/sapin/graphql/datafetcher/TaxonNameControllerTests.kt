package com.github.ben_lc.sapin.graphql.datafetcher

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

@GraphQlTest(controllers = [TaxonNameController::class])
class TaxonNameControllerTests {
  @Autowired lateinit var tester: GraphQlTester
  @MockkBean lateinit var taxonRepository: TaxonRepository

  @MockkBean lateinit var taxonNameRepo: TaxonScientificNameRepository

  @Test
  fun `searchTaxonNames should return taxon names matching given parameters`() {
    coEvery { taxonNameRepo.findAllBySimilarName("haliae", TaxonEntity.Rank.SPECIES) } returns
        flowOf(
            TaxonScientificNameEntity(
                id = 5,
                srcId = "564",
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                taxonId = 4,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                acceptedNameId = null))
    coEvery {
      taxonRepository.findVernacularNamesBySimilarName(
          name = "haliae", language = "fr", rank = TaxonEntity.Rank.SPECIES)
    } returns
        flowOf(TaxonEntity.VernacularName("Pygargue à queue blanche", language = "fr", taxonId = 5))

    tester
        .document(
            """{ searchTaxonNames(name: "haliae", rank: SPECIES, includeScientificName: true, includeVernacularNameLang: "fr") { __typename name } }""",
        )
        .execute()
        .path("data.searchTaxonNames")
        .matchesJsonStrictly(
            """[{"__typename":"TaxonScientificName","name":"Haliaeetus albicilla (Linnaeus, 1758)"},{"__typename":"TaxonVernacularName","name":"Pygargue à queue blanche"}]""")
  }

  @Test
  fun `taxonFromVernacularName should return taxa matching given collection of verncular names`() {
    coEvery {
      taxonRepository.findVernacularNamesBySimilarName(name = "pygargue", language = "fr")
    } returns
        flowOf(
            TaxonEntity.VernacularName(
                "Pygargue à queue blanche", language = "fr", taxonId = 753968),
            TaxonEntity.VernacularName(
                "Pygargue à tête blanche", language = "fr", taxonId = 753969))

    coEvery { taxonRepository.findAllByIdIn(listOf(753968, 753969)) } returns
        flowOf(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 753969,
                srcNameId = "2480450",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus leucocephalus (Linnaeus, 1758)"))

    tester
        .document(
            """{ searchTaxonNames(name: "pygargue", includeScientificName: false, includeVernacularNameLang: "fr") { name taxon { id acceptedName } } }""",
        )
        .execute()
        .path("data.searchTaxonNames")
        .matchesJsonStrictly(
            """[{"name":"Pygargue à queue blanche","taxon":{"id":"753968","acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)"}},{"name":"Pygargue à tête blanche","taxon":{"id":"753969","acceptedName":"Haliaeetus leucocephalus (Linnaeus, 1758)"}}]""")
  }

  @Test
  fun `taxonFromScientificName should return taxa matching given collection of scientific names`() {
    coEvery { taxonNameRepo.findAllBySimilarName("Haliaeetus") } returns
        flowOf(
            TaxonScientificNameEntity(
                id = 4,
                srcId = "563",
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                taxonId = 753968,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                acceptedNameId = null),
            TaxonScientificNameEntity(
                id = 5,
                srcId = "564",
                name = "Haliaeetus leucocephalus (Linnaeus, 1758)",
                taxonId = 753969,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                acceptedNameId = null))
    coEvery { taxonRepository.findAllByIdIn(listOf(753968, 753969)) } returns
        flowOf(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 753969,
                srcNameId = "2480450",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus leucocephalus (Linnaeus, 1758)"))

    tester
        .document(
            """{ searchTaxonNames(name: "Haliaeetus", includeScientificName: true) { name taxon { id acceptedName } } }""",
        )
        .execute()
        .path("data.searchTaxonNames")
        .matchesJsonStrictly(
            """[{"name":"Haliaeetus albicilla (Linnaeus, 1758)","taxon":{"id":"753968","acceptedName":"Haliaeetus albicilla (Linnaeus, 1758)"}},{"name":"Haliaeetus leucocephalus (Linnaeus, 1758)","taxon":{"id":"753969","acceptedName":"Haliaeetus leucocephalus (Linnaeus, 1758)"}}]""")
  }

  @Test
  fun `acceptedName should return taxon accepted scientific names matching given collection of scientific names`() {
    coEvery { taxonNameRepo.findAllBySimilarName("Haliaeetus") } returns
        flowOf(
            TaxonScientificNameEntity(
                id = 4,
                srcId = "563",
                name = "Falco albicilla (Linnaeus, 1758)",
                taxonId = 753968,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                acceptedNameId = 666),
            TaxonScientificNameEntity(
                id = 5,
                srcId = "564",
                name = "Falco leucocephalus (Linnaeus, 1758)",
                taxonId = 753969,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                acceptedNameId = 667))
    coEvery { taxonNameRepo.findAllByIdIn(listOf(666, 667)) } returns
        flowOf(
            TaxonScientificNameEntity(
                id = 666,
                srcId = "564",
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                taxonId = 753969,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                acceptedNameId = null),
            TaxonScientificNameEntity(
                id = 667,
                srcId = "564",
                name = "Haliaeetus leucocephalus (Linnaeus, 1758)",
                taxonId = 753969,
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                acceptedNameId = null))

    tester
        .document(
            """{ searchTaxonNames(name: "Haliaeetus", includeScientificName: true) { name ... on TaxonScientificName { acceptedName { id name } } } }""",
        )
        .execute()
        .path("data.searchTaxonNames")
        .matchesJsonStrictly(
            """[{"name":"Falco albicilla (Linnaeus, 1758)","acceptedName":{"id":"666","name":"Haliaeetus albicilla (Linnaeus, 1758)"}},{"name":"Falco leucocephalus (Linnaeus, 1758)","acceptedName":{"id":"667","name":"Haliaeetus leucocephalus (Linnaeus, 1758)"}}]""")
  }
}
