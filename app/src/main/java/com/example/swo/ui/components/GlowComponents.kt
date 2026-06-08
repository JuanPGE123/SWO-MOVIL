package com.example.swo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.swo.ui.theme.GradientEnd
import com.example.swo.ui.theme.GradientStart

@Composable
fun GlowBorder(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        content = content
    )
}

@Composable
fun DynamicAvatar(
    imageUrl: String?,
    size: Dp = 56.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                drawCircle(
                    brush = Brush.sweepGradient(listOf(GradientStart, GradientEnd, GradientStart)),
                    radius = size.toPx() / 2 + 4,
                    alpha = 0.6f
                )
            }
            .clip(CircleShape)
            .background(Color.Gray) // Placeholder
    ) {
        // Aquí iría AsyncImage de Coil
    }
}
