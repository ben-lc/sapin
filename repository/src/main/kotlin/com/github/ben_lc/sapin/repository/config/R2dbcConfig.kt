package com.github.ben_lc.sapin.repository.config

import com.github.ben_lc.sapin.model.NaturalAreaEntity
import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity
import com.github.ben_lc.sapin.repository.util.TaxonRankConverter
import com.github.ben_lc.sapin.repository.util.TaxonomicStatusConverter
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
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
            .codecRegistrar(
                EnumCodec.builder()
                    .withEnum("taxon_rank_enum", TaxonEntity.Rank::class.java)
                    .withEnum(
                        "taxonomic_status_enum",
                        TaxonScientificNameEntity.TaxonomicStatus::class.java)
                    .withEnum("natural_area_domain_enum", NaturalAreaEntity.Domain::class.java)
                    .build())
            .build())
  }

  override fun getCustomConverters(): MutableList<Any> {
    return mutableListOf(TaxonRankConverter(), TaxonomicStatusConverter())
  }
}
