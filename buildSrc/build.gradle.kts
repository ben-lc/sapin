plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.6")
  implementation("io.spring.gradle:dependency-management-plugin:1.1.0")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.18.0")
  implementation("com.github.ben-manes:gradle-versions-plugin:0.46.0")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
  implementation("org.jetbrains.kotlin:kotlin-allopen:1.7.22")
}
