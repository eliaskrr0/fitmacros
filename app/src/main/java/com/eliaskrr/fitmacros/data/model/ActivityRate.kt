package com.eliaskrr.fitmacros.data.model

enum class ActivityRate(val description: String, val value: Double) {
    SEDENTARY("Sedentario", 1.20),
    LIGHT("Ligera", 1.375),
    MODERATE("Moderado", 1.55),
    HEAVY("Alto", 1.725),
    VERY_HEAVY("Atleta", 1.90);

    companion object {
        fun fromDescription(description: String): ActivityRate? {
            return entries.find { it.description == description }
        }
    }
}
