package com.github.ben_lc.sapin.etl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource

@ConfigurationProperties(prefix = "etl", ignoreUnknownFields = false)
data class EtlProperties(val scriptella: ScriptellaProperties, val load: LoadProperties)

data class ScriptellaProperties(val config: ScriptellaConfigProperties)

/** @property location the location of scriptella xml configuration files */
data class ScriptellaConfigProperties(val location: Resource)

data class LoadProperties(val geopackage: GeopackageProperties)

data class GeopackageProperties(val location: List<LocationGeopackageProperties>)

/**
 * Properties of the location geopackage to load.
 *
 * @property tableName the name of the geopackage table to load
 * @property nameColumnName the table column name containing the name of locations
 * @property level the level number of the locations
 * @property srcIdColumnName the table column name containing unique id of source data
 * @property isoIdColumnName the table column name containing iso id (eg: 3166-2) of the locations
 * @property levelNameColumnName the table column name containing the level name in the local
 *   language
 * @property levelNameEnColumnName the table column name containing the level name in english
 * @property srcParentIdColumnName the table column name containing parent unique id for sub
 *   divisions
 */
data class LocationGeopackageProperties(
    val tableName: String,
    val nameColumnName: String,
    val level: Short,
    val srcIdColumnName: String,
    val isoIdColumnName: String? = null,
    val levelNameColumnName: String? = null,
    val levelNameEnColumnName: String? = null,
    val srcParentIdColumnName: String? = null
)
