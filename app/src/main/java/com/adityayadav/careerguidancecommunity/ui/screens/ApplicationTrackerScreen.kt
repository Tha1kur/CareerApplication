package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.ScholarshipRepository
import com.adityayadav.careerguidancecommunity.data.model.ApplicationStage
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import com.adityayadav.careerguidancecommunity.data.model.TrackedScholarship
import kotlinx.coroutines.launch

private val STANDARD_REQUIRED_DOCUMENTS = listOf(
    "Academic Mark Sheets & Transcripts",
    "Family Income / EWS Certificate",
    "Government Photo ID (Aadhaar / Voter ID)",
    "Statement of Purpose (SOP)",
    "Letter of Recommendation (LOR)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTrackerScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val trackedList by DataStoreManager.trackedScholarshipsFlow(context).collectAsState(initial = emptyList())
    val savedIds by DataStoreManager.savedScholarshipsFlow(context).collectAsState(initial = emptySet())

    var allCatalogScholarships by remember { mutableStateOf<List<Scholarship>>(emptyList()) }
    var selectedFilterStage by remember { mutableStateOf<ApplicationStage?>(null) } // null = All

    var showAddDialog by remember { mutableStateOf(false) }
    var editingNotesItem by remember { mutableStateOf<TrackedScholarship?>(null) }
    var tempNoteText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        allCatalogScholarships = ScholarshipRepository.loadAll(context)
    }

    val filteredList = remember(trackedList, selectedFilterStage) {
        if (selectedFilterStage == null) {
            trackedList
        } else {
            trackedList.filter { it.stage == selectedFilterStage }
        }
    }

    val discoveredCount = trackedList.count { it.stage == ApplicationStage.DISCOVERED }
    val docsReadyCount = trackedList.count { it.stage == ApplicationStage.DOCS_READY }
    val appliedCount = trackedList.count { it.stage == ApplicationStage.APPLIED }
    val awardedCount = trackedList.count { it.stage == ApplicationStage.AWARDED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "Application Tracker")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Scholarship to Tracker")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pipeline Stage Metrics Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilterStage == null,
                            onClick = { selectedFilterStage = null },
                            label = { Text("All (${trackedList.size})") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStage == ApplicationStage.DISCOVERED,
                            onClick = { selectedFilterStage = ApplicationStage.DISCOVERED },
                            label = { Text("🎯 Discovered ($discoveredCount)") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStage == ApplicationStage.DOCS_READY,
                            onClick = { selectedFilterStage = ApplicationStage.DOCS_READY },
                            label = { Text("📋 Docs Ready ($docsReadyCount)") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStage == ApplicationStage.APPLIED,
                            onClick = { selectedFilterStage = ApplicationStage.APPLIED },
                            label = { Text("🚀 Applied ($appliedCount)") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStage == ApplicationStage.AWARDED,
                            onClick = { selectedFilterStage = ApplicationStage.AWARDED },
                            label = { Text("🏆 Awarded ($awardedCount)") }
                        )
                    }
                }
            }

            if (trackedList.isEmpty()) {
                // Empty state with quick import
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Text(
                            text = "No Tracked Applications Yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Track your scholarship application deadlines, document checklist readiness, and progression from discovery to award.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (savedIds.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scope.launch {
                                            val savedItems = allCatalogScholarships.filter { it.id in savedIds }
                                            savedItems.forEach { s ->
                                                DataStoreManager.trackScholarship(
                                                    context,
                                                    TrackedScholarship(
                                                        scholarshipId = s.id,
                                                        scholarshipName = s.name,
                                                        provider = s.category,
                                                        amount = s.amount ?: "Varies",
                                                        deadline = s.deadline ?: "Rolling",
                                                        stage = ApplicationStage.DISCOVERED,
                                                        updatedAt = System.currentTimeMillis()
                                                    )
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Text("Import Saved (${savedIds.size})")
                                }
                            }

                            FilledTonalButton(onClick = { onNavigate("scholarships") }) {
                                Text("Explore Catalog")
                            }
                        }
                    }
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No applications in this stage.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredList, key = { it.scholarshipId }) { item ->
                        TrackedScholarshipCard(
                            item = item,
                            onStageChange = { newStage ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    DataStoreManager.updateScholarshipStage(context, item.scholarshipId, newStage)
                                    if (newStage == ApplicationStage.AWARDED) {
                                        DataStoreManager.addPoints(context, 100)
                                        DataStoreManager.addBadge(context, "Scholarship Awardee")
                                    }
                                }
                            },
                            onToggleDocument = { docName ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                scope.launch {
                                    DataStoreManager.toggleScholarshipDocument(context, item.scholarshipId, docName)
                                }
                            },
                            onEditNotes = {
                                editingNotesItem = item
                                tempNoteText = item.notes
                            },
                            onUntrack = {
                                scope.launch {
                                    DataStoreManager.untrackScholarship(context, item.scholarshipId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Scholarship from Catalog Dialog
    if (showAddDialog) {
        val untrackedCatalog = remember(allCatalogScholarships, trackedList) {
            val trackedIds = trackedList.map { it.scholarshipId }.toSet()
            allCatalogScholarships.filter { it.id !in trackedIds }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Scholarship to Tracker") },
            text = {
                if (untrackedCatalog.isEmpty()) {
                    Text("All catalog scholarships are already in your tracker!")
                } else {
                    LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(untrackedCatalog) { s ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scope.launch {
                                            DataStoreManager.trackScholarship(
                                                context,
                                                TrackedScholarship(
                                                    scholarshipId = s.id,
                                                    scholarshipName = s.name,
                                                    provider = s.category,
                                                    amount = s.amount ?: "Varies",
                                                    deadline = s.deadline ?: "Rolling",
                                                    stage = ApplicationStage.DISCOVERED,
                                                    updatedAt = System.currentTimeMillis()
                                                )
                                            )
                                            showAddDialog = false
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = s.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${s.category} • ${s.amount ?: "Varies"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Edit Notes Dialog
    editingNotesItem?.let { item ->
        AlertDialog(
            onDismissRequest = { editingNotesItem = null },
            title = { Text("Application Notes & Application ID") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = item.scholarshipName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = tempNoteText,
                        onValueChange = { tempNoteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Application ID #NSP2026-884, interview date Nov 12...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            DataStoreManager.saveScholarshipNotes(context, item.scholarshipId, tempNoteText)
                            editingNotesItem = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNotesItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrackedScholarshipCard(
    item: TrackedScholarship,
    onStageChange: (ApplicationStage) -> Unit,
    onToggleDocument: (String) -> Unit,
    onEditNotes: () -> Unit,
    onUntrack: () -> Unit
) {
    var expandedChecklist by remember { mutableStateOf(false) }

    val completedDocsCount = STANDARD_REQUIRED_DOCUMENTS.count { item.completedDocuments.contains(it) }
    val docFraction = completedDocsCount.toFloat() / STANDARD_REQUIRED_DOCUMENTS.size.toFloat()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Stage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.scholarshipName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.provider}  •  Deadline: ${item.deadline}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (item.stage) {
                        ApplicationStage.DISCOVERED -> MaterialTheme.colorScheme.surfaceVariant
                        ApplicationStage.DOCS_READY -> MaterialTheme.colorScheme.secondaryContainer
                        ApplicationStage.APPLIED -> MaterialTheme.colorScheme.primaryContainer
                        ApplicationStage.AWARDED -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (item.stage) {
                            ApplicationStage.DISCOVERED -> "🎯 Discovered"
                            ApplicationStage.DOCS_READY -> "📋 Docs Ready"
                            ApplicationStage.APPLIED -> "🚀 Applied"
                            ApplicationStage.AWARDED -> "🏆 Awarded"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (item.stage) {
                            ApplicationStage.DISCOVERED -> MaterialTheme.colorScheme.onSurfaceVariant
                            ApplicationStage.DOCS_READY -> MaterialTheme.colorScheme.onSecondaryContainer
                            ApplicationStage.APPLIED -> MaterialTheme.colorScheme.onPrimaryContainer
                            ApplicationStage.AWARDED -> Color(0xFF2E7D32)
                        }
                    )
                }
            }

            // Amount pill
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Grant Value: ${item.amount}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            // Stage Progression Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Application Stage:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StageSelectChip(
                        label = "Discovered",
                        isSelected = item.stage == ApplicationStage.DISCOVERED,
                        modifier = Modifier.weight(1f),
                        onClick = { onStageChange(ApplicationStage.DISCOVERED) }
                    )
                    StageSelectChip(
                        label = "Docs Ready",
                        isSelected = item.stage == ApplicationStage.DOCS_READY,
                        modifier = Modifier.weight(1f),
                        onClick = { onStageChange(ApplicationStage.DOCS_READY) }
                    )
                    StageSelectChip(
                        label = "Applied",
                        isSelected = item.stage == ApplicationStage.APPLIED,
                        modifier = Modifier.weight(1f),
                        onClick = { onStageChange(ApplicationStage.APPLIED) }
                    )
                    StageSelectChip(
                        label = "Awarded",
                        isSelected = item.stage == ApplicationStage.AWARDED,
                        modifier = Modifier.weight(1f),
                        onClick = { onStageChange(ApplicationStage.AWARDED) }
                    )
                }
            }

            HorizontalDivider()

            // Document Readiness Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedChecklist = !expandedChecklist },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Document Checklist ($completedDocsCount/${STANDARD_REQUIRED_DOCUMENTS.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (expandedChecklist) "Hide" else "Manage",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LinearProgressIndicator(
                    progress = { docFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (docFraction == 1f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                AnimatedVisibility(visible = expandedChecklist) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        STANDARD_REQUIRED_DOCUMENTS.forEach { doc ->
                            val isChecked = item.completedDocuments.contains(doc)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleDocument(doc) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChecked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = doc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Notes and actions row
            if (item.notes.isNotBlank()) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${item.notes}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditNotes,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (item.notes.isBlank()) "Add Note" else "Edit Note", style = MaterialTheme.typography.labelSmall)
                }

                IconButton(
                    onClick = onUntrack,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove from tracker",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StageSelectChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
