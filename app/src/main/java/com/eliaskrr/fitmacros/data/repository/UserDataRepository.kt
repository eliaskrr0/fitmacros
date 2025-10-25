package com.eliaskrr.fitmacros.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserData(
    val nombre: String,
    val sexo: String,
    val fechaNacimiento: String,
    val altura: String,
    val peso: String,
    val objetivo: String,
    val activityRate: String
)

class UserDataRepository(context: Context) {

    private val dataStore = context.dataStore

    val userData: Flow<UserData> = dataStore.data.map {
        UserData(
            nombre = it[PreferencesKeys.USER_NAME] ?: "",
            sexo = it[PreferencesKeys.USER_SEX] ?: "",
            fechaNacimiento = it[PreferencesKeys.USER_BIRTHDATE] ?: "",
            altura = it[PreferencesKeys.USER_HEIGHT] ?: "",
            peso = it[PreferencesKeys.USER_WEIGHT] ?: "",
            objetivo = it[PreferencesKeys.USER_TARGET] ?: "",
            activityRate = it[PreferencesKeys.USER_ACTIVITY_RATE] ?: ""
        )
    }

    suspend fun saveUserData(userData: UserData) {
        dataStore.edit {
            it[PreferencesKeys.USER_NAME] = userData.nombre
            it[PreferencesKeys.USER_SEX] = userData.sexo
            it[PreferencesKeys.USER_BIRTHDATE] = userData.fechaNacimiento
            it[PreferencesKeys.USER_HEIGHT] = userData.altura
            it[PreferencesKeys.USER_WEIGHT] = userData.peso
            it[PreferencesKeys.USER_TARGET] = userData.objetivo
            it[PreferencesKeys.USER_ACTIVITY_RATE] = userData.activityRate
        }
    }

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_SEX = stringPreferencesKey("user_sex")
        val USER_BIRTHDATE = stringPreferencesKey("user_birthdate")
        val USER_HEIGHT = stringPreferencesKey("user_height")
        val USER_WEIGHT = stringPreferencesKey("user_weight")
        val USER_TARGET = stringPreferencesKey("user_target")
        val USER_ACTIVITY_RATE = stringPreferencesKey("user_activity_rate")
    }
}
