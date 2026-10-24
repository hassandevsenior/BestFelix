package com.felix.bestfelix.core.ui

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

/** Petit « tac » discret à la validation. Pas besoin de la permission VIBRATE pour ça. */
@Composable
fun rememberLightTick(): () -> Unit {
    val view: View = LocalView.current
    return { view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) }
}
