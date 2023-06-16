plugins {
  id("com.netflix.dgs.codegen") version "5.11.1"
  id("sapin.common-conventions")
  id("sapin.kotlin-conventions")
  id("sapin.spring-conventions")
}

repositories { mavenCentral() }

dependencies {
  implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:7.2.0"))

  implementation(project(":repository"))
  implementation("com.netflix.graphql.dgs:graphql-dgs-webflux-starter")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("com.ninja-squad:springmockk:${property("springMockVersion")}")
}

spotless {
  format("graphql") {
    target("src/main/resources/schema/*.graphqls")
    prettier(mapOf("prettier" to "2.8.8"))
  }
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
  schemaPaths = mutableListOf("${projectDir}/src/main/resources/schema")
  packageName = "com.github.ben_lc.sapin.generated.graphql"
  generateClientv2 = true
}
