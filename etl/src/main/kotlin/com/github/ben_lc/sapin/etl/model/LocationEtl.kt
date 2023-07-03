package com.github.ben_lc.sapin.etl.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/** Model for location in the context of the ETL tool. */
@Table("sapin.location")
data class LocationEtl(
    @Id val id: Int? = null,
    val parentId: Int? = null,
    val name: String,
    val level: Int,
    val levelName: String? = null,
    val levelNameEn: String? = null,
    val isoId: String? = null,
    val geom: String? = null,
    val srid: Int? = null,
    val srcId: String? = null,
    val srcParentId: String? = null
) {}
