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

extra["geotoolsVersion"] = "28.2"

extra["picocliVersion"] = "4.7.0"

val scriptella by configurations.creating

dependencies {
  implementation(project(":repository"))
  implementation("org.geotools.jdbc:gt-jdbc-postgis:${property("geotoolsVersion")}")
  implementation("org.geotools:gt-geopkg:${property("geotoolsVersion")}")
  implementation("info.picocli:picocli-spring-boot-starter:${property("picocliVersion")}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.scriptella:scriptella-core:${property("scriptellaVersion")}")
  implementation("org.scriptella:scriptella-tools:${property("scriptellaVersion")}")
  implementation("org.scriptella:scriptella-drivers:${property("scriptellaVersion")}")
  runtimeOnly("org.postgresql:postgresql")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("com.ninja-squad:springmockk:${property("springMockVersion")}")
}

flyway {
  url = "jdbc:postgresql://localhost/db_sapin"
  user = "postgres"
  password = "postgres"
  schemas = arrayOf("sapin")
  locations = arrayOf("filesystem:../src/main/resources/db/migration")
  cleanDisabled = false
}

spotless {
  format("xml") {
    target("**/*.xml")
    prettier(mapOf("prettier" to "2.8.8", "@prettier/plugin-xml" to "2.2.0"))
        .config(mapOf("xmlWhitespaceSensitivity" to "ignore"))
  }
}
