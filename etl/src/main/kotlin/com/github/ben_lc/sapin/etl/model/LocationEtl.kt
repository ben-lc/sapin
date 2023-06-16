package com.github.ben_lc.sapin.etl.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/** Model for location in the context of the ETL tool. */
@Table("sapin.location")
data class LocationEtl(
    @Id @Column("loc_id") val id: Int? = null,
    @Column("parent_loc_id") val parentId: Int? = null,
    val name: String,
    val level: Int,
    val levelLocalName: String? = null,
    val levelLocalNameEn: String? = null,
    val isoId: String? = null,
    val geom: String? = null,
    val srid: Int? = null,
    @Column("src_loc_id") val srcId: String? = null,
    @Column("src_parent_loc_id") val srcParentId: String? = null
) {}
