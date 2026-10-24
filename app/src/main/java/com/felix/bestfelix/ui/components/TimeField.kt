package com.felix.bestfelix.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.ui.theme.tabular
import java.time.LocalTime

/** Heure affichée à la française : « 06h00 ». */
fun LocalTime.frenchLabel(): String = "%02dh%02d".format(hour, minute)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    var open by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { open = true },
        enabled = enabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = contentDescription,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = time.frenchLabel(),
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge.tabular(),
        )
    }

    if (open) {
        val state = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(LocalTime.of(state.hour, state.minute))
                        open = false
                    },
                ) {
                    Text("Choisir")
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text("Annuler") }
            },
            text = { TimePicker(state = state) },
        )
    }
}
