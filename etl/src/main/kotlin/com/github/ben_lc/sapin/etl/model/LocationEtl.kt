package com.github.ben_lc.sapin.etl.model

/** Model for location in the context of the ETL tool. */
data class LocationEtl(
    val id: Int? = null,
    val parentId: Int? = null,
    val name: String,
    val level: Short,
    val levelName: String? = null,
    val levelNameEn: String? = null,
    val isoId: String? = null,
    val srcId: String? = null,
    val srcParentId: String? = null,
    val geom: String,
    val srid: Int,
) {}
