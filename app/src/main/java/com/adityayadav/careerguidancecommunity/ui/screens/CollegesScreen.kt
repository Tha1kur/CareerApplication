package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adityayadav.careerguidancecommunity.viewmodel.CollegesViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.adityayadav.careerguidancecommunity.data.College

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegesScreen(
    viewModel: CollegesViewModel = viewModel(),
    onExploreColleges: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // If you want to set remote JSON URL (open data source), set it here:
    // viewModel.setRemoteJsonUrl("https://example.com/colleges.json")
    LaunchedEffect(Unit) { viewModel.loadAndFilter() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Colleges") }, navigationIcon = { /* optional back */ })
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(12.dp)
            .fillMaxSize()) {

            // Search
            OutlinedTextField(
                value = ui.searchQuery,
                onValueChange = { viewModel.onSearchChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search colleges, city, courses...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { /* close keyboard */ })
            )

            Spacer(Modifier.height(12.dp))

            // Filter chips - horizontal scroll
            val chips = listOf("All", "Engineering", "Design", "Management", "Science", "Private", "Government")
            Row(modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.forEach { c ->
                    val selected = ui.selectedFilter.equals(c, true)
                    FilterChip(selected = selected, onClick = { viewModel.onFilterSelected(c) }, label = { Text(c) })
                }
            }

            Spacer(Modifier.height(12.dp))

            // Loading / error
            if (ui.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ui.error != null) {
                Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(ui.items) { c ->
                        CollegeListItem(c, onClick = { onNavigate("college/${c.id}") })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(selected: Boolean, onClick: () -> Unit, label: @Composable () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = label,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun CollegeListItem(c: College, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(c.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text("${c.city ?: ""}, ${c.state ?: ""} • ${c.type ?: ""}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
            Text(c.description ?: "", maxLines = 3, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
