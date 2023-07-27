package com.github.ben_lc.sapin.etl.cli

import com.github.ben_lc.sapin.etl.EtlProperties
import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import java.io.File
import java.util.concurrent.Callable
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import picocli.CommandLine.*

val logger = LoggerFactory.getLogger(EtlCommand::class.java)
/** Picocli commands configuration. */
@Component
@Command(
    name = "etl",
    description = ["sapin ETL CLI tool used to load, transform and extract data"],
    mixinStandardHelpOptions = true)
class EtlCommand(
    val geopackageService: GeopackageService,
    val scriptellaService: ScriptellaService,
    val etlProps: EtlProperties
) : Callable<Int> {

  @Spec val spec: Model.CommandSpec? = null

  @Command(
      name = "load-location",
      description = ["Load location data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadLocation(
      @Parameters(
          paramLabel = "<gpkg file>",
          description = ["Geopackage file containing locations to load"])
      gpkg: File
  ) {
    if (etlProps.load.geopackage.location == null) {
      logger.error(
          "Missing configuration 'etl.log.geopackage.location in provided application.yml'")
    }
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/location"), "pre-load.etl.xml")
    runBlocking { geopackageService.loadLocation(gpkg, etlProps.load.geopackage.location!!) }
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/location"), "post-load.etl.xml")
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
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/taxon"), configFilePattern, dataFolder)
  }

  @Command(
      name = "load-natural-area",
      description = ["Load natural area data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadNaturalArea(
      @Parameters(
          paramLabel = "<gpkg file>",
          description = ["Geopackage file containing nature areas to load"])
      gpkg: File
  ) {
    if (etlProps.load.geopackage.naturalArea == null) {
      logger.error(
          "Missing configuration 'etl.log.geopackage.natureArea in provided application.yml'")
    }
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/naturalArea"), "pre-load.etl.xml")
    runBlocking { geopackageService.loadNatureArea(gpkg, etlProps.load.geopackage.naturalArea!!) }
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/naturalArea"), "post-load.etl.xml")
  }

  @Command(
      name = "load-natural-area-type",
      description = ["Load natural area type data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadNaturalAreaType(
      @Parameters(paramLabel = "<data folder>", description = ["Folder containing data to load"])
      dataFolder: File,
      @Parameters(
          description = ["Ant file pattern to select scriptella etl files to run e.g: 1-1*"],
          paramLabel = "<file pattern>",
          defaultValue = "*.etl.xml")
      configFilePattern: String
  ) {
    scriptellaService.runEtl(
        File(etlProps.scriptella.config.location.file, "/naturalAreaType"),
        configFilePattern,
        dataFolder)
  }

  override fun call(): Int = throw ParameterException(spec?.commandLine(), "Specify a subcommand")
}
