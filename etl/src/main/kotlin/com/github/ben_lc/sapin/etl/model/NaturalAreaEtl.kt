package com.github.ben_lc.sapin.etl.model

import com.github.ben_lc.sapin.model.NaturalAreaEntity

data class NaturalAreaEtl(
    val id: Int? = null,
    val name: String,
    val domain: NaturalAreaEntity.Domain? = null,
    val srcId: String,
    val typeCode: String,
    val description: String? = null,
    val geom: String,
    val srid: Int
)
