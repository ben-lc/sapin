package com.github.ben_lc.sapin.etl.service

import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher
import scriptella.execution.EtlExecutor
import scriptella.interactive.ConsoleProgressIndicator

@Service
class ScriptellaService(val r2dbcProperties: R2dbcProperties) {

  val logger: Logger = LoggerFactory.getLogger(ScriptellaService::class.java)
  fun runEtl(configLocation: File, configFilePattern: String? = null, inputFolder: File? = null) {
    val properties =
        mapOf(
            "csv.location" to inputFolder?.absolutePath,
            "db.url" to r2dbcProperties.url.replace("r2dbc", "jdbc"),
            "db.user" to r2dbcProperties.username,
            "db.password" to r2dbcProperties.password)

    val antPathMatcher = AntPathMatcher()
    val antPattern =
        when {
          configFilePattern == null -> "*.etl.xml"
          configFilePattern.endsWith("etl.xml") -> configFilePattern
          else -> antPathMatcher.combine("*.etl.xml", configFilePattern)
        }

    configLocation
        .walkTopDown()
        .filter { antPathMatcher.match(antPattern, it.relativeTo(configLocation).path) }
        .sorted()
        .forEach {
          logger.info("Run ETL config: ${it.name}")
          val executor = EtlExecutor.newExecutor(it.toURI().toURL(), properties)
          val stats = executor.execute(ConsoleProgressIndicator())
          logger.info(stats.toString())
        }
  }
}
