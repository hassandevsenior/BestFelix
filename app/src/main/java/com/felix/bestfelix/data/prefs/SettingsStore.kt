package com.felix.bestfelix.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "reglages")

/** Les deux rappels quotidiens, tels qu'ils sont réglés. */
data class ReminderSettings(
    val morningEnabled: Boolean,
    val morningTime: LocalTime,
    val eveningEnabled: Boolean,
    val eveningTime: LocalTime,
) {
    companion object {
        val DEFAULT_MORNING: LocalTime = LocalTime.of(6, 0)
        val DEFAULT_EVENING: LocalTime = LocalTime.of(21, 0)
    }
}

/** Réglages locaux de l'appli. Aucune donnée personnelle ici, juste des préférences. */
@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val onboardingDone: Flow<Boolean> = context.dataStore.data
        .map { it[ONBOARDING_DONE] ?: false }

    val reminders: Flow<ReminderSettings> = context.dataStore.data.map { prefs ->
        ReminderSettings(
            morningEnabled = prefs[MORNING_ENABLED] ?: true,
            morningTime = time(
                prefs[MORNING_HOUR],
                prefs[MORNING_MINUTE],
                ReminderSettings.DEFAULT_MORNING,
            ),
            eveningEnabled = prefs[EVENING_ENABLED] ?: true,
            eveningTime = time(
                prefs[EVENING_HOUR],
                prefs[EVENING_MINUTE],
                ReminderSettings.DEFAULT_EVENING,
            ),
        )
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[ONBOARDING_DONE] = done }
    }

    suspend fun setMorningEnabled(enabled: Boolean) {
        context.dataStore.edit { it[MORNING_ENABLED] = enabled }
    }

    suspend fun setEveningEnabled(enabled: Boolean) {
        context.dataStore.edit { it[EVENING_ENABLED] = enabled }
    }

    suspend fun setMorningTime(time: LocalTime) {
        context.dataStore.edit {
            it[MORNING_HOUR] = time.hour
            it[MORNING_MINUTE] = time.minute
        }
    }

    suspend fun setEveningTime(time: LocalTime) {
        context.dataStore.edit {
            it[EVENING_HOUR] = time.hour
            it[EVENING_MINUTE] = time.minute
        }
    }

    private fun time(hour: Int?, minute: Int?, fallback: LocalTime): LocalTime =
        if (hour == null) fallback else LocalTime.of(hour, minute ?: 0)

    private companion object {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val MORNING_ENABLED = booleanPreferencesKey("rappel_matin_actif")
        val MORNING_HOUR = intPreferencesKey("rappel_matin_heure")
        val MORNING_MINUTE = intPreferencesKey("rappel_matin_minute")
        val EVENING_ENABLED = booleanPreferencesKey("rappel_soir_actif")
        val EVENING_HOUR = intPreferencesKey("rappel_soir_heure")
        val EVENING_MINUTE = intPreferencesKey("rappel_soir_minute")
    }
}
