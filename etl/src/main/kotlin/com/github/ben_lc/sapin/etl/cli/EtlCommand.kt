package com.github.ben_lc.sapin.etl.cli

import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.GeopackageService.GpkgProps
import com.github.ben_lc.sapin.etl.service.ScriptellaService
import com.github.ben_lc.sapin.model.Location
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
      gpkgLocation: File
  ) {
    runBlocking {
      geopackageService.loadLocation(
          gpkgLocation,
          GpkgProps(
              tableName = "ADM_0",
              level = Location.Level.COUNTRY,
              isoIdColumn = "GID_0",
              nameColumn = "COUNTRY"))
    }
  }
  @Command(
      name = "load-taxon",
      description = ["Load taxon data into sapin database"],
      mixinStandardHelpOptions = true)
  fun loadTaxon(
      @Parameters(paramLabel = "<data folder>", description = ["Folder containing data to load"])
      dataLocation: File,
      @Parameters(
          description = ["Ant file pattern to select scriptella etl files to run e.g: 1-1*"],
          paramLabel = "<file pattern>",
          defaultValue = "*.etl.xml")
      configFilePattern: String
  ) {
    scriptellaService.runEtl(dataLocation, File(configLocation.file, "/taxon"), configFilePattern)
  }

  override fun call(): Int = throw ParameterException(spec?.commandLine(), "Specify a subcommand")
}
