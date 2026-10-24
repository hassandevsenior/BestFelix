package com.felix.bestfelix.ui.habits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.habits.good.GoodHabitsPane
import com.felix.bestfelix.ui.habits.quit.QuitHabitsPane

private val TABS = listOf("À prendre", "À abandonner")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Habitudes") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                TABS.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label) },
                    )
                }
            }
            when (selectedTab) {
                0 -> GoodHabitsPane(
                    snackbarHostState = snackbarHostState,
                    onOpenNotes = onOpenNotes,
                )
                else -> QuitHabitsPane(
                    snackbarHostState = snackbarHostState,
                    onOpenNotes = onOpenNotes,
                )
            }
        }
    }
}
