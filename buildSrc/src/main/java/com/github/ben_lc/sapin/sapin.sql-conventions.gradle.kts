plugins {
  id("com.diffplug.spotless")
}

spotless {
  sql {
    target("src/**/*.sql")
    prettier(mapOf("prettier" to "2.8.8", "prettier-plugin-sql" to "0.14.0"))
        .config(mapOf("language" to "postgresql"))
  }
}
