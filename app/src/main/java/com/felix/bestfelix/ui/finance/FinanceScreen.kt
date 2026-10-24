package com.felix.bestfelix.ui.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.components.StatTile
import com.felix.bestfelix.ui.notes.NotesLink
import com.felix.bestfelix.ui.finance.credits.CreditsPane
import com.felix.bestfelix.ui.finance.incomes.IncomesPane
import com.felix.bestfelix.ui.theme.BestFelixTheme

private val TABS = listOf("Crédits", "Entrées")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val accents = BestFelixTheme.accents

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Finances") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Les trois tuiles de résumé restent visibles au-dessus des deux onglets.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                NotesLink(category = NoteCategory.FINANCE, onOpenNotes = onOpenNotes)
            }

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
                0 -> CreditsPane(state = state, viewModel = viewModel)
                else -> IncomesPane(state = state, viewModel = viewModel)
            }
        }
    }
}
