package com.felix.bestfelix.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Tout petit bouton « i » : l'icône est discrète (16 dp) mais la zone tactile reste de
 * 48 dp, comme l'exige l'accessibilité.
 */
@Composable
fun InfoButton(
    title: String,
    explanation: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Expliquer la règle",
) {
    var open by remember { mutableStateOf(false) }

    IconButton(onClick = { open = true }, modifier = modifier) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text(title) },
            text = { Text(explanation, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { open = false }) { Text("Compris") }
            },
        )
    }
}
