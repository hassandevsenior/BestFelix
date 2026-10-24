package com.felix.bestfelix.ui.habits.good

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.notes.NotesLink

@Composable
fun GoodHabitsPane(
    snackbarHostState: SnackbarHostState,
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoodHabitsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheet by remember { mutableStateOf<HabitSheetTarget?>(null) }

    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val message = when (event) {
                is GoodHabitEvent.XpGained -> buildString {
                    append("+${event.amount} XP")
                    if (event.perfectDay) append(" · journée parfaite !")
                }
                is GoodHabitEvent.JokersUsed -> if (event.count == 1) {
                    "Un joker a sauvé ta série d'hier"
                } else {
                    "${event.count} jokers ont sauvé tes séries d'hier"
                }
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!state.loading && state.rows.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.CheckCircle,
                title = "Aucune habitude à prendre",
                message = "Ajoute une habitude, valide-la chaque jour et regarde ta série monter. " +
                    "Tous les 7 jours, le multiplicateur d'XP grimpe.",
                actionLabel = "Ajouter une habitude",
                onAction = { sheet = HabitSheetTarget.New },
                modifier = Modifier.align(Alignment.TopCenter),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { RewardHeader(state = state) }

                item {
                    NotesLink(
                        category = NoteCategory.GOOD_HABITS,
                        onOpenNotes = onOpenNotes,
                    )
                }

                items(items = state.rows, key = { it.habit.id }) { row ->
                    HabitCard(
                        row = row,
                        onToggle = { date -> viewModel.toggleDay(row.habit.id, date) },
                        onEdit = { sheet = HabitSheetTarget.Edit(row.habit) },
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = HabitSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Habitude", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        HabitSheet(
            editing = (target as? HabitSheetTarget.Edit)?.habit,
            onDismiss = { sheet = null },
            onCreate = viewModel::createHabit,
            onUpdate = viewModel::updateHabit,
            onDelete = viewModel::deleteHabit,
        )
    }
}

private sealed interface HabitSheetTarget {
    data object New : HabitSheetTarget
    data class Edit(val habit: Habit) : HabitSheetTarget
}
