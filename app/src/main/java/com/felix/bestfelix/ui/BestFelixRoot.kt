package com.felix.bestfelix.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.felix.bestfelix.ui.navigation.BestFelixBottomBar
import com.felix.bestfelix.ui.navigation.BestFelixNavHost
import com.felix.bestfelix.ui.navigation.TopLevelDestination

@Composable
fun BestFelixRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTab = TopLevelDestination.fromRoute(currentRoute)

    Scaffold(
        bottomBar = {
            // Pas de barre d'onglets sur les écrans secondaires (Réglages).
            if (currentTab != null) {
                BestFelixBottomBar(
                    current = currentTab,
                    onSelect = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(TopLevelDestination.TODAY.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        BestFelixNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
