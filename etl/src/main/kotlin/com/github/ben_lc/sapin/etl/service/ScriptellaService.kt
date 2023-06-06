package com.github.ben_lc.sapin.etl.service

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher
import scriptella.execution.EtlExecutor
import scriptella.interactive.ConsoleProgressIndicator
import java.io.File

@Service
class ScriptellaService(val r2dbcProperties: R2dbcProperties) {

  val logger = LoggerFactory.getLogger(ScriptellaService::class.java)
  fun runEtl(inputFolder: File, configLocation: File, configFilePattern: String = "") {
    val properties =
        mapOf(
            "csv.location" to inputFolder.absolutePath,
            "db.url" to r2dbcProperties.url.replace("r2dbc", "jdbc"),
            "db.user" to r2dbcProperties.username,
            "db.password" to r2dbcProperties.password)

    val antPathMatcher = AntPathMatcher()
    val antPattern =
        if (configFilePattern.endsWith("etl.xml")) configFilePattern
        else antPathMatcher.combine("*.etl.xml", configFilePattern)

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
