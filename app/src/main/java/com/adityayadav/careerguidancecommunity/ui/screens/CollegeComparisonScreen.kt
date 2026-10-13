package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.model.College

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeComparisonScreen(
    initialIds: List<String> = emptyList(),
    onBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var allColleges by remember { mutableStateOf<List<College>>(emptyList()) }
    var selectedCollegeIds by remember { mutableStateOf(initialIds.take(3)) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val loaded = CollegeRepository.loadAll(context)
        allColleges = loaded
        if (selectedCollegeIds.isEmpty() && loaded.size >= 2) {
            // Default to first 2 colleges if none passed
            selectedCollegeIds = loaded.take(2).map { it.id }
        }
        isLoading = false
    }

    val selectedColleges = allColleges.filter { it.id in selectedCollegeIds }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Compare Colleges")
                        Text(
                            text = "${selectedColleges.size} of 3 selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedColleges.size < 3 && allColleges.isNotEmpty()) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add college to compare")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedColleges.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("No colleges selected for comparison", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { showAddDialog = true }) {
                        Text("Select Colleges")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top control: Add college pill if room
                if (selectedColleges.size < 3) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showAddDialog = true },
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Add another college to compare (${selectedColleges.size}/3)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                // Horizontal scrollable side-by-side comparison cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    selectedColleges.forEach { college ->
                        ComparisonColumnCard(
                            college = college,
                            onRemove = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedCollegeIds = selectedCollegeIds - college.id
                            },
                            onViewDetails = { onNavigateToDetails(college.id) },
                            onVisitWebsite = {
                                college.website?.let { url -> openSafeUrl(context, url) }
                            }
                        )
                    }
                }

                // Comparison Summary Table
                ComparisonSummarySection(colleges = selectedColleges)
            }
        }

        if (showAddDialog) {
            val availableToAdd = allColleges.filter { it.id !in selectedCollegeIds }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add College to Compare") },
                text = {
                    if (availableToAdd.isEmpty()) {
                        Text("All available colleges are already added.")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableToAdd) { col ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedCollegeIds = selectedCollegeIds + col.id
                                        showAddDialog = false
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(col.name, style = MaterialTheme.typography.titleSmall)
                                            val loc = listOfNotNull(col.city, col.state).joinToString(", ")
                                            Text(loc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ComparisonColumnCard(
    college: College,
    onRemove: () -> Unit,
    onViewDetails: () -> Unit,
    onVisitWebsite: () -> Unit
) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Name & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = college.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val loc = listOfNotNull(college.city, college.state).joinToString(", ")
                    Text(
                        text = loc.ifBlank { "India" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove from compare", modifier = Modifier.size(18.dp))
                }
            }

            HorizontalDivider()

            // Key Metrics
            MetricRow(label = "NIRF Rank", value = college.rank?.let { "#$it" } ?: "Not Ranked")
            MetricRow(label = "Institute Type", value = college.type ?: "General")
            MetricRow(label = "Average Fees", value = college.avgFees ?: "Contact Institute")
            MetricRow(label = "Intake Capacity", value = college.intake ?: "UG / PG")

            HorizontalDivider()

            // Key Courses
            Text("Featured Programs", style = MaterialTheme.typography.labelMedium)
            val courses = college.courses.take(4)
            if (courses.isNotEmpty()) {
                courses.forEach { course ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(course, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Text("Information upon inquiry", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider()

            // Actions
            Button(onClick = onViewDetails, modifier = Modifier.fillMaxWidth()) {
                Text("View Full Profile")
            }
            if (!college.website.isNullOrBlank()) {
                OutlinedButton(onClick = onVisitWebsite, modifier = Modifier.fillMaxWidth()) {
                    Text("Visit Website")
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ComparisonSummarySection(colleges: List<College>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Comparison Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val bestRank = colleges.filter { it.rank != null }.minByOrNull { it.rank!! }
            if (bestRank != null) {
                Text(
                    "🏆 Best National Rank: ${bestRank.name} (Rank #${bestRank.rank})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val types = colleges.mapNotNull { it.type }.distinct()
            if (types.isNotEmpty()) {
                Text(
                    "🏛️ Institute Categories: ${types.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val cities = colleges.mapNotNull { it.city }.distinct()
            if (cities.isNotEmpty()) {
                Text(
                    "📍 Hub Locations: ${cities.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun openSafeUrl(context: Context, rawUrl: String) {
    try {
        val validUrl = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else rawUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}
