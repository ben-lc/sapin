plugins {
  id("org.flywaydb.flyway") version "9.17.0"
  id("sacoche.common-conventions")
  id("sacoche.sql-conventions")
}

extra["scriptellaVersion"] = "1.2"

extra["postgresDriverVersion"] = "42.5.0"

extra["scriptellaVersion"] = "1.2"

val scriptella by configurations.creating

dependencies {
  scriptella("org.scriptella:scriptella-core:${property("scriptellaVersion")}")
  scriptella("org.scriptella:scriptella-tools:${property("scriptellaVersion")}")
  scriptella("org.scriptella:scriptella-drivers:${property("scriptellaVersion")}")
  scriptella("org.postgresql:postgresql:${property("postgresDriverVersion")}")
}

tasks.register("loadDatabase") {
  val includes = project.properties["includes"] ?: "*.etl.xml"
  ant.setProperty("csv.location", "../input")
  ant.setProperty("db.url", "jdbc:postgresql://localhost/db_sapin")
  ant.setProperty("db.user", "postgres")
  ant.setProperty("db.password", "postgres")
  doLast {
    ant.withGroovyBuilder {
      "taskdef"(
          "resource" to "antscriptella.properties",
          "classpath" to configurations["scriptella"].asPath)
      "etl"("nostat" to true, "nojmx" to true) {
        "fileset"("dir" to "src/main/resources/etl", "includes" to includes)
      }
    }
  }
}

flyway {
  url = "jdbc:postgresql://localhost/db_sapin"
  user = "postgres"
  password = "postgres"
  schemas = arrayOf("sapin")
  locations = arrayOf("filesystem:../src/main/resources/db/migration")
  configurations = arrayOf("scriptella")
  cleanDisabled = false
}
