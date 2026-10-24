package com.felix.bestfelix.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.data.prefs.SettingsStore
import com.felix.bestfelix.domain.repository.HabitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Les workers ne passent pas par Hilt (pas de hilt-work ici, donc pas de bricolage du
 * WorkManagerInitializer dans le manifeste) : ils récupèrent leurs dépendances par cet
 * EntryPoint.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ReminderEntryPoint {
    fun habitRepository(): HabitRepository
    fun settingsStore(): SettingsStore
    fun dateProvider(): DateProvider
    fun notifier(): Notifier
    fun reminderScheduler(): ReminderScheduler
}

private fun Context.reminderEntryPoint(): ReminderEntryPoint =
    EntryPointAccessors.fromApplication(this, ReminderEntryPoint::class.java)

/** Combien d'habitudes restent à valider aujourd'hui. */
private suspend fun remainingToday(entryPoint: ReminderEntryPoint): Int {
    val today = entryPoint.dateProvider().today()
    val histories = entryPoint.habitRepository().observeHistories().first()
    return histories.count { !it.countsForStreak(today) }
}

/** Rappel du matin : « X habitudes à valider aujourd'hui ». */
class MorningReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val entryPoint = applicationContext.reminderEntryPoint()
        val settings = entryPoint.settingsStore().reminders.first()

        if (settings.morningEnabled) {
            val remaining = remainingToday(entryPoint)
            if (remaining > 0) {
                entryPoint.notifier().notifyReminder(
                    id = Notifier.MORNING_ID,
                    title = "Bonjour Félix",
                    text = if (remaining == 1) {
                        "1 habitude à valider aujourd'hui."
                    } else {
                        "$remaining habitudes à valider aujourd'hui."
                    },
                )
            }
        }

        // Chaque rappel replanifie le suivant : c'est ce qui permet de viser une heure précise.
        entryPoint.reminderScheduler().reschedule()
        return Result.success()
    }
}

/** Rappel du soir : seulement s'il reste des habitudes non validées, pour protéger la série. */
class EveningReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val entryPoint = applicationContext.reminderEntryPoint()
        val settings = entryPoint.settingsStore().reminders.first()

        if (settings.eveningEnabled) {
            val remaining = remainingToday(entryPoint)
            if (remaining > 0) {
                entryPoint.notifier().notifyReminder(
                    id = Notifier.EVENING_ID,
                    title = "Protège ta série",
                    text = if (remaining == 1) {
                        "Il reste 1 habitude à valider avant minuit."
                    } else {
                        "Il reste $remaining habitudes à valider avant minuit."
                    },
                )
            }
        }

        entryPoint.reminderScheduler().reschedule()
        return Result.success()
    }
}
