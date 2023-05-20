plugins {
  id("org.springframework.boot") version "3.0.6"
  id("org.graalvm.buildtools.native") version "0.9.20"
  id("sacoche.common-conventions")
  id("sacoche.kotlin-conventions")
  id("sacoche.sql-conventions")
}

group = "fr.sacoche"

version = "0.0.1-SNAPSHOT"

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

extra["testcontainersVersion"] = "1.18.0"

repositories { mavenCentral() }

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-graphql")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.flywaydb:flyway-core")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.springframework:spring-jdbc")
  runtimeOnly("org.postgresql:postgresql")
  runtimeOnly("org.postgresql:r2dbc-postgresql")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.graphql:spring-graphql-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:r2dbc")
}

dependencyManagement {
  imports { mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}") }
}
