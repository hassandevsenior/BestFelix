package com.felix.bestfelix.ui.finance.credits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.finance.CreditRow
import com.felix.bestfelix.ui.finance.FinanceUiState
import com.felix.bestfelix.ui.finance.FinanceViewModel

private enum class CreditFilter(val label: String) {
    ALL("Tous"),
    I_OWE("Je dois"),
    OWED_TO_ME("On me doit"),
}

@Composable
fun CreditsPane(
    state: FinanceUiState,
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
) {
    var filter by rememberSaveable { mutableStateOf(CreditFilter.ALL) }
    var sheet by remember { mutableStateOf<CreditSheetTarget?>(null) }
    var repayFor by remember { mutableStateOf<CreditRow?>(null) }

    val rows = state.credits.filter { row ->
        when (filter) {
            CreditFilter.ALL -> true
            CreditFilter.I_OWE -> row.credit.direction == CreditDirection.I_OWE
            CreditFilter.OWED_TO_ME -> row.credit.direction == CreditDirection.OWED_TO_ME
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CreditFilter.entries.forEach { candidate ->
                        FilterChip(
                            selected = filter == candidate,
                            onClick = { filter = candidate },
                            label = { Text(candidate.label) },
                        )
                    }
                }
            }

            if (!state.loading && rows.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        title = "Aucun crédit",
                        message = "Note ce que tu dois et ce qu'on te doit, avec la date limite : " +
                            "l'appli calcule le reste à payer et signale les retards.",
                        actionLabel = "Ajouter un crédit",
                        onAction = { sheet = CreditSheetTarget.New },
                    )
                }
            }

            items(items = rows, key = { it.credit.id }) { row ->
                CreditCard(
                    row = row,
                    onClick = { sheet = CreditSheetTarget.Edit(row) },
                    onRepay = { repayFor = row },
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = CreditSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Crédit", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        CreditSheet(
            editing = (target as? CreditSheetTarget.Edit)?.row,
            onDismiss = { sheet = null },
            onCreate = viewModel::createCredit,
            onUpdate = viewModel::updateCredit,
            onDelete = viewModel::deleteCredit,
            onDeleteRepayment = viewModel::deleteRepayment,
        )
    }

    repayFor?.let { row ->
        RepaymentSheet(
            row = row,
            today = viewModel.today(),
            onDismiss = { repayFor = null },
            onConfirm = { date, amount -> viewModel.addRepayment(row.credit.id, date, amount) },
        )
    }
}

private sealed interface CreditSheetTarget {
    data object New : CreditSheetTarget
    data class Edit(val row: CreditRow) : CreditSheetTarget
}
