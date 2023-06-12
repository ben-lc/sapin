package com.github.ben_lc.sapin.repository

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class R2dbcConfig : AbstractR2dbcConfiguration() {

  @Autowired private lateinit var r2dbcProperties: R2dbcProperties

  @Bean
  override fun connectionFactory(): ConnectionFactory {
    val baseOptions = ConnectionFactoryOptions.parse(r2dbcProperties.url)
    return PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host(baseOptions.getValue(ConnectionFactoryOptions.HOST)!! as String)
            .port(baseOptions.getValue(ConnectionFactoryOptions.PORT)!! as Int)
            .database(baseOptions.getValue(ConnectionFactoryOptions.DATABASE)!! as String)
            .username(r2dbcProperties.username)
            .password(r2dbcProperties.password)
            .build())
  }
}
