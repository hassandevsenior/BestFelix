package com.felix.bestfelix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.notes.NotesViewModel

/** Les 5 onglets de la barre du bas, dans l'ordre d'affichage. */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
) {
    TODAY("aujourdhui", "Aujourd'hui", Icons.Filled.WbSunny, Icons.Outlined.WbSunny),
    HABITS("habitudes", "Habitudes", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    PROJECTS("projets", "Projets", Icons.Filled.WorkOutline, Icons.Outlined.WorkOutline),
    FINANCE("finances", "Finances", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    NOTES("notes", "Notes", Icons.AutoMirrored.Filled.StickyNote2, Icons.AutoMirrored.Outlined.StickyNote2),
    ;

    companion object {
        /** L'onglet Notes porte un argument de rubrique : on compare donc aussi le préfixe. */
        fun fromRoute(route: String?): TopLevelDestination? = entries.firstOrNull {
            route == it.route || route?.startsWith("${it.route}?") == true
        }
    }
}

object Routes {
    const val SETTINGS = "reglages"

    /** Ouvre le bloc-notes, éventuellement filtré sur une rubrique. */
    fun notes(category: NoteCategory?): String =
        "${TopLevelDestination.NOTES.route}?${NotesViewModel.CATEGORY_ARG}=" +
            (category?.name ?: NotesViewModel.NO_CATEGORY)
}
