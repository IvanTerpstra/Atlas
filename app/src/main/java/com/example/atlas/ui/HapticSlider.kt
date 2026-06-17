package com.example.atlas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat

@Composable
fun HapticSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    color: Color = Color(0xFFA78BFA),
    labelText: (Float) -> String = { it.toInt().toString() }
) {
    val view = LocalView.current
    val range = valueRange.endInclusive - valueRange.start
    
    // Initialize step based on 10% increments
    var lastHapticStep by remember(valueRange) {
        val initialPercent = if (range > 0) ((value - valueRange.start) / range * 100f) else 0f
        mutableIntStateOf((initialPercent / 10f).toInt())
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val fraction = if (range > 0) (value - valueRange.start) / range else 0f

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(start = (fraction * 280).dp.coerceIn(0.dp, 260.dp))
                ) {
                    Text(
                        text = labelText(value),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = { newVal ->
                onValueChange(newVal)
                
                // Calculate if we've crossed a 10% threshold
                val percent = if (range > 0) ((newVal - valueRange.start) / range * 100f) else 0f
                val currentStep = (percent / 10f).toInt()
                
                if (currentStep != lastHapticStep) {
                    ViewCompat.performHapticFeedback(
                        view,
                        HapticFeedbackConstantsCompat.CLOCK_TICK
                    )
                    lastHapticStep = currentStep
                }
            },
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
