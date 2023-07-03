package com.github.ben_lc.sapin.etl.cli

import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.GeopackageService.GpkgProps
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import java.io.File
import java.util.concurrent.Callable
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import picocli.CommandLine.*

/** Picocli commands configuration. */
@Component
@Command(
    name = "etl",
    description = ["sapin ETL CLI tool used to load, transform and extract data"],
    mixinStandardHelpOptions = true)
class EtlCommand(
    val geopackageService: GeopackageService,
    val scriptellaService: ScriptellaService
) : Callable<Int> {

  @Spec val spec: Model.CommandSpec? = null

  @Value("\${scriptella.config.location}") lateinit var configLocation: Resource

  @Command(
      name = "load-location",
      description = ["Load location data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadLocation(
      @Parameters(paramLabel = "<gpkg file>", description = ["Geopackage file location to load"])
      gpkg: File
  ) {
    scriptellaService.runEtl(File(configLocation.file, "/location"), "pre-load.etl.xml")
    runBlocking {
      geopackageService.loadLocation(
          gpkg,
          GpkgProps(
              tableName = "ADM_0",
              level = 1,
              isoIdColumn = "GID_0",
              nameColumn = "COUNTRY",
              srcId = "GID_0"),
          GpkgProps(
              tableName = "ADM_1",
              level = 2,
              isoIdColumn = "ISO_1",
              nameColumn = "NAME_1",
              levelName = "TYPE_1",
              levelNameEn = "ENGTYPE_1",
              srcId = "GID_1",
              srcParentId = "GID_0"))
    }
    scriptellaService.runEtl(File(configLocation.file, "/location"), "post-load.etl.xml")
  }
  @Command(
      name = "load-taxon",
      description = ["Load taxon data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadTaxon(
      @Parameters(paramLabel = "<data folder>", description = ["Folder containing data to load"])
      dataFolder: File,
      @Parameters(
          description = ["Ant file pattern to select scriptella etl files to run e.g: 1-1*"],
          paramLabel = "<file pattern>",
          defaultValue = "*.etl.xml")
      configFilePattern: String
  ) {
    scriptellaService.runEtl(File(configLocation.file, "/taxon"), configFilePattern, dataFolder)
  }

  override fun call(): Int = throw ParameterException(spec?.commandLine(), "Specify a subcommand")
}
