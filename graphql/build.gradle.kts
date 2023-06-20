plugins {
  id("sapin.common-conventions")
  id("sapin.kotlin-conventions")
  id("sapin.spring-conventions")
}

repositories { mavenCentral() }

dependencies {
  implementation(project(":repository"))
  implementation("org.springframework.boot:spring-boot-starter-graphql")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  testImplementation("org.springframework.graphql:spring-graphql-test")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("com.ninja-squad:springmockk:${property("springMockVersion")}")
}

spotless {
  format("graphql") {
    target("src/main/resources/graphql/*.graphqls")
    prettier(mapOf("prettier" to "2.8.8"))
  }
}
