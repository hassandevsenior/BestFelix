package com.felix.bestfelix.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.felix.bestfelix.data.prefs.SettingsStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Planifie les deux rappels à l'heure choisie. On utilise des travaux uniques replanifiés
 * à chaque exécution : un PeriodicWorkRequest ne sait pas viser une heure précise.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsStore: SettingsStore,
) {

    suspend fun reschedule() {
        val settings = settingsStore.reminders.first()
        schedule(MORNING_WORK, settings.morningEnabled, settings.morningTime, morning = true)
        schedule(EVENING_WORK, settings.eveningEnabled, settings.eveningTime, morning = false)
    }

    private fun schedule(
        name: String,
        enabled: Boolean,
        time: LocalTime,
        morning: Boolean,
    ) {
        val workManager = WorkManager.getInstance(context)
        if (!enabled) {
            workManager.cancelUniqueWork(name)
            return
        }

        val delay = delayUntil(time)
        val request = if (morning) {
            OneTimeWorkRequestBuilder<MorningReminderWorker>()
        } else {
            OneTimeWorkRequestBuilder<EveningReminderWorker>()
        }.setInitialDelay(delay).build()

        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request)
    }

    /** Délai jusqu'à la prochaine occurrence de [time], aujourd'hui ou demain. */
    private fun delayUntil(time: LocalTime): Duration {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        var next = now.toLocalDate().atTime(time)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next)
    }

    private companion object {
        const val MORNING_WORK = "rappel_matin"
        const val EVENING_WORK = "rappel_soir"
    }
}
