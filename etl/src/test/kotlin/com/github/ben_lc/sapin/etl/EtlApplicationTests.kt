package com.github.ben_lc.sapin.etl

import com.github.ben_lc.sapin.etl.cli.EtlCommand
import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import java.io.File
import kotlinx.coroutines.Job
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import picocli.CommandLine

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
internal class EtlApplicationTests {

  @Autowired lateinit var factory: CommandLine.IFactory

  @Autowired lateinit var etlCommand: EtlCommand

  @MockkBean lateinit var scriptellaService: ScriptellaService

  @MockkBean lateinit var geopackageService: GeopackageService

  @Test
  fun `Command line parameters for subcommand load-taxon should be parsed and call scriptellaService#runEtl`() {
    every { scriptellaService.runEtl(any(), any(), any()) } just Runs

    CommandLine(etlCommand, factory).execute("load-taxon", "/that/folder", "toto")

    verify {
      scriptellaService.runEtl(match { it.endsWith("etl/taxon") }, "toto", File("/that/folder"))
    }
  }

  @Test
  fun `Last Command line parameter for subcommand load-taxon should be defaulted of not set and call scriptellaService#runEtl`() {
    every { scriptellaService.runEtl(any(), any(), any()) } just Runs

    CommandLine(etlCommand, factory).execute("load-taxon", "/that/folder")

    verify {
      scriptellaService.runEtl(
          match { it.endsWith("etl/taxon") }, "*.etl.xml", File("/that/folder"))
    }
  }

  @Test
  fun `Command line parameters for subcommand load-location should be parsed and call geopackageService#loadLocation`() {
    val expectedGeopkgProps =
        listOf(
            LocationGeopackageProperties(
                tableName = "TABLE_0",
                level = 1,
                isoIdColumnName = "ID_COL",
                nameColumnName = "NAME_COL",
                srcIdColumnName = "CODE_COL"),
            LocationGeopackageProperties(
                tableName = "TABLE_1",
                level = 2,
                isoIdColumnName = "ID_COL2",
                nameColumnName = "NAME_COL2",
                levelNameColumnName = "LEVEL2",
                levelNameEnColumnName = "LEVEL_EN2",
                srcIdColumnName = "CODE_COL2",
                srcParentIdColumnName = "CODE_COL"))

    every { scriptellaService.runEtl(any(), any()) } just Runs
    coEvery { geopackageService.loadLocation(any(), any()) } returns Job()

    CommandLine(etlCommand, factory).execute("load-location", "/that/folder/file.gpkg")

    verifyOrder {
      scriptellaService.runEtl(match { it.endsWith("etl/location") }, "pre-load.etl.xml", null)
      scriptellaService.runEtl(match { it.endsWith("etl/location") }, "post-load.etl.xml", null)
    }
    coVerify(exactly = 1) {
      geopackageService.loadLocation(File("/that/folder/file.gpkg"), expectedGeopkgProps)
    }
  }
}
