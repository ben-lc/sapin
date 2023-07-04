package com.github.ben_lc.sapin.etl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.core.io.Resource

@ConfigurationProperties(prefix = "etl", ignoreUnknownFields = false)
data class EtlProperties @ConstructorBinding constructor(val scriptella: ScriptellaProperties)

data class ScriptellaProperties
@ConstructorBinding
constructor(val config: ScriptellaConfigProperties)

/** @property location the location of scriptella xml configuration files */
data class ScriptellaConfigProperties @ConstructorBinding constructor(val location: Resource)
