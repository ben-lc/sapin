plugins {
  id("com.diffplug.spotless")
  id("com.github.ben-manes.versions")
}

repositories {
  mavenCentral()
}

spotless {
  kotlinGradle { ktfmt() }
}
