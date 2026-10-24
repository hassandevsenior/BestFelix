package com.felix.bestfelix.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.data.backup.BackupManager
import com.felix.bestfelix.data.prefs.ReminderSettings
import com.felix.bestfelix.data.prefs.SettingsStore
import com.felix.bestfelix.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class SettingsUiState(
    val reminders: ReminderSettings = ReminderSettings(
        morningEnabled = true,
        morningTime = ReminderSettings.DEFAULT_MORNING,
        eveningEnabled = true,
        eveningTime = ReminderSettings.DEFAULT_EVENING,
    ),
    val busy: Boolean = false,
)

sealed interface SettingsEvent {
    data class Message(val text: String) : SettingsEvent
    data class Error(val text: String) : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore,
    private val reminderScheduler: ReminderScheduler,
    private val backupManager: BackupManager,
) : ViewModel() {

    private val _events = MutableSharedFlow<SettingsEvent>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    val uiState: StateFlow<SettingsUiState> = settingsStore.reminders
        .map { SettingsUiState(reminders = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun setMorningEnabled(enabled: Boolean) = applyReminderChange {
        settingsStore.setMorningEnabled(enabled)
    }

    fun setEveningEnabled(enabled: Boolean) = applyReminderChange {
        settingsStore.setEveningEnabled(enabled)
    }

    fun setMorningTime(time: LocalTime) = applyReminderChange {
        settingsStore.setMorningTime(time)
    }

    fun setEveningTime(time: LocalTime) = applyReminderChange {
        settingsStore.setEveningTime(time)
    }

    /** Toute modification des rappels replanifie immédiatement les deux travaux. */
    private fun applyReminderChange(change: suspend () -> Unit) {
        viewModelScope.launch {
            change()
            reminderScheduler.reschedule()
        }
    }

    fun suggestedFileName(): String = backupManager.suggestedFileName()

    fun export(uri: Uri) {
        viewModelScope.launch {
            runCatching { backupManager.export(uri) }
                .onSuccess { summary ->
                    _events.emit(
                        SettingsEvent.Message("Export terminé : ${summary.itemCount} éléments enregistrés."),
                    )
                }
                .onFailure { error ->
                    _events.emit(
                        SettingsEvent.Error(error.message ?: "L'export a échoué."),
                    )
                }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            runCatching { backupManager.import(uri) }
                .onSuccess { summary ->
                    _events.emit(
                        SettingsEvent.Message("Import terminé : ${summary.itemCount} éléments restaurés."),
                    )
                }
                .onFailure { error ->
                    _events.emit(
                        SettingsEvent.Error(error.message ?: "L'import a échoué."),
                    )
                }
        }
    }
}
