package com.eliaskrr.fitmacros.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.ui.theme.Dimens
import com.eliaskrr.fitmacros.ui.theme.TextFieldContainerColor
import com.eliaskrr.fitmacros.ui.theme.TextPrimaryColor

object FitMacrosTextFieldDefaults {
    val MinHeight: Dp = 48.dp
    val Shape = RoundedCornerShape(Dimens.Large)

    fun colors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = TextFieldContainerColor,
        unfocusedContainerColor = TextFieldContainerColor,
        disabledContainerColor = TextFieldContainerColor.copy(alpha = 0.6f),
        focusedBorderColor = TextPrimaryColor.copy(alpha = 0.6f),
        unfocusedBorderColor = TextPrimaryColor.copy(alpha = 0.3f),
        focusedLabelColor = TextPrimaryColor.copy(alpha = 0.9f),
        unfocusedLabelColor = TextPrimaryColor.copy(alpha = 0.7f),
        focusedTextColor = TextPrimaryColor,
        unfocusedTextColor = TextPrimaryColor,
        disabledTextColor = TextPrimaryColor.copy(alpha = 0.6f),
        cursorColor = TextPrimaryColor
    )
}
