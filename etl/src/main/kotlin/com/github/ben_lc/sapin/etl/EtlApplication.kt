package com.github.ben_lc.sapin.etl

import com.github.ben_lc.sapin.etl.cli.EtlCommand
import com.github.ben_lc.sapin.etl.repository.LocationEtlRepository
import com.github.ben_lc.sapin.repository.R2dbcConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import picocli.CommandLine
import picocli.CommandLine.IFactory

/** Spring boot cli tool app with picocli. */
@SpringBootApplication
@Import(R2dbcConfig::class, LocationEtlRepository::class)
@EnableConfigurationProperties(EtlProperties::class)
class EtlApplication
@Autowired
constructor(
    private val etlCommand: EtlCommand,
    private val factory: IFactory,
    private var exitCode: Int = 0
) : CommandLineRunner, ExitCodeGenerator {
  override fun run(vararg args: String) {
    exitCode =
        CommandLine(etlCommand, factory).setCaseInsensitiveEnumValuesAllowed(true).execute(*args)
  }

  override fun getExitCode(): Int {
    return exitCode
  }
}

fun main(args: Array<String>) {
  runApplication<EtlApplication>(*args)
}
