package com.github.ben_lc.sapin.repository.util

import com.github.ben_lc.sapin.model.TaxonEntity
import com.github.ben_lc.sapin.model.TaxonScientificNameEntity
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.EnumWriteSupport

@WritingConverter class TaxonRankConverter : EnumWriteSupport<TaxonEntity.Rank>()

@WritingConverter
class TaxonomicStatusConverter : EnumWriteSupport<TaxonScientificNameEntity.TaxonomicStatus>()
