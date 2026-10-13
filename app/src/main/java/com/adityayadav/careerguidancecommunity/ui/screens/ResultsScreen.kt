package com.adityayadav.careerguidancecommunity.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    onNavigate: (String) -> Unit
) {
    val scores by viewModel.scores.collectAsState(initial = emptyMap())
    val answers by viewModel.answers.collectAsState()
    val history by viewModel.quizHistory.collectAsState()

    LaunchedEffect(Unit) {
        try {
            viewModel.markViewedResults()
        } catch (_: Exception) {
        }
    }

    val effectiveScores = scores.ifEmpty { emptyMap() }
    val sorted = effectiveScores.entries.sortedByDescending { it.value }
    val topEntry = sorted.firstOrNull()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (effectiveScores.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No results yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Take the aptitude quiz to see your career matches.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { onNavigate("quiz") }) {
                    Text("Start quiz")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Gradient hero
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quiz results",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )

                        if (topEntry != null) {
                            val (bestKey, bestScore) = topEntry
                            val maxScore = sorted.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
                            val percent = (bestScore * 100f / maxScore).toInt()

                            Text(
                                text = "Top match: $bestKey",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "$percent% match • $bestScore pts",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val summaryChips = buildAnswerSummaryChips(answers)
                        if (summaryChips.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                summaryChips.forEach { chip ->
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        tonalElevation = 1.dp
                                    ) {
                                        Text(
                                            text = chip,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Score distribution
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Career scores",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))

                        val maxScore = sorted.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
                        sorted.forEach { (key, value) ->
                            val fraction = value.toFloat() / maxScore.toFloat()

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "$value pts",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { fraction.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                if (history.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quiz History (${history.size} attempts)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(onClick = { onNavigate("quiz") }) {
                                    Text("Retake Quiz")
                                }
                            }
                            HorizontalDivider()

                            history.take(4).forEachIndexed { idx, attempt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Attempt #${history.size - idx} • ${attempt.topCareer}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = attempt.formattedDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "${attempt.topScore} pts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigate("recommendations") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View detailed recommendations")
                    }
                    OutlinedButton(
                        onClick = { onNavigate("dashboard") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open gamified dashboard")
                    }
                    OutlinedButton(
                        onClick = { onNavigate("colleges") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Explore colleges")
                    }
                }
            }
        }
    }
}

private fun buildAnswerSummaryChips(a: QuizAnswers): List<String> {
    val chips = mutableListOf<String>()

    if (a.favoriteSubject.isNotBlank()) {
        val label = when (a.favoriteSubject.lowercase()) {
            "math" -> "Enjoys Mathematics"
            "science" -> "Science lover"
            "arts" -> "Creative & artsy"
            "commerce" -> "Interested in commerce"
            else -> "Has a favourite subject"
        }
        chips += label
    }

    if (a.handsOnWork.isNotBlank()) {
        val label = when (a.handsOnWork.lowercase()) {
            "yes" -> "Likes hands‑on work"
            "no" -> "Prefers theory"
            "maybe" -> "Open to practical work"
            else -> "Flexible about hands‑on work"
        }
        chips += label
    }

    if (a.jobPreference.isNotBlank()) {
        val label = when (a.jobPreference.lowercase()) {
            "government" -> "Prefers stability"
            "private" -> "Open to corporate growth"
            "business" -> "Entrepreneurial mindset"
            else -> "Has career preferences"
        }
        chips += label
    }

    if (a.workEnvironment.isNotBlank()) {
        val label = when (a.workEnvironment.lowercase()) {
            "team" -> "Team player"
            "independent" -> "Independent worker"
            "mixed" -> "Mix of solo & team work"
            else -> "Has environment preference"
        }
        chips += label
    }

    if (a.leadership.isNotBlank()) {
        val label = when (a.leadership.lowercase()) {
            "very" -> "Comfortable leading"
            "somewhat" -> "Open to leadership"
            "not" -> "Prefers support role"
            else -> "Thinks about leadership"
        }
        chips += label
    }

    if (a.problemSolving.isNotBlank()) {
        val label = when (a.problemSolving.lowercase()) {
            "analytical" -> "Analytical problem‑solver"
            "creative" -> "Creative problem‑solver"
            "people" -> "People‑focused helper"
            else -> "Enjoys solving problems"
        }
        chips += label
    }

    if (a.creativity.isNotBlank()) {
        val label = when (a.creativity.lowercase()) {
            "very" -> "Values creativity"
            "somewhat" -> "Balanced with creativity"
            "not" -> "Practical & structured"
            else -> "Has views on creativity"
        }
        chips += label
    }

    if (a.communication.isNotBlank()) {
        val label = when (a.communication.lowercase()) {
            "public" -> "Likes public speaking"
            "written" -> "Strong written communicator"
            "oneonone" -> "Great in 1‑1 conversations"
            else -> "Has communication preference"
        }
        chips += label
    }

    return chips
}