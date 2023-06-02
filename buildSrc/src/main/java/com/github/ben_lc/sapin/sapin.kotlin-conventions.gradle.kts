import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.diffplug.spotless")
  kotlin("jvm")
}

java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> { useJUnitPlatform() }

spotless {
  kotlin { ktfmt() }
}
