package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.TaxonEntity
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
@Import(R2dbcConfig::class, TaxonRepository::class)
@Sql("taxon-data.sql")
@Sql("clean-taxon-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TaxonRepositoryTests {

  @Autowired lateinit var taxonRepo: TaxonRepository

  @Test
  fun `findById should return taxon matching given id`(): Unit = runBlocking {
    Assertions.assertThat(taxonRepo.findById(1847746))
        .isEqualTo(
            TaxonEntity(
                id = 1847746,
                srcNameId = "2480444",
                parentId = 522064,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Haliaeetus Savigny, 1809"))
  }

  @Test
  fun `findAllByIdIn should return flow of taxon matching given ids`(): Unit = runBlocking {
    Assertions.assertThat(taxonRepo.findAllByIdIn(listOf(1847746, 753968)).toList())
        .containsExactlyInAnyOrder(
            TaxonEntity(
                id = 1847746,
                srcNameId = "2480444",
                parentId = 522064,
                rank = TaxonEntity.Rank.GENUS,
                acceptedName = "Haliaeetus Savigny, 1809"),
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"))
  }

  @Test
  fun `findAllVernacularNameById should return flow of vernacular name matching given taxon ids`():
      Unit = runBlocking {
    Assertions.assertThat(taxonRepo.findVernacularNamesByIdIn(listOf(1847746, 753968)).toList())
        .containsExactlyInAnyOrder(
            TaxonEntity.VernacularName(name = "Fish Eagles", language = "en", taxonId = 1847746),
            TaxonEntity.VernacularName(name = "havsörnar", language = "sv", taxonId = 1847746),
            TaxonEntity.VernacularName(name = "Havørn", language = "da", taxonId = 753968),
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968),
            TaxonEntity.VernacularName(
                name = "pygargue à queue blanche", language = "fr", taxonId = 753968))
  }

  @Test
  fun `findVernacularNameBySimilarName should return flow of vernacular name matching given name`():
      Unit = runBlocking {
    Assertions.assertThat(
            taxonRepo.findVernacularNamesBySimilarName(name = "fish", language = "en").toList())
        .containsExactlyInAnyOrder(
            TaxonEntity.VernacularName(name = "Fish Eagles", language = "en", taxonId = 1847746),
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968))

    Assertions.assertThat(
            taxonRepo.findVernacularNamesBySimilarName(name = "white", language = "en").toList())
        .containsExactlyInAnyOrder(
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968),
            TaxonEntity.VernacularName(name = "Fish Eagles", language = "en", taxonId = 1847746))
  }

  @Test
  fun `findVernacularNameBySimilarName should return flow of vernacular name matching given name and rank`():
      Unit = runBlocking {
    Assertions.assertThat(
            taxonRepo
                .findVernacularNamesBySimilarName(
                    name = "fish", language = "en", rank = TaxonEntity.Rank.SPECIES)
                .toList())
        .containsExactlyInAnyOrder(
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968))
  }

  @Test
  fun `findVernacularNameBySimilarName should return flow of vernacular name matching given name and gteRank`():
      Unit = runBlocking {
    Assertions.assertThat(
            taxonRepo
                .findVernacularNamesBySimilarName(
                    name = "fish", language = "en", gteRank = TaxonEntity.Rank.GENUS)
                .toList())
        .containsExactlyInAnyOrder(
            TaxonEntity.VernacularName(name = "Fish Eagles", language = "en", taxonId = 1847746),
            TaxonEntity.VernacularName(
                name = "White-tailed Eagle", language = "en", taxonId = 753968))
  }

  @Test
  fun `findParentsById should return flow of taxon parents given its id`(): Unit = runBlocking {
    Assertions.assertThat(taxonRepo.findParentsById(1035228).toList())
        .containsExactly(
            TaxonEntity(
                id = 2489965,
                srcNameId = "1",
                parentId = null,
                rank = TaxonEntity.Rank.KINGDOM,
                acceptedName = "Animalia"),
            TaxonEntity(
                id = 1609069,
                srcNameId = "44",
                parentId = 2489965,
                rank = TaxonEntity.Rank.PHYLUM,
                acceptedName = "Chordata"))
  }

  @Test
  fun `findChildrenByIdIn should return flow of taxon children given given list of taxon ids`():
      Unit = runBlocking {
    Assertions.assertThat(taxonRepo.findChildrenByIdIn(listOf(1847746, 1892778)).toList())
        .containsExactlyInAnyOrder(
            TaxonEntity(
                id = 753968,
                srcNameId = "2480449",
                parentId = 1847746,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Haliaeetus albicilla (Linnaeus, 1758)"),
            TaxonEntity(
                id = 162022,
                srcNameId = "2481047",
                parentId = 1892778,
                rank = TaxonEntity.Rank.SPECIES,
                acceptedName = "Falco peregrinus Tunstall, 1771"))
  }
}
