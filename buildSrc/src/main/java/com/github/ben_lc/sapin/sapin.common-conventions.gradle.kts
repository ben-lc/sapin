plugins {
  id("com.diffplug.spotless")
  id("com.github.ben-manes.versions")
}

spotless {
  kotlinGradle { ktfmt() }
}
