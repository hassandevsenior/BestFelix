package com.felix.bestfelix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.finance.FinanceScreen
import com.felix.bestfelix.ui.habits.HabitsScreen
import com.felix.bestfelix.ui.notes.NotesScreen
import com.felix.bestfelix.ui.notes.NotesViewModel
import com.felix.bestfelix.ui.projects.ProjectsScreen
import com.felix.bestfelix.ui.settings.SettingsScreen
import com.felix.bestfelix.ui.today.TodayScreen

@Composable
fun BestFelixNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Tous les liens « Notes (n) » des autres onglets passent par ici.
    val openNotes: (NoteCategory) -> Unit = { category ->
        navController.navigate(Routes.notes(category)) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.TODAY.route,
        modifier = modifier,
    ) {
        composable(TopLevelDestination.TODAY.route) {
            TodayScreen(onOpenSettings = { navController.navigate(Routes.SETTINGS) })
        }
        composable(TopLevelDestination.HABITS.route) {
            HabitsScreen(onOpenNotes = openNotes)
        }
        composable(TopLevelDestination.PROJECTS.route) {
            ProjectsScreen(onOpenNotes = openNotes)
        }
        composable(TopLevelDestination.FINANCE.route) {
            FinanceScreen(onOpenNotes = openNotes)
        }
        composable(
            route = "${TopLevelDestination.NOTES.route}?${NotesViewModel.CATEGORY_ARG}={${NotesViewModel.CATEGORY_ARG}}",
            arguments = listOf(
                navArgument(NotesViewModel.CATEGORY_ARG) {
                    type = NavType.StringType
                    defaultValue = NotesViewModel.NO_CATEGORY
                },
            ),
        ) {
            NotesScreen()
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
