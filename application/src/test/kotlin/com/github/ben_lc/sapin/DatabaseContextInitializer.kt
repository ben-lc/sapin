package com.github.ben_lc.sapin

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class DatabaseContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

  override fun initialize(context: ConfigurableApplicationContext) {
    database.start()
    TestPropertyValues.of(
            "spring.r2dbc.url=${database.jdbcUrl.replace("jdbc", "r2dbc")}",
            "spring.r2dbc.username=${database.username}",
            "spring.r2dbc.password=${database.password}",
            "spring.r2dbc.name=db_sapin")
        .applyTo(context.environment)
  }

  private companion object {
    val database: KPostgreSQLContainer = KPostgreSQLContainer("postgis/postgis:15-3.3-alpine")
  }

  internal class KPostgreSQLContainer(image: String) :
      PostgreSQLContainer<KPostgreSQLContainer>(
          DockerImageName.parse(image).asCompatibleSubstituteFor("postgres"))
}
