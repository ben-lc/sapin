package com.github.ben_lc.sapin.repository

import com.github.ben_lc.sapin.model.Location
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.EnumWriteSupport

/**
 * Spring data converter used to force spring data keep enum as enum type, so it can be persisted as
 * postgres enum.
 */
@WritingConverter class LocationLevelConverter : EnumWriteSupport<Location.Level>()
