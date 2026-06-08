package com.example.swo.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.swo.data.incidents.local.IncidentEntity
import com.example.swo.ui.components.DynamicAvatar
import com.example.swo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    userName: String = "Usuario",
    onNavigateToSettings: () -> Unit,
    onNavigateToIncidents: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToChatbot: () -> Unit
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val stats by viewModel.stats.collectAsState()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(containerColor = DarkBackground_Slate) { padding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 60 }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Hero Banner
                item {
                    HeroBanner(userName = userName, onSettingsClick = onNavigateToSettings)
                }

                // 2. KPI Cards
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            "Panel de Control",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label = "Total Incidencias",
                                value = stats.totalIncidents,
                                icon = Icons.Default.BugReport,
                                color = SwoLightBlue
                            )
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label = "Abiertas",
                                value = stats.openIncidents,
                                icon = Icons.Default.ErrorOutline,
                                color = Color(0xFFEF4444)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label = "En Progreso",
                                value = stats.inProgressIncidents,
                                icon = Icons.Default.Autorenew,
                                color = Color(0xFFF59E0B)
                            )
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label = "Resueltas",
                                value = stats.resolvedIncidents,
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                // 3. Status progress bar
                if (!stats.isLoading && stats.totalIncidents > 0) {
                    item {
                        StatusBreakdownCard(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            total = stats.totalIncidents,
                            open = stats.openIncidents,
                            inProgress = stats.inProgressIncidents,
                            resolved = stats.resolvedIncidents
                        )
                    }
                }

                // 4. Module grid
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            "Módulos del Sistema",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModuleTile(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Warning,
                                label = "Incidencias",
                                subtitle = "${stats.totalIncidents} registros",
                                gradient = listOf(Color(0xFF1E3A5F), Color(0xFF2563EB)),
                                onClick = onNavigateToIncidents
                            )
                            ModuleTile(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.RocketLaunch,
                                label = "Proyectos",
                                subtitle = "${stats.totalProjects} activos",
                                gradient = listOf(Color(0xFF1A1A2E), Color(0xFF7C3AED)),
                                onClick = onNavigateToProjects
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModuleTile(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Group,
                                label = "Usuarios",
                                subtitle = "${stats.totalUsers} registrados",
                                gradient = listOf(Color(0xFF0F2027), Color(0xFF00B4D8)),
                                onClick = onNavigateToUsers
                            )
                            ModuleTile(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Assessment,
                                label = "Reportes",
                                subtitle = "Analytics & KPIs",
                                gradient = listOf(Color(0xFF1A2A1A), Color(0xFF059669)),
                                onClick = onNavigateToReports
                            )
                        }
                    }
                }

                // 5. Quick actions
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            "Acciones Rápidas",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.SmartToy,
                                label = "Asistente IA",
                                color = SwoAccent,
                                onClick = onNavigateToChatbot
                            )
                            QuickActionChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AddTask,
                                label = "Nueva Incidencia",
                                color = Color(0xFFEF4444),
                                onClick = onNavigateToIncidents
                            )
                        }
                    }
                }

                // 6. Recent activity
                if (stats.recentIncidents.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Actividad Reciente",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                TextButton(onClick = onNavigateToIncidents) {
                                    Text("Ver todo", color = SwoAccent, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    items(stats.recentIncidents.take(4)) { incident ->
                        RecentIncidentRow(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            incident = incident,
                            onClick = onNavigateToIncidents
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun HeroBanner(userName: String, onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0D2137), Color(0xFF1A3A5C), Color(0xFF0D2137))
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(SwoAccent.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .clip(CircleShape)
                .background(SwoLightBlue.copy(alpha = 0.06f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DynamicAvatar(imageUrl = null)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = SimpleDateFormat("EEEE d MMM", Locale("es")).format(Date()).replaceFirstChar { it.uppercase() },
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Hola, $userName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Sistema activo", color = Color(0xFF10B981), fontSize = 11.sp)
                    }
                }
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.07f))
            ) {
                Icon(Icons.Default.Settings, null, tint = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    icon: ImageVector,
    color: Color
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "kpi"
    )
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "$animatedValue",
                    color = color,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StatusBreakdownCard(
    modifier: Modifier = Modifier,
    total: Int,
    open: Int,
    inProgress: Int,
    resolved: Int
) {
    val openFrac = if (total > 0) open.toFloat() / total else 0f
    val inProgressFrac = if (total > 0) inProgress.toFloat() / total else 0f
    val resolvedFrac = if (total > 0) resolved.toFloat() / total else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Distribución de estados", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Segmented bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                val animOpen by animateFloatAsState(openFrac, tween(900), label = "o")
                val animProgress by animateFloatAsState(inProgressFrac, tween(900), label = "p")
                val animResolved by animateFloatAsState(resolvedFrac, tween(900), label = "r")

                if (animOpen > 0f) Box(Modifier.fillMaxHeight().weight(animOpen).background(Color(0xFFEF4444)))
                if (animProgress > 0f) Box(Modifier.fillMaxHeight().weight(animProgress).background(Color(0xFFF59E0B)))
                if (animResolved > 0f) Box(Modifier.fillMaxHeight().weight(animResolved).background(Color(0xFF10B981)))
                val remaining = 1f - animOpen - animProgress - animResolved
                if (remaining > 0f) Box(Modifier.fillMaxHeight().weight(remaining).background(Color(0xFF2A3A4A)))
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusLegend(color = Color(0xFFEF4444), label = "Abiertos", count = open)
                StatusLegend(color = Color(0xFFF59E0B), label = "En Prog.", count = inProgress)
                StatusLegend(color = Color(0xFF10B981), label = "Resueltos", count = resolved)
                StatusLegend(color = Color(0xFF6B7280), label = "Otros", count = total - open - inProgress - resolved)
            }
        }
    }
}

@Composable
private fun StatusLegend(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text("$count", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(label, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ModuleTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(gradient))
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(18.dp)
        )
    }
}

@Composable
private fun QuickActionChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = color, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun RecentIncidentRow(
    modifier: Modifier = Modifier,
    incident: IncidentEntity,
    onClick: () -> Unit
) {
    val priorityColor = when (incident.priority) {
        "CRITICAL" -> Color(0xFFEF4444)
        "HIGH" -> Color(0xFFF97316)
        "MEDIUM" -> Color(0xFFF59E0B)
        else -> Color(0xFF6B7280)
    }
    val statusLabel = when (incident.status) {
        "OPEN" -> "Abierta"
        "IN_PROGRESS" -> "En progreso"
        "RESOLVED" -> "Resuelta"
        else -> incident.status
    }
    val statusColor = when (incident.status) {
        "OPEN" -> Color(0xFFEF4444)
        "IN_PROGRESS" -> Color(0xFFF59E0B)
        "RESOLVED" -> Color(0xFF10B981)
        else -> Color.Gray
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(priorityColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Warning, null, tint = priorityColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                incident.title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                incident.projectName ?: "—",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(statusLabel, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun animateFloatAsState(fraction: Float, spec: AnimationSpec<Float>, label: String): State<Float> {
    return androidx.compose.animation.core.animateFloatAsState(
        targetValue = fraction,
        animationSpec = spec,
        label = label
    )
}
