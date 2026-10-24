package com.felix.bestfelix.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.pain.BalancePoint
import com.felix.bestfelix.ui.theme.BestFelixTheme

/**
 * Courbe du solde dans le temps. Ligne de zéro en pointillés, dernier point mis en avant,
 * et un point rouge sur chaque jour non tenu : les chutes doivent sauter aux yeux.
 */
@Composable
fun BalanceChart(
    points: List<BalancePoint>,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 120.dp,
) {
    val accents = BestFelixTheme.accents
    val outline = MaterialTheme.colorScheme.outlineVariant
    val lineColor = if (points.lastOrNull()?.balance?.let { it < 0 } == true) accents.pain else accents.reward

    if (points.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "La courbe apparaîtra après deux jours.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    val values = points.map { it.balance }
    val maxValue = maxOf(values.max(), 0)
    val minValue = minOf(values.min(), 0)
    val span = (maxValue - minValue).coerceAtLeast(1)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                contentDescription = "Courbe du solde, de ${values.first()} à ${values.last()} XP"
            },
    ) {
        val padding = 6.dp.toPx()
        val usableHeight = size.height - padding * 2
        val stepX = size.width / (points.size - 1)

        fun yOf(value: Int): Float =
            padding + usableHeight * (1f - (value - minValue).toFloat() / span)

        fun offsetOf(index: Int): Offset = Offset(index * stepX, yOf(values[index]))

        // Ligne de zéro en pointillés.
        val zeroY = yOf(0)
        drawLine(
            color = outline,
            start = Offset(0f, zeroY),
            end = Offset(size.width, zeroY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
        )

        // La courbe.
        val path = Path().apply {
            moveTo(offsetOf(0).x, offsetOf(0).y)
            for (index in 1 until points.size) {
                val point = offsetOf(index)
                lineTo(point.x, point.y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )

        // Un point rouge à chaque chute.
        for (index in 1 until points.size) {
            if (values[index] < values[index - 1]) {
                drawCircle(
                    color = accents.pain,
                    radius = 3.dp.toPx(),
                    center = offsetOf(index),
                )
            }
        }

        // Dernier point mis en avant.
        val last = offsetOf(points.size - 1)
        drawCircle(color = lineColor, radius = 5.dp.toPx(), center = last)
        drawCircle(
            color = lineColor,
            radius = 8.dp.toPx(),
            center = last,
            style = Stroke(width = 1.5.dp.toPx()),
        )
    }
}
