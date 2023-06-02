plugins {
  id("org.springframework.boot")
  id("org.flywaydb.flyway") version "9.17.0"
  id("sapin.common-conventions")
  id("sapin.sql-conventions")
  id("sapin.kotlin-conventions")
  id("sapin.spring-conventions")
}

repositories {
  maven { url = uri("https://repo.osgeo.org/repository/release") }
  mavenCentral()
}

extra["scriptellaVersion"] = "1.2"

extra["scriptellaVersion"] = "1.2"

extra["geotoolsVersion"] = "28.2"

extra["picocliVersion"] = "4.7.0"

val scriptella by configurations.creating

dependencies {
  scriptella("org.scriptella:scriptella-core:${property("scriptellaVersion")}")
  scriptella("org.scriptella:scriptella-tools:${property("scriptellaVersion")}")
  scriptella("org.scriptella:scriptella-drivers:${property("scriptellaVersion")}")
  scriptella("org.postgresql:postgresql")

  implementation(project(":repository"))
  implementation("org.geotools.jdbc:gt-jdbc-postgis:${property("geotoolsVersion")}")
  // implementation("org.geotools:gt-epsg-hsql:${property("geotoolsVersion")}")
  implementation("org.geotools:gt-geopkg:${property("geotoolsVersion")}")
  implementation("info.picocli:picocli-spring-boot-starter:${property("picocliVersion")}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
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

spotless {
  format("xml") {
    target("**/*.xml")
    prettier(mapOf("prettier" to "2.8.8", "@prettier/plugin-xml" to "2.2.0"))
        .config(mapOf("xmlWhitespaceSensitivity" to "ignore"))
  }
}
