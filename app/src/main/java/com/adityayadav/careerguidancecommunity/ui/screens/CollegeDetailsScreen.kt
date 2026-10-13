package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.College
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeDetailsScreen(
    collegeId: String?,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var college by remember { mutableStateOf<College?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val savedCollegeIds by DataStoreManager.savedCollegesFlow(context).collectAsState(initial = emptySet())
    val isSaved = collegeId != null && savedCollegeIds.contains(collegeId)

    LaunchedEffect(collegeId) {
        isLoading = true
        if (collegeId != null) {
            college = CollegeRepository.getById(context, collegeId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(college?.name ?: "College Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (collegeId != null) {
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigate("compare?ids=$collegeId")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = "Compare this college")
                        }
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                DataStoreManager.toggleSaveCollege(context, collegeId)
                            }
                        }) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Bookmark",
                                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                college != null -> {
                    val c = college!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Main Name
                        Text(c.name, style = MaterialTheme.typography.headlineSmall)

                        val location = listOfNotNull(c.city, c.state)
                            .joinToString(", ")
                            .ifBlank { "Location to be confirmed" }
                        val typeText = if (!c.type.isNullOrBlank()) " • ${c.type}" else ""
                        Text("$location$typeText", style = MaterialTheme.typography.bodyMedium)

                        Text("Rank: ${c.rank ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Intake: ${c.intake ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Average Fees: ${c.avgFees ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

                        HorizontalDivider()

                        Text("About", style = MaterialTheme.typography.titleMedium)
                        Text(c.description ?: "No description provided.", style = MaterialTheme.typography.bodyMedium)

                        HorizontalDivider()

                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigate("compare?ids=${c.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null)
                            Text("Compare this College", modifier = Modifier.padding(start = 8.dp))
                        }

                        if (!c.website.isNullOrBlank()) {
                            Button(
                                onClick = { openCollegeWebsite(context, c.website) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Visit Website / Apply")
                            }
                        }
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("College not found.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun openCollegeWebsite(ctx: Context, rawUrl: String) {
    try {
        val validUrl = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else rawUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(ctx, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}
