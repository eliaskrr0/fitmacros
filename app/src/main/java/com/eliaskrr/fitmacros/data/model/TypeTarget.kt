package com.eliaskrr.fitmacros.data.model

enum class TypeTarget(val description: String) {
    GAIN_WEIGHT_AGGRESSIVELY("Ganar 0,5% de peso"),
    GAIN_WEIGHT_MODERATELY("Ganar 0,35% de peso"),
    GAIN_WEIGHT_SLOWLY("Ganar 0,25% de peso"),
    MAINTAIN_WEIGHT("Mantener peso"),
    LOSE_WEIGHT_SLOWLY("Perder 0,25% de peso"),
    LOSE_WEIGHT_MODERATELY("Perder 0,5% de peso"),
    LOSE_WEIGHT_AGGRESSIVELY("Perder 0,75% de peso");

    companion object {
        fun fromDescription(description: String): TypeTarget? {
            return entries.find { it.description == description }
        }
    }
}
