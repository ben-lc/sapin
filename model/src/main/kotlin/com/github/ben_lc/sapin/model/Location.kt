package com.github.ben_lc.sapin.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/** Model for location */
@Table("sapin.location")
data class Location(
    @Id @Column("loc_id") val id: Int? = null,
    val name: String,
    val level: Level,
    val isoId: String? = null,
    val geom: String? = null
) {
  enum class Level {
    COUNTRY
  }
}
