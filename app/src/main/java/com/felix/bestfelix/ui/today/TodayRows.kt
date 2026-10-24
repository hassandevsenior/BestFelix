package com.felix.bestfelix.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.core.ui.rememberLightTick
import com.felix.bestfelix.ui.projects.projects.label
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

/** Ligne compacte d'habitude : validation directe depuis l'écran du matin. */
@Composable
fun TodayHabitRow(
    habit: TodayHabit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val tick = rememberLightTick()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(
                onClickLabel = if (habit.marked) {
                    "Annuler la validation de ${habit.name}"
                } else {
                    "Valider ${habit.name}"
                },
            ) {
                tick()
                onToggle()
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape = CircleShape,
            color = if (habit.marked) accents.reward else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (habit.marked) accents.onReward else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(36.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Text(
            text = habit.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (habit.streak > 0) accents.reward else MaterialTheme.colorScheme.outline,
            )
            Text(
                text = "${habit.streak} j",
                style = MaterialTheme.typography.labelLarge.tabular(),
            )
        }
    }
}

/** Habitude à abandonner, en version compacte. */
@Composable
fun TodayQuitHabitRow(
    quitHabit: TodayQuitHabit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = quitHabit.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${quitHabit.heldStreak} j tenus · record ${quitHabit.bestBalance} XP",
                style = MaterialTheme.typography.bodySmall.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (quitHabit.isDebt) {
                "Dette ${-quitHabit.balance} XP"
            } else {
                "${quitHabit.balance} XP"
            },
            style = MaterialTheme.typography.titleMedium.tabular(),
            fontWeight = FontWeight.SemiBold,
            color = if (quitHabit.isDebt) accents.pain else accents.success,
        )
    }
}

/** Projet prioritaire, avec la tâche du moment. */
@Composable
fun TodayProjectRow(
    item: TodayProject,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val project = item.project

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = project.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = project.currentTask?.let { "En cours sur : $it" }
                    ?: "${project.context} · priorité ${project.priority.label.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.overdue) {
            Surface(
                color = accents.painContainer,
                contentColor = accents.onPainContainer,
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = "En retard",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        } else {
            project.dueDate?.let { due ->
                Text(
                    text = DateFormatFr.dayMonth(due),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Objectif d'épargne, en version compacte. */
@Composable
fun TodayGoalRow(
    item: TodayGoal,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val barColor = if (item.progress.isReached) accents.success else accents.reward

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.goal.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${item.progress.percent} %",
                style = MaterialTheme.typography.labelLarge.tabular(),
                color = barColor,
            )
        }
        LinearProgressIndicator(
            progress = { item.progress.fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = barColor,
            trackColor = accents.neutralTrack,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Text(
            text = "${MoneyFormat.grouped(item.progress.saved)} / ${MoneyFormat.format(item.progress.target)}",
            style = MaterialTheme.typography.bodySmall.tabular(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Carte de section de l'écran du matin : un titre, puis son contenu. */
@Composable
fun TodaySection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                trailing?.invoke()
            }
            content()
        }
    }
}

/** Petit état vide, dans une section de l'écran du matin. */
@Composable
fun TodayEmptyLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 10.dp),
    )
}
