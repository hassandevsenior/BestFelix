package com.felix.bestfelix.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.ui.theme.tabular

/**
 * Saisie d'un montant en FCFA : chiffres uniquement, et le montant formaté est rappelé
 * juste en dessous du champ.
 */
@Composable
fun MoneyField(
    label: String,
    digits: String,
    onDigitsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val amount = MoneyFormat.parse(digits)

    OutlinedTextField(
        value = digits,
        onValueChange = { input -> onDigitsChange(input.filter { it.isDigit() }.take(12)) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = {
            Text(
                text = amount?.let { MoneyFormat.format(it) } ?: "Montant en FCFA",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall.tabular(),
            )
        },
        modifier = modifier.fillMaxWidth(),
    )
}
