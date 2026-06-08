package com.example.swo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.swo.ui.theme.SurfaceDark

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E2D3D),
            Color(0xFF2A3D50),
            Color(0xFF1E2D3D)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 200f)
    )
}

@Composable
fun UserCardShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.55f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.75f).height(11.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Box(modifier = Modifier.width(70.dp).height(20.dp).clip(RoundedCornerShape(20.dp)).background(brush))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) {
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(brush))
                }
            }
        }
    }
}

@Composable
fun ProjectCardShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Box(modifier = Modifier.width(60.dp).height(22.dp).clip(RoundedCornerShape(20.dp)).background(brush))
            }
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(11.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFF1E2A36)))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(brush))
                }
            }
        }
    }
}

@Composable
fun KpiCardShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(brush))
                Box(modifier = Modifier.width(40.dp).height(26.dp).clip(RoundedCornerShape(6.dp)).background(brush))
            }
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(11.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        }
    }
}
