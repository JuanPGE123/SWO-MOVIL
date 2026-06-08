package com.example.swo.presentation.features.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.swo.core.base.UiState
import com.example.swo.domain.model.User
import com.example.swo.presentation.designsystem.components.SWOPremiumCard

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.example.swo.presentation.designsystem.components.NetworkImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    UserList((uiState as UiState.Success<List<User>>).data, viewModel::toggleUserStatus)
                }
                is UiState.Error -> Text((uiState as UiState.Error).message)
                else -> Unit
            }
        }
    }
}

@Composable
fun UserList(users: List<User>, onToggle: (User) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            SWOPremiumCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NetworkImage(
                            url = user.avatarUrl,
                            modifier = Modifier.size(50.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(user.role.label, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Switch(
                        checked = user.isActive,
                        onCheckedChange = { onToggle(user) }
                    )
                }
            }
        }
    }
}
