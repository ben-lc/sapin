package com.github.ben_lc.sapin.etl

import com.github.ben_lc.sapin.etl.cli.EtlCommand
import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import com.github.ben_lc.sapin.model.Location
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import java.io.File
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
    CommandLine(etlCommand, factory).execute("load-taxon", "/that/folder", "toto")
    every { scriptellaService.runEtl(any(), any(), any()) } just Runs
    verify {
      scriptellaService.runEtl(File("/that/folder"), match { it.endsWith("etl/taxon") }, "toto")
    }
  }

  @Test
  fun `Last Command line parameter for subcommand load-taxon should be defaulted of not set and call scriptellaService#runEtl`() {
    CommandLine(etlCommand, factory).execute("load-taxon", "/that/folder")
    every { scriptellaService.runEtl(any(), any(), any()) } just Runs
    verify {
      scriptellaService.runEtl(
          File("/that/folder"), match { it.endsWith("etl/taxon") }, "*.etl.xml")
    }
  }

  @Test
  fun `Command line parameters for subcommand load-location should be parsed and call geopackageService#loadLocation`() {
    val expectedGeopkgProps =
        GeopackageService.GpkgProps(
            tableName = "ADM_0",
            level = Location.Level.COUNTRY,
            isoIdColumn = "GID_0",
            nameColumn = "COUNTRY")
    CommandLine(etlCommand, factory).execute("load-location", "/that/folder/file.gpkg")
    coEvery { geopackageService.loadLocation(any(), any()) } just Runs
    coVerify { geopackageService.loadLocation(File("/that/folder/file.gpkg"), expectedGeopkgProps) }
  }
}
