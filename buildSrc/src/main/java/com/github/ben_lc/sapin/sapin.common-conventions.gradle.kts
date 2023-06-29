plugins {
  id("com.diffplug.spotless")
  id("com.github.ben-manes.versions")
  id("pl.allegro.tech.build.axion-release")
}

spotless {
  kotlinGradle { ktfmt() }
}

version = scmVersion.version
