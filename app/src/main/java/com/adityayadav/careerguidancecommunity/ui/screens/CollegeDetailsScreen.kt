package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.model.College

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeDetailsScreen(
    collegeId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var college by remember { mutableStateOf<College?>(null) }

    LaunchedEffect(collegeId) {
        if (collegeId != null) {
            college = CollegeRepository.getById(collegeId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(college?.name ?: "College") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            college?.let { c ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Main Name
                    Text(c.name, style = MaterialTheme.typography.headlineSmall)

                    Text("${c.city}, ${c.state} • ${c.type}")

                    Text("Rank: ${c.rank ?: "N/A"}")
                    Text("Intake: ${c.intake ?: "N/A"}")
                    Text("Average Fees: ${c.avgFees ?: "N/A"}")

                    Divider()

                    Text("About", style = MaterialTheme.typography.titleMedium)
                    Text(c.description ?: "No description provided.")

                    Divider()

                    if (!c.website.isNullOrBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, c.website.toUri())
                                context.startActivity(intent)
                            }
                        ) {
                            Text("Visit Website / Apply")
                        }
                    }
                }

            } ?: run {
                // loading state
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
