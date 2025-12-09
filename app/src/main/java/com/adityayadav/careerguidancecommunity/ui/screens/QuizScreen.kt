package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

data class QuizOption(val value: String, val label: String)
data class QuizQuestion(val id: String, val question: String, val options: List<QuizOption>)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onComplete: () -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }
    val partialKey = "quiz_partial_answers"

    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var index by remember { mutableStateOf(0) }

    // map questionId -> selectedOptionValue (in-memory)
    val selected = remember { mutableStateMapOf<String, String>() }

    // Load questions from assets and restore partial answers
    LaunchedEffect(Unit) {
        isLoading = true
        loadError = null
        try {
            questions = loadQuestionsFromAssets(context, "aptitudequiz.json", gson)
        } catch (e: Exception) {
            loadError = e.message ?: "Failed to load quiz"
        } finally {
            isLoading = false
        }

        // Restore partial answers (one-shot)
        try {
            val json = DataStoreManager.getStringFlow(context, partialKey, "{}").first()
            val type = object : TypeToken<Map<String, String>>() {}.type
            val restored: Map<String, String> = try {
                gson.fromJson(json, type) ?: emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }
            selected.clear()
            selected.putAll(restored)
        } catch (_: Exception) {
        }
    }

    fun persistPartial() {
        scope.launch {
            try {
                val json = gson.toJson(selected.toMap())
                DataStoreManager.saveString(context, partialKey, json)
            } catch (_: Exception) {
            }
        }
    }

    fun onSelect(questionId: String, optionValue: String) {
        selected[questionId] = optionValue
        persistPartial()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aptitude Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                loadError != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $loadError")
                    }
                }

                questions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No questions found in assets/aptitudequiz.json")
                    }
                }

                else -> {
                    val safeIndex = index.coerceIn(0, questions.lastIndex)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuizProgressHeader(
                            index = safeIndex,
                            total = questions.size,
                            answeredCount = selected.size
                        )

                        AnimatedContent(
                            targetState = safeIndex,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    slideInHorizontally(
                                        animationSpec = tween(300)
                                    ) { width -> width } + fadeIn() with
                                            slideOutHorizontally(
                                                animationSpec = tween(300)
                                            ) { width -> -width } + fadeOut()
                                } else {
                                    slideInHorizontally(
                                        animationSpec = tween(300)
                                    ) { width -> -width } + fadeIn() with
                                            slideOutHorizontally(
                                                animationSpec = tween(300)
                                            ) { width -> width } + fadeOut()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { target ->
                            val q = questions[target]
                            QuizQuestionCard(
                                question = q,
                                selectedValue = selected[q.id],
                                onOptionSelected = { value ->
                                    onSelect(q.id, value)
                                }
                            )
                        }

                        QuizFooter(
                            canGoBack = index > 0,
                            isLastQuestion = index == questions.lastIndex,
                            onBackToHome = onBackToHome,
                            onPrevious = { if (index > 0) index-- },
                            onNext = { if (index < questions.lastIndex) index++ },
                            onSubmit = {
                                selected["favoriteSubject"]?.let { viewModel.updateFavoriteSubject(it) }
                                selected["handsOnWork"]?.let { viewModel.updateHandsOnWork(it) }
                                selected["jobPreference"]?.let { viewModel.updateJobPreference(it) }
                                selected["workEnvironment"]?.let { viewModel.updateWorkEnvironment(it) }
                                selected["leadership"]?.let { viewModel.updateLeadership(it) }
                                selected["problemSolving"]?.let { viewModel.updateProblemSolving(it) }
                                selected["creativity"]?.let { viewModel.updateCreativity(it) }
                                selected["communication"]?.let { viewModel.updateCommunication(it) }

                                viewModel.submitQuiz()

                                scope.launch {
                                    DataStoreManager.saveString(context, partialKey, "{}")
                                }

                                onComplete()
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ---------- UI pieces ---------- */

@Composable
private fun QuizProgressHeader(
    index: Int,
    total: Int,
    answeredCount: Int
) {
    val progress = if (total == 0) 0f else (index + 1).toFloat() / total.toFloat()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (total > 0) "Question ${index + 1} of $total" else "Quiz",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "$answeredCount answered",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun QuizQuestionCard(
    question: QuizQuestion,
    selectedValue: String?,
    onOptionSelected: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { index, option ->
                    val isSelected = selectedValue == option.value
                    OptionCard(
                        index = index,
                        label = option.label,
                        selected = isSelected,
                        onClick = { onOptionSelected(option.value) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    index: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val bg = if (selected) scheme.primaryContainer else scheme.surfaceVariant
    val borderColor = if (selected) scheme.primary else scheme.outline.copy(alpha = 0.4f)
    val labelColor = if (selected) scheme.onPrimaryContainer else scheme.onSurface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (selected) 4.dp else 1.dp,
        border = BorderStroke(1.dp, borderColor),
        color = bg
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = scheme.onPrimaryContainer.copy(
                            alpha = if (selected) 0.18f else 0.08f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ('A' + index).toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = labelColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = scheme.primary
                )
            }
        }
    }
}

@Composable
private fun QuizFooter(
    canGoBack: Boolean,
    isLastQuestion: Boolean,
    onBackToHome: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            onClick = onBackToHome,
            modifier = Modifier.weight(1f)
        ) {
            Text("Exit")
        }
        OutlinedButton(
            onClick = onPrevious,
            enabled = canGoBack,
            modifier = Modifier.weight(1f)
        ) {
            Text("Previous")
        }
        Button(
            onClick = if (isLastQuestion) onSubmit else onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (isLastQuestion) "Submit" else "Next")
        }
    }
}

/**
 * Read questions from assets using Gson.
 * Expects JSON like in aptitudequiz.json  [oai_citation:0‡aptitudequiz.json](file-service://file-FCWAnwGvakkKZN3j9yPhzC)
 */
@Throws(IOException::class)
private fun loadQuestionsFromAssets(
    context: Context,
    assetName: String,
    gson: Gson
): List<QuizQuestion> {
    val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<QuizQuestion>>() {}.type
    return gson.fromJson(json, type) ?: emptyList()
}