plugins {
  id("sapin.common-conventions")
  id("sapin.kotlin-conventions")
  id("sapin.spring-conventions")
  id("sapin.sql-conventions")
}

repositories { mavenCentral() }

dependencies {
  api(project(":model"))
  api("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.postgresql:r2dbc-postgresql")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
}
