package com.felix.bestfelix.ui.habits.quit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.notes.NotesLink

@Composable
fun QuitHabitsPane(
    snackbarHostState: SnackbarHostState,
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuitHabitsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val today = viewModel.today()
    var sheet by remember { mutableStateOf<QuitSheetTarget?>(null) }
    var relapseFor by remember { mutableStateOf<QuitRow?>(null) }
    var relapseDate by remember { mutableStateOf(today) }

    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!state.loading && state.rows.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.DoNotDisturbOn,
                title = "Aucune habitude à abandonner",
                message = "Ajoute une habitude à arrêter : chaque jour tenu rapporte des XP, " +
                    "chaque rechute en efface sept jours. La courbe montre tout.",
                actionLabel = "Ajouter une habitude",
                onAction = { sheet = QuitSheetTarget.New },
                modifier = Modifier.align(Alignment.TopCenter),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    NotesLink(
                        category = NoteCategory.QUIT_HABITS,
                        onOpenNotes = onOpenNotes,
                    )
                }

                items(items = state.rows, key = { it.quitHabit.id }) { row ->
                    QuitHabitCard(
                        row = row,
                        onEdit = { sheet = QuitSheetTarget.Edit(row.quitHabit) },
                        onRelapse = {
                            relapseDate = today
                            relapseFor = row
                        },
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = QuitSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("À abandonner", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        QuitHabitSheet(
            editing = (target as? QuitSheetTarget.Edit)?.quitHabit,
            today = today,
            onDismiss = { sheet = null },
            onCreate = viewModel::create,
            onUpdate = viewModel::update,
            onDelete = viewModel::delete,
        )
    }

    relapseFor?.let { row ->
        RelapseSheet(
            habitName = row.quitHabit.name,
            today = today,
            startDate = row.quitHabit.startDate,
            preview = viewModel.previewRelapse(row.quitHabit.id, relapseDate),
            selectedDate = relapseDate,
            onDateChange = { relapseDate = it },
            onConfirm = { date, note ->
                viewModel.addRelapse(row.quitHabit.id, date, note)
            },
            onDismiss = { relapseFor = null },
        )
    }
}

private sealed interface QuitSheetTarget {
    data object New : QuitSheetTarget
    data class Edit(val quitHabit: QuitHabit) : QuitSheetTarget
}
