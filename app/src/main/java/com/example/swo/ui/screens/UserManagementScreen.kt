package com.example.swo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swo.core.designsystem.components.SWOCard
import com.example.swo.domain.auth.User
import com.example.swo.domain.auth.UserRole
import com.example.swo.ui.theme.NeonBlue
import com.example.swo.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(navController: NavController) {
    val dummyUsers = remember {
        mutableStateListOf(
            User("1", "102030", "Carlos Pérez", UserRole.SUPPORT),
            User("2", "405060", "Admin Root", UserRole.ADMIN),
            User("3", "708090", "Cliente VIP", UserRole.CLIENT)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Usuarios", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }, containerColor = NeonBlue) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyUsers) { user ->
                UserItem(user)
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    var isActive by remember { mutableStateOf(true) }

    SWOCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = if (isActive) NeonCyan else Color.Gray
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else Color.Gray
                )
                Text(
                    text = "${user.role.name} • ID: ${user.corporateId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonBlue,
                    checkedTrackColor = NeonBlue.copy(alpha = 0.5f)
                )
            )
        }
    }
}
