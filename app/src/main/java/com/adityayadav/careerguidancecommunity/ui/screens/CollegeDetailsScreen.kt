package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.core.net.toUri
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
// import both possible College types with aliases to avoid ambiguity
import com.adityayadav.careerguidancecommunity.data.College as DataCollege
import com.adityayadav.careerguidancecommunity.data.model.College as ModelCollege

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeDetailsScreen(
    collegeId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // hold a generic object (may be DataCollege or ModelCollege)
    var collegeAny by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(collegeId) {
        collegeAny = null
        if (collegeId != null) {
            // First try repository getById (preferred)
            val byId = try {
                CollegeRepository.getById(collegeId)
            } catch (_: Throwable) {
                null
            }

            if (byId != null) {
                collegeAny = byId
            } else {
                // fallback: load all and find a match by checking both known types
                val list = try {
                    CollegeRepository.loadAll(context)
                } catch (_: Throwable) {
                    emptyList<Any>()
                }

                val found = list.firstOrNull { item ->
                    when (item) {
                        is DataCollege -> item.id == collegeId
                        is ModelCollege -> item.id == collegeId
                        else -> false
                    }
                }
                collegeAny = found
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val c = collegeAny) {
                        is DataCollege -> c.name
                        is ModelCollege -> c.name
                        else -> "College"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val c = collegeAny
            if (c != null) {
                // extract fields in a type-safe way for both possible college classes
                val name = when (c) {
                    is DataCollege -> c.name
                    is ModelCollege -> c.name
                    else -> ""
                }
                val city = when (c) {
                    is DataCollege -> c.city ?: "—"
                    is ModelCollege -> c.city ?: "—"
                    else -> "—"
                }
                val state = when (c) {
                    is DataCollege -> c.state ?: "—"
                    is ModelCollege -> c.state ?: "—"
                    else -> "—"
                }
                val type = when (c) {
                    is DataCollege -> c.type ?: "—"
                    is ModelCollege -> c.type ?: "—"
                    else -> "—"
                }
                val rankText = when (c) {
                    is DataCollege -> c.rank?.toString() ?: "N/A"
                    is ModelCollege -> c.rank?.toString() ?: "N/A"
                    else -> "N/A"
                }
                val intakeText = when (c) {
                    is DataCollege -> c.intake?.toString() ?: "N/A"
                    is ModelCollege -> c.intake?.toString() ?: "N/A"
                    else -> "N/A"
                }
                val fees = when (c) {
                    is DataCollege -> c.avgFees ?: "N/A"
                    is ModelCollege -> c.avgFees ?: "N/A"
                    else -> "N/A"
                }
                val description = when (c) {
                    is DataCollege -> c.description ?: "No description provided."
                    is ModelCollege -> c.description ?: "No description provided."
                    else -> "No description provided."
                }
                // website might not exist on DataCollege; handle safely
                val website = when (c) {
                    is DataCollege -> null
                    is ModelCollege -> c.website
                    else -> null
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(name, style = MaterialTheme.typography.headlineSmall)
                    Text("$city, $state • $type", style = MaterialTheme.typography.bodyMedium)
                    Text("Rank: $rankText", style = MaterialTheme.typography.bodyMedium)
                    Text("Intake: $intakeText", style = MaterialTheme.typography.bodyMedium)
                    Text("Average fees: $fees", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("About", style = MaterialTheme.typography.titleMedium)
                    Text(description, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    website?.let { url ->
                        if (url.isNotBlank()) {
                            Text("Website: $url", color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    // If you want, show a Toast here (keeping code simple for now)
                                }
                            }) {
                                Text("Visit Website / Apply")
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }
        }
    }
}
