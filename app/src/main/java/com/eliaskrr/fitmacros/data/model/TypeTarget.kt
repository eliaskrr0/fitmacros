package com.eliaskrr.fitmacros.data.model

enum class TypeTarget(val description: String, val multiplier: Double) {
    GAIN_WEIGHT_AGGRESSIVELY("Ganar 0,5% de peso", 1.05),
    GAIN_WEIGHT_MODERATELY("Ganar 0,35% de peso", 1.035),
    GAIN_WEIGHT_SLOWLY("Ganar 0,25% de peso", 1.025),
    MAINTAIN_WEIGHT("Mantener peso", 1.0),
    LOSE_WEIGHT_SLOWLY("Perder 0,25% de peso", 0.975),
    LOSE_WEIGHT_MODERATELY("Perder 0,5% de peso", 0.95),
    LOSE_WEIGHT_AGGRESSIVELY("Perder 0,75% de peso", 0.925);

    companion object {
        fun fromDescription(description: String): TypeTarget? {
            return entries.find { it.description.equals(description, ignoreCase = true) }
        }
    }
}

