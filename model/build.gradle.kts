plugins {
  id("sapin.common-conventions")
  id("sapin.kotlin-conventions")
  id("sapin.spring-conventions")
}

repositories { mavenCentral() }

dependencies {
  api("org.springframework.data:spring-data-commons")
  api("org.springframework.data:spring-data-relational")
}
