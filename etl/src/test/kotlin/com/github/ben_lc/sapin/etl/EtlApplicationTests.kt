package com.github.ben_lc.sapin.etl

import com.github.ben_lc.sapin.etl.cli.EtlCommand
import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import io.mockk.verifyOrder
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
        arrayOf(
            GeopackageService.GpkgProps(
                tableName = "ADM_0",
                level = 1,
                isoIdColumn = "GID_0",
                nameColumn = "COUNTRY",
                srcId = "GID_0"),
            GeopackageService.GpkgProps(
                tableName = "ADM_1",
                level = 2,
                isoIdColumn = "ISO_1",
                nameColumn = "NAME_1",
                levelName = "TYPE_1",
                levelNameEn = "ENGTYPE_1",
                srcId = "GID_1",
                srcParentId = "GID_0"))

    every { scriptellaService.runEtl(any(), any()) } just Runs
    coEvery { geopackageService.loadLocation(any(), any(), any()) } returns Job()

    CommandLine(etlCommand, factory).execute("load-location", "/that/folder/file.gpkg")

    verifyOrder {
      scriptellaService.runEtl(match { it.endsWith("etl/location") }, "pre-load.etl.xml", null)
      scriptellaService.runEtl(match { it.endsWith("etl/location") }, "post-load.etl.xml", null)
    }
    coVerify(exactly = 1) {
      geopackageService.loadLocation(File("/that/folder/file.gpkg"), *expectedGeopkgProps)
    }
  }
}
