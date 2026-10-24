package com.felix.bestfelix

import android.app.Application
import com.felix.bestfelix.notifications.Notifier
import com.felix.bestfelix.notifications.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BestFelixApp : Application() {

    @Inject lateinit var notifier: Notifier

    @Inject lateinit var reminderScheduler: ReminderScheduler

    @Inject lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        notifier.createChannel()
        // Les travaux WorkManager survivent au redémarrage, mais on réaligne les heures
        // à chaque lancement au cas où les réglages auraient changé hors de l'appli.
        applicationScope.launch { reminderScheduler.reschedule() }
    }
}
