package com.felix.bestfelix.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

/** Chiffres tabulaires : les colonnes de montants et d'XP ne doivent pas danser. */
private const val TABULAR = "tnum"

/** À appliquer à tout texte qui affiche un nombre (XP, FCFA, pourcentages). */
fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = TABULAR)

internal val BestFelixTypography = Typography()
