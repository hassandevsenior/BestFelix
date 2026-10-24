package com.felix.bestfelix.ui.projects.projects

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.ProjectStatus

val Priority.label: String
    get() = when (this) {
        Priority.HIGH -> "Haute"
        Priority.MEDIUM -> "Moyenne"
        Priority.LOW -> "Basse"
    }

val ProjectStatus.label: String
    get() = when (this) {
        ProjectStatus.ACTIVE -> "En cours"
        ProjectStatus.PAUSED -> "En pause"
        ProjectStatus.DONE -> "Terminé"
    }

/**
 * La bande de priorité joue sur l'intensité de l'indigo, la couleur de structure :
 * le rouge reste réservé à la douleur et aux retards.
 */
@Composable
fun Priority.bandColor(): Color = when (this) {
    Priority.HIGH -> MaterialTheme.colorScheme.primary
    Priority.MEDIUM -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
    Priority.LOW -> MaterialTheme.colorScheme.outlineVariant
}
