package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adityayadav.careerguidancecommunity.viewmodel.ScholarshipViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipScreen(
    onBack: () -> Unit,
    vm: ScholarshipViewModel = viewModel()
) {
    val ui by vm.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) { vm.loadAndFilter() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Scholarships") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
        })
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(12.dp)
            .fillMaxSize()) {

            OutlinedTextField(
                value = ui.searchQuery,
                onValueChange = { vm.onSearchChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search scholarships...") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            val categories = listOf("All", "Merit", "Need-based", "STEM", "Science")
            Row(modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    val sel = ui.selectedCategory.equals(cat, true)
                    AssistChip(onClick = { vm.onCategorySelected(cat) }, label = { Text(cat) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant))
                }
            }

            Spacer(Modifier.height(12.dp))

            if (ui.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (ui.error != null) {
                Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(ui.items) { s ->
                        ScholarshipListItem(s, onApply = {
                            s.applyLink?.let { url ->
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun ScholarshipListItem(s: com.adityayadav.careerguidancecommunity.data.Scholarship, onApply: ()->Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(s.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text("Category: ${s.category}", style = MaterialTheme.typography.bodySmall)
            Text("Amount: ${s.amount ?: "—"}", style = MaterialTheme.typography.bodySmall)
            Text("Deadline: ${s.deadline ?: "—"}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApply) { Text("Apply Now") }
                OutlinedButton(onClick = { /* share / save */ }) { Text("Save") }
            }
        }
    }
}
