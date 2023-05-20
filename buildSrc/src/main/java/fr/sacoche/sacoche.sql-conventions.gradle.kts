plugins {
  id("com.diffplug.spotless")
}

spotless {
spotless {
  sql {
    target("src/main/resources/**/*.sql")
    prettier(mapOf("prettier" to "2.8.8", "prettier-plugin-sql" to "0.14.0"))
        .config(mapOf("language" to "postgresql"))
  }
  format("xml") {
    target("**/*.xml")
    prettier(mapOf("prettier" to "2.8.8", "@prettier/plugin-xml" to "2.2.0"))
            .config(mapOf("xmlWhitespaceSensitivity" to "ignore"))
  }
}
