package com.github.ben_lc.sapin.etl.cli

import com.github.ben_lc.sapin.etl.cli.EtlCommand.DataType.LOCATION
import com.github.ben_lc.sapin.etl.service.GeopackageService
import com.github.ben_lc.sapin.etl.service.GeopackageService.GpkgProps
import com.github.ben_lc.sapin.model.Location
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.File
import java.util.concurrent.Callable

/**
 * Picocli commands configuration.
 */
@Component
@Command(
    name = "etl",
    description = ["sapin ETL CLI tool used to load, transform and extract data"],
    mixinStandardHelpOptions = true)
class EtlCommand @Autowired constructor(val geopackageService: GeopackageService) : Callable<Int> {

  @CommandLine.Spec val spec: CommandLine.Model.CommandSpec? = null

  enum class DataType {
    LOCATION
  }

  @Command(name = "load", description = ["Load data into sapin"])
  fun load(
      @CommandLine.Parameters(
          description = ["Type of data to load, valid values: \${COMPLETION-CANDIDATES}"],
          paramLabel = "<data type>")
      dataType: DataType,
      @CommandLine.Parameters(
          paramLabel = "<folder>", description = ["Folder containing data to load"])
      loadFolder: File
  ) {
    when (dataType) {
      LOCATION ->
          runBlocking {
            geopackageService.loadLocation(
                loadFolder,
                GpkgProps(
                    tableName = "ADM_0",
                    level = Location.Level.COUNTRY,
                    isoIdColumn = "GID_0",
                    nameColumn = "COUNTRY"))
          }
    }
  }
  override fun call(): Int =
      throw CommandLine.ParameterException(spec?.commandLine(), "Specify a subcommand")
}
