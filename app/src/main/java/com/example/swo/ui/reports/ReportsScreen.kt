package com.example.swo.ui.reports

import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.swo.ui.theme.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Resumen", "Por Estado", "Por Prioridad", "Proyectos")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes y Estadísticas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Exportar", tint = SwoAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = DarkBackground_Slate
    ) { padding ->
        if (stats.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SwoAccent)
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Tabs de navegación
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = SwoAccent,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == i) SwoAccent else Color.Gray,
                                fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // --- TAB RESUMEN ---
                        item { SummaryKpiRow(stats) }
                        item { ResolutionRateCard(stats.resolutionRate) }
                        item { StatusDistributionChart(stats) }
                    }
                    1 -> {
                        // --- TAB POR ESTADO ---
                        item {
                            Text("Distribución por Estado", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        item { StatusPieChart(stats) }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatusDetailRow("Abiertos", stats.open, stats.total, Color(0xFFEF4444), Icons.Default.Error)
                                StatusDetailRow("En Progreso", stats.inProgress, stats.total, Color(0xFF3B82F6), Icons.Default.PendingActions)
                                StatusDetailRow("Resueltos", stats.resolved, stats.total, Color(0xFF10B981), Icons.Default.CheckCircle)
                                StatusDetailRow("Cancelados", stats.cancelled, stats.total, Color(0xFF64748B), Icons.Default.Warning)
                            }
                        }
                    }
                    2 -> {
                        // --- TAB POR PRIORIDAD ---
                        item {
                            Text("Distribución por Prioridad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        item { PriorityBarChart(stats) }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                PriorityDetailCard("Crítica", stats.critical, Color(0xFFEF4444))
                                PriorityDetailCard("Alta", stats.high, Color(0xFFF97316))
                                PriorityDetailCard("Media", stats.medium, Color(0xFFF59E0B))
                                PriorityDetailCard("Baja", stats.low, Color(0xFF10B981))
                            }
                        }
                    }
                    3 -> {
                        // --- TAB POR PROYECTO ---
                        item {
                            Text("Incidentes por Proyecto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        if (stats.byProject.isEmpty()) {
                            item {
                                Text("Sin datos de proyectos", color = Color.Gray, modifier = Modifier.padding(16.dp))
                            }
                        } else {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    stats.byProject.forEach { proj ->
                                        ProjectIncidentBar(proj.projectName, proj.count, stats.total)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- KPI Row ---
@Composable
fun SummaryKpiRow(stats: ReportStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        KpiCard("Total", stats.total.toString(), SwoAccent, Icons.Default.Assessment, Modifier.weight(1f))
        KpiCard("Abiertos", stats.open.toString(), Color(0xFFEF4444), Icons.Default.Error, Modifier.weight(1f))
        KpiCard("Resueltos", stats.resolved.toString(), Color(0xFF10B981), Icons.Default.CheckCircle, Modifier.weight(1f))
    }
}

@Composable
fun KpiCard(label: String, value: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Text(value, color = color, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

// --- Tasa de Resolución ---
@Composable
fun ResolutionRateCard(rate: Float) {
    val animatedRate by animateFloatAsState(
        targetValue = rate,
        animationSpec = tween(durationMillis = 1200),
        label = "resolution_rate"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tasa de Resolución", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${animatedRate.toInt()}%", color = Color(0xFF10B981), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            LinearProgressIndicator(
                progress = { animatedRate / 100f },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF10B981),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Text(
                "De cada 100 incidentes, ${animatedRate.toInt()} han sido resueltos exitosamente.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

// --- Gráfico de Barras: Distribución por Estado ---
@Composable
fun StatusDistributionChart(stats: ReportStats) {
    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Incidentes por Estado", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    BarChart(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setDrawGridBackground(false)
                        description.isEnabled = false
                        legend.isEnabled = false
                        setScaleEnabled(false)
                    }
                },
                update = { chart ->
                    val labels = listOf("Abiertos", "En Prog.", "Resueltos", "Cancelados")
                    val values = listOf(stats.open, stats.inProgress, stats.resolved, stats.cancelled)
                    val colors = listOf(0xFFEF4444, 0xFF3B82F6, 0xFF10B981, 0xFF64748B).map { it.toInt() }
                    val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
                    val dataSet = BarDataSet(entries, "Estado").apply {
                        this.colors = colors
                        valueTextColor = Color.White.toArgb()
                        valueTextSize = 11f
                    }
                    chart.data = BarData(dataSet).apply { barWidth = 0.6f }
                    chart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        position = XAxis.XAxisPosition.BOTTOM
                        textColor = Color.White.toArgb()
                        setDrawGridLines(false)
                        granularity = 1f
                    }
                    chart.axisLeft.apply {
                        textColor = Color.White.toArgb()
                        gridColor = Color.White.copy(alpha = 0.1f).toArgb()
                    }
                    chart.axisRight.isEnabled = false
                    chart.animateY(900)
                    chart.invalidate()
                }
            )
        }
    }
}

// --- Gráfico de Pastel: Estado ---
@Composable
fun StatusPieChart(stats: ReportStats) {
    Card(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            factory = { ctx ->
                PieChart(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    description.isEnabled = false
                    isDrawHoleEnabled = true
                    holeRadius = 42f
                    setHoleColor(SurfaceDark.toArgb())
                    setDrawCenterText(true)
                    centerText = "Estado"
                    setCenterTextColor(Color.White.toArgb())
                    setCenterTextSize(14f)
                    legend.apply {
                        textColor = Color.White.toArgb()
                        textSize = 12f
                    }
                }
            },
            update = { chart ->
                val entries = listOf(
                    PieEntry(stats.open.toFloat(), "Abiertos"),
                    PieEntry(stats.inProgress.toFloat(), "En Progreso"),
                    PieEntry(stats.resolved.toFloat(), "Resueltos"),
                    PieEntry(stats.cancelled.toFloat(), "Cancelados")
                ).filter { it.value > 0 }
                val colors = listOf(0xFFEF4444, 0xFF3B82F6, 0xFF10B981, 0xFF64748B).map { it.toInt() }
                val dataSet = PieDataSet(entries, "").apply {
                    this.colors = colors
                    valueTextColor = Color.White.toArgb()
                    valueTextSize = 12f
                    sliceSpace = 3f
                }
                chart.data = PieData(dataSet)
                chart.animateY(1000)
                chart.invalidate()
            }
        )
    }
}

// --- Fila de detalle de estado ---
@Composable
fun StatusDetailRow(label: String, count: Int, total: Int, color: Color, icon: ImageVector) {
    val pct = if (total > 0) (count.toFloat() / total) * 100f else 0f
    val animPct by animateFloatAsState(targetValue = pct, animationSpec = tween(800), label = label)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("$count (${animPct.toInt()}%)", color = color, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { animPct / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = color,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

// --- Gráfico de barras: Prioridades ---
@Composable
fun PriorityBarChart(stats: ReportStats) {
    Card(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Distribución por Prioridad", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    BarChart(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setDrawGridBackground(false)
                        description.isEnabled = false
                        legend.isEnabled = false
                        setScaleEnabled(false)
                    }
                },
                update = { chart ->
                    val labels = listOf("Crítica", "Alta", "Media", "Baja")
                    val values = listOf(stats.critical, stats.high, stats.medium, stats.low)
                    val colors = listOf(0xFFEF4444, 0xFFF97316, 0xFFF59E0B, 0xFF10B981).map { it.toInt() }
                    val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
                    val dataSet = BarDataSet(entries, "Prioridad").apply {
                        this.colors = colors
                        valueTextColor = Color.White.toArgb()
                        valueTextSize = 11f
                    }
                    chart.data = BarData(dataSet).apply { barWidth = 0.6f }
                    chart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        position = XAxis.XAxisPosition.BOTTOM
                        textColor = Color.White.toArgb()
                        setDrawGridLines(false)
                        granularity = 1f
                    }
                    chart.axisLeft.apply {
                        textColor = Color.White.toArgb()
                        gridColor = Color.White.copy(alpha = 0.1f).toArgb()
                    }
                    chart.axisRight.isEnabled = false
                    chart.animateY(900)
                    chart.invalidate()
                }
            )
        }
    }
}

// --- Tarjeta de prioridad ---
@Composable
fun PriorityDetailCard(label: String, count: Int, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(label, color = Color.White, modifier = Modifier.weight(1f))
            Text(
                text = count.toString(),
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        }
    }
}

// --- Barra de proyecto ---
@Composable
fun ProjectIncidentBar(projectName: String, count: Int, total: Int) {
    val pct = if (total > 0) count.toFloat() / total else 0f
    val animPct by animateFloatAsState(targetValue = pct, animationSpec = tween(800), label = projectName)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(projectName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text("$count incidentes", color = SwoAccent, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { animPct },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = SwoAccent,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}
