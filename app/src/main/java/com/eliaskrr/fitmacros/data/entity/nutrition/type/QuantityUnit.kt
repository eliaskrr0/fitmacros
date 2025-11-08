package com.eliaskrr.fitmacros.data.entity.nutrition.type

import androidx.annotation.StringRes
import com.eliaskrr.fitmacros.R

enum class QuantityUnit(
	@StringRes val labelRes: Int,
	@StringRes val shortLabelRes: Int
) {
    GRAMS(R.string.unit_grams, R.string.unit_grams_short),
    MILLILITERS(R.string.unit_milliliters, R.string.unit_milliliters_short);
}