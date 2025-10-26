package com.eliaskrr.fitmacros.domain

import com.eliaskrr.fitmacros.data.model.ActivityRate
import com.eliaskrr.fitmacros.data.repository.UserData
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class CalculationResult(
    val tdee: Int = 0,
    val calorieGoal: Int = 0,
    val proteinGoal: Int = 0,
    val carbGoal: Int = 0,
    val fatGoal: Int = 0
)

object MacroCalculator {

    fun calculate(userData: UserData): CalculationResult {
        val weight = userData.peso.toDoubleOrNull()
        val height = userData.altura.toDoubleOrNull()
        val birthDate = try {
            val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
            LocalDate.parse(userData.fechaNacimiento, formatter)
        } catch (e: Exception) {
            null
        }

        if (weight == null || height == null || birthDate == null || userData.sexo.isBlank() || userData.activityRate.isBlank() || userData.objetivo.isBlank()) {
            return CalculationResult() // Devuelve valores por defecto si los datos no son válidos
        }

        val age = Period.between(birthDate, LocalDate.now()).years

        // 1. Calcular Tasa Metabólica Basal (TMB) - Fórmula Mifflin-St Jeor
        val bmr = if (userData.sexo == "Hombre") {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else { // Mujer
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }

        // 2. Calcular Calorías de Mantenimiento (TDEE)
        val activityRate = ActivityRate.fromDescription(userData.activityRate)
        val tdee = bmr * (activityRate?.value ?: 1.2) // Default to Sedentary if null

        // 3. Calcular Objetivo Calórico
        val target = TypeTarget.fromDescription(userData.objetivo)
        val calorieGoal = tdee * (target?.calorieMultiplier ?: 1.0) // Default to Maintain if null

        // 4. Calcular Macronutrientes (usando factores p y f según el objetivo)
        val proteinFactor = target?.proteinFactor ?: 1.8
        val fatFactor = target?.fatFactor ?: 0.8

        val proteinGrams = (weight * proteinFactor).roundToInt()
        val fatGrams = (weight * fatFactor).roundToInt()
        val caloriesFromProteinAndFat = (proteinGrams * 4) + (fatGrams * 9)
        val carbGrams = ((calorieGoal - caloriesFromProteinAndFat) / 4).roundToInt()

        return CalculationResult(
            tdee = tdee.roundToInt(),
            calorieGoal = calorieGoal.roundToInt(),
            proteinGoal = proteinGrams,
            carbGoal = if (carbGrams > 0) carbGrams else 0,
            fatGoal = fatGrams
        )
    }
}

enum class TypeTarget(
    val description: String,
    val calorieMultiplier: Double,
    val proteinFactor: Double, // p
    val fatFactor: Double      // f
) {
    GAIN_WEIGHT_AGGRESSIVELY("Ganar 0,5% de peso", 1.15, 1.9, 0.9), // Superávit ~15-20%
    GAIN_WEIGHT_MODERATELY("Ganar 0,35% de peso", 1.10, 1.9, 0.9), // Superávit ~10-15%
    GAIN_WEIGHT_SLOWLY("Ganar 0,25% de peso", 1.05, 1.9, 0.9),     // Superávit ~5-10%
    MAINTAIN_WEIGHT("Mantener peso", 1.0, 1.8, 0.8),                   // Mantenimiento
    LOSE_WEIGHT_SLOWLY("Perder 0,25% de peso", 0.90, 2.2, 0.7),       // Déficit ~10%
    LOSE_WEIGHT_MODERATELY("Perder 0,5% de peso", 0.85, 2.2, 0.7),      // Déficit ~15%
    LOSE_WEIGHT_AGGRESSIVELY("Perder 0,75% de peso", 0.80, 2.2, 0.7);   // Déficit ~20%

    companion object {
        fun fromDescription(description: String): TypeTarget? {
            return entries.find { it.description == description }
        }
    }
}
