package com.example.swo.ui.screens

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.swo.core.designsystem.components.SWOCard
import com.example.swo.ui.theme.NeonBlue
import com.example.swo.ui.theme.NeonCyan
import com.example.swo.ui.theme.StatusError
import com.example.swo.ui.theme.StatusSuccess
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métricas y Rendimiento", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Resumen Gerencial",
                style = MaterialTheme.typography.titleLarge,
                color = NeonCyan,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // KPI Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                KPICard("Tickets", "142", StatusSuccess, Modifier.weight(1f))
                KPICard("Pendientes", "28", StatusError, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pie Chart Section
            SWOCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Distribución de Recursos", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    PieChartComposable()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Chart Section
            SWOCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Rendimiento Semanal", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    BarChartComposable()
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun KPICard(label: String, value: String, color: Color, modifier: Modifier) {
    SWOCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PieChartComposable() {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    800
                )
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = Color.White.toArgb()
                setHoleColor(Color.Transparent.toArgb())
                setCenterTextColor(Color.White.toArgb())
                animateY(1000)
            }
        },
        update = { chart ->
            val entries = listOf(
                PieEntry(40f, "Soporte"),
                PieEntry(30f, "Infra"),
                PieEntry(20f, "DevOps"),
                PieEntry(10f, "Otros")
            )
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(NeonBlue.toArgb(), NeonCyan.toArgb(), StatusSuccess.toArgb(), StatusError.toArgb())
                valueTextColor = Color.White.toArgb()
                valueTextSize = 12f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Composable
fun BarChartComposable() {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    800
                )
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = Color.White.toArgb()
                xAxis.setDrawGridLines(false)
                axisLeft.textColor = Color.White.toArgb()
                axisRight.isEnabled = false
                animateY(1000)
            }
        },
        update = { chart ->
            val entries = listOf(
                BarEntry(0f, 10f),
                BarEntry(1f, 20f),
                BarEntry(2f, 15f),
                BarEntry(3f, 25f),
                BarEntry(4f, 18f)
            )
            val dataSet = BarDataSet(entries, "Tickets Resueltos").apply {
                color = NeonCyan.toArgb()
                valueTextColor = Color.White.toArgb()
            }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Lun", "Mar", "Mie", "Jue", "Vie"))
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}
