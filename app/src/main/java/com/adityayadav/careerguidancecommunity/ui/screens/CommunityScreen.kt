package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.adityayadav.careerguidancecommunity.data.CommunityRepository
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.CommunityAnswer
import com.adityayadav.careerguidancecommunity.data.model.CommunityQuestion
import kotlinx.coroutines.launch
import java.util.UUID

private val COMMUNITY_CATEGORIES = listOf(
    "All",
    "Technology",
    "Design",
    "Management",
    "Science",
    "Admissions & Funding"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val questions by CommunityRepository.getQuestionsFlow(context).collectAsState(initial = emptyList())
    val upvotedQuestionIds by DataStoreManager.upvotedQuestionsFlow(context).collectAsState(initial = emptySet())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var showAskDialog by remember { mutableStateOf(false) }
    var answeringQuestion by remember { mutableStateOf<CommunityQuestion?>(null) }

    val filteredQuestions = remember(questions, searchQuery, selectedCategory) {
        questions.filter { q ->
            val matchesCategory = (selectedCategory == "All" || q.category.equals(selectedCategory, ignoreCase = true))
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                q.title.contains(searchQuery, ignoreCase = true) ||
                    q.content.contains(searchQuery, ignoreCase = true) ||
                    q.tags.any { it.contains(searchQuery, ignoreCase = true) }
            }
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Forum,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "Student & Mentor Community")
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
                onClick = { showAskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Ask Question")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search discussions, JEE, design, IIM...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Categories Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(COMMUNITY_CATEGORIES) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            if (filteredQuestions.isEmpty()) {
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
                        Text(
                            text = "No questions found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Be the first to start a conversation in this topic!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { showAskDialog = true }) {
                            Text("Ask a Question")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
                ) {
                    items(filteredQuestions, key = { it.id }) { q ->
                        val isUpvoted = upvotedQuestionIds.contains(q.id)
                        val totalUpvotes = q.upvotes + (if (isUpvoted) 1 else 0)

                        CommunityQuestionCard(
                            question = q,
                            isUpvoted = isUpvoted,
                            upvoteCount = totalUpvotes,
                            onToggleUpvote = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    CommunityRepository.toggleUpvote(context, q.id)
                                }
                            },
                            onAddAnswer = {
                                answeringQuestion = q
                            }
                        )
                    }
                }
            }
        }
    }

    // Ask Question Dialog
    if (showAskDialog) {
        AskQuestionDialog(
            onDismiss = { showAskDialog = false },
            onSubmit = { title, content, cat, name, role ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch {
                    val newQ = CommunityQuestion(
                        id = "user_q_${UUID.randomUUID().toString().take(8)}",
                        title = title,
                        author = if (name.isNotBlank()) name else "Anonymous Scholar",
                        authorRole = if (role.isNotBlank()) role else "Student",
                        category = cat,
                        content = content,
                        timestamp = System.currentTimeMillis(),
                        upvotes = 1,
                        tags = listOf(cat, "StudentAMA")
                    )
                    CommunityRepository.postQuestion(context, newQ)
                    DataStoreManager.addPoints(context, 35)
                    showAskDialog = false
                }
            }
        )
    }

    // Add Answer Dialog
    answeringQuestion?.let { q ->
        AddAnswerDialog(
            questionTitle = q.title,
            onDismiss = { answeringQuestion = null },
            onSubmit = { answerText, mentorName, credential, isMentor ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch {
                    val newAns = CommunityAnswer(
                        id = "ans_${UUID.randomUUID().toString().take(8)}",
                        author = if (mentorName.isNotBlank()) mentorName else "Community Contributor",
                        authorBadge = if (credential.isNotBlank()) credential else "Peer Mentor",
                        isVerifiedMentor = isMentor,
                        content = answerText,
                        timestamp = System.currentTimeMillis(),
                        upvotes = 0
                    )
                    CommunityRepository.addAnswer(context, q.id, newAns)
                    DataStoreManager.addPoints(context, 50)
                    DataStoreManager.addBadge(context, "Community Helper")
                    answeringQuestion = null
                }
            }
        )
    }
}

@Composable
private fun CommunityQuestionCard(
    question: CommunityQuestion,
    isUpvoted: Boolean,
    upvoteCount: Int,
    onToggleUpvote: () -> Unit,
    onAddAnswer: () -> Unit
) {
    var expandedAnswers by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Author info & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = question.author.take(1).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = question.author,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (question.authorRole.isNotBlank()) {
                            Text(
                                text = question.authorRole,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = question.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Question Headline & Content
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = question.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Tags row
            if (question.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    question.tags.take(4).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "#$tag",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Card Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onToggleUpvote),
                        color = if (isUpvoted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = "Upvote",
                                tint = if (isUpvoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$upvoteCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isUpvoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { expandedAnswers = !expandedAnswers }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChatBubbleOutline,
                            contentDescription = "Answers",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${question.answers.size} ${if (question.answers.size == 1) "Answer" else "Answers"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(onClick = onAddAnswer) {
                    Text(text = "Answer Thread", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Expanded answers thread
            AnimatedVisibility(visible = expandedAnswers) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (question.answers.isEmpty()) {
                        Text(
                            text = "No answers yet. Share your experience or advice!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        question.answers.forEach { ans ->
                            AnswerItemRow(ans)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerItemRow(answer: CommunityAnswer) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (answer.isVerifiedMentor) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = answer.author,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                if (answer.isVerifiedMentor) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Verified Mentor",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (answer.authorBadge.isNotBlank()) {
                    Text(
                        text = "• ${answer.authorBadge}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AskQuestionDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technology") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ask the Community") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Question Headline") },
                    placeholder = { Text("e.g. Which course is best for AI?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Details & Context") },
                    placeholder = { Text("Describe your background, dilemmas, and goals...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.labelMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(COMMUNITY_CATEGORIES.filter { it != "All" }) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Your Stage (e.g. Class 12, 1st Year)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title, content, category, name, role)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Post Question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddAnswerDialog(
    questionTitle: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Boolean) -> Unit
) {
    var answerText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var credential by remember { mutableStateOf("") }
    var isMentor by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contribute Advice") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Re: $questionTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    label = { Text("Your Advice / Experience") },
                    placeholder = { Text("Share practical steps, insights, or resources...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = credential,
                    onValueChange = { credential = it },
                    label = { Text("Background / College / Role") },
                    placeholder = { Text("e.g. NIT Senior, B.Com Graduate") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isMentor,
                        onClick = { isMentor = !isMentor },
                        label = { Text(if (isMentor) "✓ Verified Mentor Advice" else "Mark as Verified Mentor") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (answerText.isNotBlank()) {
                        onSubmit(answerText, name, credential, isMentor)
                    }
                },
                enabled = answerText.isNotBlank()
            ) {
                Text("Post Advice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
