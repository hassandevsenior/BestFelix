package com.felix.bestfelix.ui.finance.incomes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.finance.FinanceUiState
import com.felix.bestfelix.ui.finance.FinanceViewModel
import com.felix.bestfelix.ui.theme.tabular

@Composable
fun IncomesPane(
    state: FinanceUiState,
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
) {
    var sheet by remember { mutableStateOf<IncomeSheetTarget?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (!state.loading && state.incomeMonths.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Payments,
                title = "Aucune entrée",
                message = "Note tes entrées d'argent avec leur source : elles sont regroupées " +
                    "par mois, avec le total de chaque mois.",
                actionLabel = "Ajouter une entrée",
                onAction = { sheet = IncomeSheetTarget.New },
                modifier = Modifier.align(Alignment.TopCenter),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            ) {
                state.incomeMonths.forEach { month ->
                    item(key = "mois-${month.month}") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = DateFormatFr.monthYear(month.month.atDay(1))
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = MoneyFormat.format(month.total),
                                style = MaterialTheme.typography.titleSmall.tabular(),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    items(
                        count = month.incomes.size,
                        key = { index -> "entree-${month.incomes[index].id}" },
                    ) { index ->
                        IncomeRow(
                            income = month.incomes[index],
                            onClick = { sheet = IncomeSheetTarget.Edit(month.incomes[index]) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = IncomeSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Entrée", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        IncomeSheet(
            editing = (target as? IncomeSheetTarget.Edit)?.income,
            today = viewModel.today(),
            onDismiss = { sheet = null },
            onCreate = viewModel::createIncome,
            onUpdate = viewModel::updateIncome,
            onDelete = viewModel::deleteIncome,
        )
    }
}

@Composable
private fun IncomeRow(
    income: Income,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = income.source, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = DateFormatFr.short(income.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = MoneyFormat.format(income.amount),
            style = MaterialTheme.typography.bodyLarge.tabular(),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private sealed interface IncomeSheetTarget {
    data object New : IncomeSheetTarget
    data class Edit(val income: Income) : IncomeSheetTarget
}
