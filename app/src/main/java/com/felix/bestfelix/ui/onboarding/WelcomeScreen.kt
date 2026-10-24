package com.felix.bestfelix.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.ui.theme.BestFelixTheme

private data class Block(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

private val BLOCKS = listOf(
    Block(
        icon = Icons.Filled.CheckCircle,
        title = "Habitudes à prendre",
        description = "Un tap par jour. Ta série monte, les XP et le niveau suivent, " +
            "et tous les 7 jours un joker te protège d'un oubli.",
    ),
    Block(
        icon = Icons.Outlined.DoNotDisturbOn,
        title = "Habitudes à abandonner",
        description = "Chaque jour tenu rapporte 5 XP. Une rechute efface 7 jours d'efforts. " +
            "La courbe ne ment pas.",
    ),
    Block(
        icon = Icons.Outlined.WorkOutline,
        title = "Projets et épargne",
        description = "Tes projets pro par priorité, avec la tâche du moment, " +
            "et tes objectifs d'épargne en FCFA.",
    ),
    Block(
        icon = Icons.Outlined.AccountBalanceWallet,
        title = "Finances",
        description = "Ce que tu dois, ce qu'on te doit, et tes entrées du mois.",
    ),
    Block(
        icon = Icons.AutoMirrored.Outlined.StickyNote2,
        title = "Notes",
        description = "Un bloc-notes rangé par rubrique, relié à chaque section.",
    ),
)

/** Écran du tout premier lancement. */
@Composable
fun WelcomeScreen(
    onLoadSamples: () -> Unit,
    onStartEmpty: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Bienvenue dans Best Félix",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Cinq blocs, un seul écran le matin. Tout reste sur ce téléphone.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        BLOCKS.forEach { block ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(
                    imageVector = block.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(22.dp),
                    tint = accents.reward,
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = block.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = block.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = onLoadSamples, modifier = Modifier.fillMaxWidth()) {
                Text("Charger des exemples")
            }
            OutlinedButton(onClick = onStartEmpty, modifier = Modifier.fillMaxWidth()) {
                Text("Partir de zéro")
            }
            Text(
                text = "Les exemples sont préfixés « Exemple · » : tu peux les supprimer un par un.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
