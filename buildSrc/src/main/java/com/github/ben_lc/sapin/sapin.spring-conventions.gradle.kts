plugins {
  id("io.spring.dependency-management")
  kotlin("plugin.spring")
}

extra["testcontainersVersion"] = "1.18.0"

dependencyManagement {
  imports { mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES) }
  imports { mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}") }
}
