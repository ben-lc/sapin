package com.github.ben_lc.sapin.graphql.type

import com.github.ben_lc.sapin.generated.graphql.types.Location
import com.github.ben_lc.sapin.model.LocationEntity

fun LocationEntity.toGraphqlType(): Location =
    Location(
        id = this.id.toString(),
        name = this.name,
        level = this.level.toInt(),
        levelLocalName = this.levelLocalName,
        levelLocalNameEn = this.levelLocalNameEn,
        isoId = this.isoId)
