package com.felix.bestfelix.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Couleurs sémantiques que Material 3 ne fournit pas : la récompense (or), la douleur (rouge)
 * et la réussite (vert). On y accède via [BestFelixTheme.accents].
 */
@Immutable
data class Accents(
    val reward: Color,
    val onReward: Color,
    val rewardContainer: Color,
    val onRewardContainer: Color,
    val pain: Color,
    val onPain: Color,
    val painContainer: Color,
    val onPainContainer: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val neutralTrack: Color,
)

internal val LightAccents = Accents(
    reward = Gold40,
    onReward = Color.White,
    rewardContainer = Gold95,
    onRewardContainer = Gold20,
    pain = Red40,
    onPain = Color.White,
    painContainer = Red90,
    onPainContainer = Red20,
    success = Green40,
    onSuccess = Color.White,
    successContainer = Green90,
    onSuccessContainer = Green20,
    neutralTrack = NeutralLightSurfaceVariant,
)

internal val DarkAccents = Accents(
    reward = Gold80,
    onReward = Gold20,
    rewardContainer = Gold30,
    onRewardContainer = Gold90,
    pain = Red70,
    onPain = Red20,
    painContainer = Red30,
    onPainContainer = Red90,
    success = Green70,
    onSuccess = Green20,
    successContainer = Green30,
    onSuccessContainer = Green90,
    neutralTrack = NeutralDarkSurfaceVariant,
)

internal val LocalAccents = staticCompositionLocalOf { LightAccents }
