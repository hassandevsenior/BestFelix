package com.felix.bestfelix.ui.projects.saving

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.notes.NotesLink

@Composable
fun SavingsPane(
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheet by remember { mutableStateOf<SavingSheetTarget?>(null) }
    var depositFor by remember { mutableStateOf<GoalRow?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (!state.loading && state.rows.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Savings,
                title = "Aucun objectif d'épargne",
                message = "Fixe un montant cible et une échéance : l'appli te dira combien mettre " +
                    "de côté chaque mois pour la tenir.",
                actionLabel = "Ajouter un objectif",
                onAction = { sheet = SavingSheetTarget.New },
                modifier = Modifier.align(Alignment.TopCenter),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    NotesLink(category = NoteCategory.PROJECTS, onOpenNotes = onOpenNotes)
                }

                items(items = state.rows, key = { it.goal.id }) { row ->
                    SavingGoalCard(
                        row = row,
                        onClick = { sheet = SavingSheetTarget.Edit(row) },
                        onAddDeposit = { depositFor = row },
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = SavingSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Objectif", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        SavingGoalSheet(
            editing = (target as? SavingSheetTarget.Edit)?.row,
            onDismiss = { sheet = null },
            onCreate = viewModel::create,
            onUpdate = viewModel::update,
            onDelete = viewModel::delete,
            onDeleteDeposit = viewModel::deleteDeposit,
        )
    }

    depositFor?.let { row ->
        DepositSheet(
            goalTitle = row.goal.title,
            today = viewModel.today(),
            onDismiss = { depositFor = null },
            onConfirm = { date, amount -> viewModel.addDeposit(row.goal.id, date, amount) },
        )
    }
}

private sealed interface SavingSheetTarget {
    data object New : SavingSheetTarget
    data class Edit(val row: GoalRow) : SavingSheetTarget
}
