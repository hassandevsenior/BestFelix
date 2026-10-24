package com.felix.bestfelix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Choix d'une date. Le sélecteur Material travaille en millisecondes UTC : on convertit
 * à la frontière, et tout le reste de l'appli ne manipule que des [LocalDate].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    clearable: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(text = date?.let { DateFormatFr.short(it) } ?: label)
        }
        if (clearable && date != null) {
            IconButton(onClick = { onDateChange(null) }) {
                Icon(Icons.Filled.Close, contentDescription = "Retirer la date")
            }
        }
    }

    if (open) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (date ?: maxDate ?: LocalDate.now()).toEpochDay() * MILLIS_PER_DAY,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val candidate = utcTimeMillis.toLocalDate()
                    if (minDate != null && candidate.isBefore(minDate)) return false
                    if (maxDate != null && candidate.isAfter(maxDate)) return false
                    return true
                }
            },
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { onDateChange(it.toLocalDate()) }
                        open = false
                    },
                ) {
                    Text("Choisir")
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text("Annuler") }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

private const val MILLIS_PER_DAY = 86_400_000L

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
