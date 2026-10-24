package com.felix.bestfelix.ui.habits.good

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.ui.rememberLightTick
import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular
import kotlinx.coroutines.launch

@Composable
fun HabitCard(
    row: HabitRow,
    onToggle: (java.time.LocalDate) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ValidateButton(
                    validated = row.validatedToday,
                    savedByJoker = row.week.last().status == DayStatus.SAVED_BY_JOKER,
                    habitName = row.habit.name,
                    onClick = { onToggle(row.week.last().date) },
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEdit),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = row.habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!row.habit.why.isNullOrBlank()) {
                        Text(
                            text = row.habit.why,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (row.streak > 0) accents.reward else MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = "${row.streak} j",
                                style = MaterialTheme.typography.labelLarge.tabular(),
                            )
                        }
                        if (row.jokers > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = if (row.jokers > 1) "${row.jokers} jokers" else "1 joker",
                                    style = MaterialTheme.typography.labelLarge.tabular(),
                                )
                            }
                        }
                    }
                }
            }

            WeekDots(week = row.week, habitName = row.habit.name, onToggle = onToggle)
        }
    }
}

/** Gros bouton rond : un tap valide la journée, avec un petit rebond et un « tac ». */
@Composable
private fun ValidateButton(
    validated: Boolean,
    savedByJoker: Boolean,
    habitName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val tick = rememberLightTick()

    val container = when {
        savedByJoker -> accents.rewardContainer
        validated -> accents.reward
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when {
        savedByJoker -> accents.onRewardContainer
        validated -> accents.onReward
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = CircleShape,
        color = container,
        contentColor = content,
        modifier = modifier
            .size(64.dp)
            .scale(scale.value)
            .clickable(
                onClickLabel = if (validated) {
                    "Annuler la validation de $habitName"
                } else {
                    "Valider $habitName pour aujourd'hui"
                },
            ) {
                tick()
                scope.launch {
                    scale.animateTo(0.86f, tween(70))
                    scale.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 600f))
                }
                onClick()
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (savedByJoker) Icons.Filled.Shield else Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

/** Les 7 pastilles : aujourd'hui à droite, les 6 jours corrigeables à sa gauche. */
@Composable
private fun WeekDots(
    week: List<DayCell>,
    habitName: String,
    onToggle: (java.time.LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val tick = rememberLightTick()

    Row(modifier = modifier.fillMaxWidth()) {
        week.forEach { cell ->
            val label = when (cell.status) {
                DayStatus.VALIDATED -> "validé"
                DayStatus.SAVED_BY_JOKER -> "sauvé par un joker"
                null -> "non validé"
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable(
                        enabled = cell.editable,
                        onClickLabel = "$habitName, ${DateFormatFr.dayMonth(cell.date)}, $label",
                    ) {
                        tick()
                        onToggle(cell.date)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clearAndSetSemantics { },
                ) {
                    Text(
                        text = cell.initial,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cell.isToday) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    val color = when (cell.status) {
                        DayStatus.VALIDATED -> accents.reward
                        DayStatus.SAVED_BY_JOKER -> accents.rewardContainer
                        null -> Color.Transparent
                    }
                    Surface(
                        shape = CircleShape,
                        color = color,
                        contentColor = accents.onReward,
                        border = if (cell.status == null) {
                            androidx.compose.foundation.BorderStroke(
                                width = if (cell.isToday) 2.dp else 1.dp,
                                color = if (cell.editable) {
                                    MaterialTheme.colorScheme.outline
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                            )
                        } else {
                            null
                        },
                        modifier = Modifier.size(22.dp),
                    ) {
                        if (cell.status == DayStatus.VALIDATED) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
