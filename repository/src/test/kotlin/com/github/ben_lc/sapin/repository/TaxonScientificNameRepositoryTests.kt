package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity
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
@Import(R2dbcConfig::class, TaxonScientificNameRepository::class)
@Sql("load-taxon-scientificname-data.sql")
@Sql("clean-taxon-scientificname-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TaxonScientificNameRepositoryTests {
  @Autowired lateinit var taxonNameRepo: TaxonScientificNameRepository

  @Test
  fun `findById should return scientific name matching given id`(): Unit = runBlocking {
    Assertions.assertThat(taxonNameRepo.findById(2280150))
        .isEqualTo(
            TaxonScientificNameEntity(
                id = 2280150,
                taxonId = 753968,
                srcId = "4408411",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Falco albicilla Linnaeus, 1758",
                acceptedNameId = 2576157))
  }

  @Test
  fun `findAllByIdIn should return flow of scientific name matching given ids`(): Unit =
      runBlocking {
        Assertions.assertThat(taxonNameRepo.findAllByIdIn(listOf(2280150, 2576158)).toList())
            .containsExactlyInAnyOrder(
                TaxonScientificNameEntity(
                    id = 2280150,
                    taxonId = 753968,
                    srcId = "4408411",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                    name = "Falco albicilla Linnaeus, 1758",
                    acceptedNameId = 2576157),
                TaxonScientificNameEntity(
                    id = 2576158,
                    taxonId = 2544750,
                    srcId = "7059939",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                    name = "Haliaeetus albicilla albicilla",
                    acceptedNameId = null))
      }

  @Test
  fun `findAllBySimilarName should return scientific names matching given name`(): Unit =
      runBlocking {
        Assertions.assertThat(
                taxonNameRepo.findAllBySimilarName(name = "falco albicil", size = 3).toList())
            .containsExactly(
                TaxonScientificNameEntity(
                    id = 2280150,
                    taxonId = 753968,
                    srcId = "4408411",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                    name = "Falco albicilla Linnaeus, 1758",
                    acceptedNameId = 2576157),
                TaxonScientificNameEntity(
                    id = 2576158,
                    taxonId = 2544750,
                    srcId = "7059939",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                    name = "Haliaeetus albicilla albicilla",
                    acceptedNameId = null),
                TaxonScientificNameEntity(
                    id = 2576181,
                    taxonId = 753968,
                    srcId = "11358002",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                    name = "Haliaetus albicilla (L.)",
                    acceptedNameId = 2576157))
      }

  @Test
  fun `findAllBySimilarName should return scientific names matching given name and rank`(): Unit =
      runBlocking {
        Assertions.assertThat(
                taxonNameRepo
                    .findAllBySimilarName(name = "falco albicil", rank = TaxonEntity.Rank.SPECIES)
                    .toList())
            .containsExactly(
                TaxonScientificNameEntity(
                    id = 2280150,
                    taxonId = 753968,
                    srcId = "4408411",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                    name = "Falco albicilla Linnaeus, 1758",
                    acceptedNameId = 2576157),
                TaxonScientificNameEntity(
                    id = 2576181,
                    taxonId = 753968,
                    srcId = "11358002",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                    name = "Haliaetus albicilla (L.)",
                    acceptedNameId = 2576157),
                TaxonScientificNameEntity(
                    id = 2576157,
                    taxonId = 753968,
                    srcId = "2480449",
                    taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                    name = "Haliaeetus albicilla (Linnaeus, 1758)",
                    acceptedNameId = null))
      }

  @Test
  fun `findAllBySimilarName should return scientific names matching given name and gteRank`():
      Unit = runBlocking {
    Assertions.assertThat(
            taxonNameRepo
                .findAllBySimilarName(name = "falco albicil", gteRank = TaxonEntity.Rank.SPECIES)
                .toList())
        .containsExactly(
            TaxonScientificNameEntity(
                id = 2280150,
                taxonId = 753968,
                srcId = "4408411",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Falco albicilla Linnaeus, 1758",
                acceptedNameId = 2576157),
            TaxonScientificNameEntity(
                id = 2576158,
                taxonId = 2544750,
                srcId = "7059939",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                name = "Haliaeetus albicilla albicilla",
                acceptedNameId = null),
            TaxonScientificNameEntity(
                id = 2576181,
                taxonId = 753968,
                srcId = "11358002",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Haliaetus albicilla (L.)",
                acceptedNameId = 2576157),
            TaxonScientificNameEntity(
                id = 2576157,
                taxonId = 753968,
                srcId = "2480449",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                acceptedNameId = null))
  }

  @Test
  fun `findAllByTaxonIdIn should return scientific names matching taxon id`(): Unit = runBlocking {
    Assertions.assertThat(taxonNameRepo.findAllByTaxonIdIn(listOf(753968, 2544750)).toList())
        .containsExactlyInAnyOrder(
            TaxonScientificNameEntity(
                id = 2280150,
                taxonId = 753968,
                srcId = "4408411",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Falco albicilla Linnaeus, 1758",
                acceptedNameId = 2576157),
            TaxonScientificNameEntity(
                id = 2576158,
                taxonId = 2544750,
                srcId = "7059939",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                name = "Haliaeetus albicilla albicilla",
                acceptedNameId = null),
            TaxonScientificNameEntity(
                id = 2576181,
                taxonId = 753968,
                srcId = "11358002",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.SYNONYM,
                name = "Haliaetus albicilla (L.)",
                acceptedNameId = 2576157),
            TaxonScientificNameEntity(
                id = 2576157,
                taxonId = 753968,
                srcId = "2480449",
                taxonomicStatus = TaxonScientificNameEntity.TaxonomicStatus.ACCEPTED,
                name = "Haliaeetus albicilla (Linnaeus, 1758)",
                acceptedNameId = null))
  }
}
