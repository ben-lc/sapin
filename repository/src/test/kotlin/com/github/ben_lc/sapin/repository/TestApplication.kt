package com.github.ben_lc.sapin.repository

import javax.sql.DataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource

@SpringBootApplication
class TestApplication {

  /** Define JDBC datasource to be able to use @Sql test annotation to load scripts of data. */
  @Bean
  fun dataSource(r2dbcProperties: R2dbcProperties): DataSource {
    val dataSource = DriverManagerDataSource()
    dataSource.setDriverClassName("org.postgresql.Driver")
    dataSource.url = r2dbcProperties.url.replace("r2dbc", "jdbc")
    dataSource.username = r2dbcProperties.username
    dataSource.password = r2dbcProperties.password
    return dataSource
  }
}
