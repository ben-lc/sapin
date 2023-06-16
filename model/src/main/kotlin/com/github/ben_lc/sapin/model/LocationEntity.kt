package com.github.ben_lc.sapin.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/** Data class for Location */
@Table("sapin.location")
data class LocationEntity(
    @Id @Column("loc_id") val id: Int? = null,
    val name: String,
    val level: Int,
    val levelLocalName: String? = null,
    val levelLocalNameEn: String? = null,
    val isoId: String? = null
)
