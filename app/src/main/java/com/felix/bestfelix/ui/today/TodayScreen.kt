package com.felix.bestfelix.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.ui.components.StatTile
import com.felix.bestfelix.ui.onboarding.WelcomeScreen
import com.felix.bestfelix.ui.theme.BestFelixTheme

@Composable
fun TodayScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val accents = BestFelixTheme.accents

    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val message = when (event) {
                is TodayEvent.XpGained -> buildString {
                    append("+${event.amount} XP")
                    if (event.perfectDay) append(" · journée parfaite !")
                }
                is TodayEvent.JokersUsed -> if (event.count == 1) {
                    "Un joker a sauvé ta série d'hier"
                } else {
                    "${event.count} jokers ont sauvé tes séries d'hier"
                }
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.loading -> Unit

                !state.onboardingDone -> WelcomeScreen(
                    onLoadSamples = viewModel::loadSamples,
                    onStartEmpty = viewModel::startEmpty,
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        TodayHeader(state = state, onOpenSettings = onOpenSettings)
                    }

                    item {
                        TodaySection(title = "Habitudes à valider") {
                            if (state.habits.isEmpty()) {
                                TodayEmptyLine("Aucune habitude pour l'instant. Ajoute-la dans l'onglet Habitudes.")
                            } else {
                                Column {
                                    state.habits.forEachIndexed { index, habit ->
                                        if (index > 0) HorizontalDivider()
                                        TodayHabitRow(
                                            habit = habit,
                                            onToggle = { viewModel.toggleToday(habit.id) },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        TodaySection(title = "Habitudes à abandonner") {
                            if (state.quitHabits.isEmpty()) {
                                TodayEmptyLine("Rien à abandonner pour le moment.")
                            } else {
                                Column {
                                    state.quitHabits.forEachIndexed { index, quitHabit ->
                                        if (index > 0) HorizontalDivider()
                                        TodayQuitHabitRow(quitHabit = quitHabit)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        TodaySection(title = "Projets en cours") {
                            if (state.projects.isEmpty()) {
                                TodayEmptyLine("Aucun projet actif. Ajoute-le dans l'onglet Projets.")
                            } else {
                                Column {
                                    state.projects.forEachIndexed { index, project ->
                                        if (index > 0) HorizontalDivider()
                                        TodayProjectRow(item = project)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        TodaySection(title = "Épargne") {
                            if (state.goals.isEmpty()) {
                                TodayEmptyLine("Aucun objectif d'épargne.")
                            } else {
                                Column {
                                    state.goals.forEach { goal ->
                                        TodayGoalRow(item = goal)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        TodaySection(title = "Finances") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                StatTile(
                                    label = "Entrées du mois",
                                    amount = state.monthIncome,
                                    container = MaterialTheme.colorScheme.primaryContainer,
                                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f),
                                )
                                StatTile(
                                    label = "Je dois",
                                    amount = state.totalIOwe,
                                    container = accents.painContainer,
                                    content = accents.onPainContainer,
                                    modifier = Modifier.weight(1f),
                                )
                                StatTile(
                                    label = "On me doit",
                                    amount = state.totalOwedToMe,
                                    container = accents.successContainer,
                                    content = accents.onSuccessContainer,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
