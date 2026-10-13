package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.ScholarshipRepository
import com.adityayadav.careerguidancecommunity.data.model.College
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val savedCollegeIds by DataStoreManager.savedCollegesFlow(context).collectAsState(initial = emptySet())
    val savedScholarshipIds by DataStoreManager.savedScholarshipsFlow(context).collectAsState(initial = emptySet())

    var savedColleges by remember { mutableStateOf<List<College>>(emptyList()) }
    var savedScholarships by remember { mutableStateOf<List<Scholarship>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(savedCollegeIds) {
        savedColleges = CollegeRepository.getByIds(context, savedCollegeIds)
        isLoading = false
    }

    LaunchedEffect(savedScholarshipIds) {
        savedScholarships = ScholarshipRepository.getByIds(context, savedScholarshipIds)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Saved Opportunities")
                        Text(
                            text = "${savedCollegeIds.size} Colleges • ${savedScholarshipIds.size} Scholarships",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Colleges (${savedColleges.size})") },
                    icon = { Icon(Icons.Default.School, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Scholarships (${savedScholarships.size})") },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                )
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTabIndex) {
                    0 -> {
                        if (savedColleges.isEmpty()) {
                            EmptySavedState(
                                icon = Icons.Default.School,
                                title = "No saved colleges yet",
                                subtitle = "Explore institutes across India and bookmark them to keep tabs on admission dates, fees, and requirements.",
                                actionText = "Explore Colleges",
                                onAction = { onNavigate("colleges") }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(savedColleges, key = { it.id }) { college ->
                                    SavedCollegeCard(
                                        college = college,
                                        onClick = { onNavigate("college/${college.id}") },
                                        onRemove = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                DataStoreManager.toggleSaveCollege(context, college.id)
                                            }
                                        },
                                        onCompare = {
                                            onNavigate("compare?ids=${college.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        if (savedScholarships.isEmpty()) {
                            EmptySavedState(
                                icon = Icons.Default.Bookmark,
                                title = "No saved scholarships yet",
                                subtitle = "Save scholarship opportunities here to track grant deadlines, application criteria, and funding amounts.",
                                actionText = "Browse Scholarships",
                                onAction = { onNavigate("scholarships") }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(savedScholarships, key = { it.id }) { scholarship ->
                                    SavedScholarshipCard(
                                        scholarship = scholarship,
                                        onRemove = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                DataStoreManager.toggleSaveScholarship(context, scholarship.id)
                                            }
                                        },
                                        onApply = {
                                            scholarship.applyLink?.let { link ->
                                                openSafeUrl(context, link)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCollegeCard(
    college: College,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onCompare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = college.name.firstOrNull()?.uppercase() ?: "C",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = college.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    val location = listOfNotNull(college.city, college.state).joinToString(", ")
                    Text(
                        text = location.ifBlank { "India" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from saved",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (college.rank != null) {
                    Text(
                        text = "NIRF Rank: #${college.rank}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!college.avgFees.isNullOrBlank()) {
                    Text(
                        text = "Avg Fees: ${college.avgFees}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onCompare) {
                    Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Compare")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onClick) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
private fun SavedScholarshipCard(
    scholarship: Scholarship,
    onRemove: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scholarship.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = scholarship.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = "Remove scholarship",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Grant: ${scholarship.amount ?: "Varies"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Deadline: ${scholarship.deadline ?: "Rolling / Unspecified"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!scholarship.eligibility.isNullOrBlank()) {
                Text(
                    text = "Eligibility: ${scholarship.eligibility}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onApply) {
                    Text("Apply Now")
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAction) {
                Text(actionText)
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
