package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.ScholarshipRepository
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.College
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

// Info for each career track (keys must match scores map)
private data class CareerInfo(
    val key: String,
    val title: String,
    val short: String,
    val skills: List<String>,
    val vibeTags: List<String>
)

private val CAREER_INFO: Map<String, CareerInfo> = mapOf(
    "Engineering" to CareerInfo(
        key = "Engineering",
        title = "Engineering & Tech",
        short = "Build and optimize real-world systems using math, code and hardware.",
        skills = listOf("Mathematics", "Programming", "Problem solving", "Physics", "Robotics"),
        vibeTags = listOf("Labs & projects", "Tech innovation", "Product building")
    ),
    "Design" to CareerInfo(
        key = "Design",
        title = "Design & UX",
        short = "Turn ideas into beautiful, usable products and experiences.",
        skills = listOf("Visual design", "UI/UX", "Creativity", "Storytelling"),
        vibeTags = listOf("Figma & sketches", "User research", "Portfolios")
    ),
    "Management" to CareerInfo(
        key = "Management",
        title = "Business & Management",
        short = "Lead teams, manage resources and drive strategy & growth.",
        skills = listOf("Communication", "Leadership", "Strategy", "Finance basics"),
        vibeTags = listOf("Presentations", "Teamwork", "Decision making")
    ),
    "Science" to CareerInfo(
        key = "Science",
        title = "Science & Research",
        short = "Explore how the world works with experiments, data and theory.",
        skills = listOf("Scientific method", "Data analysis", "Curiosity"),
        vibeTags = listOf("Labs", "Research papers", "Deep thinking")
    ),
    "Entrepreneurship" to CareerInfo(
        key = "Entrepreneurship",
        title = "Entrepreneurship",
        short = "Start and grow your own products, startups or services.",
        skills = listOf("Problem spotting", "Business models", "Networking"),
        vibeTags = listOf("Startups", "Pitch decks", "Ownership")
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CareerRecommendationScreen(
    viewModel: QuizViewModel,
    onNavigate: (String) -> Unit
) {
    val scores by viewModel.scores.collectAsState(initial = emptyMap())
    val answers by viewModel.answers.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var colleges by remember { mutableStateOf<List<College>>(emptyList()) }
    var scholarships by remember { mutableStateOf<List<Scholarship>>(emptyList()) }

    // Saved career key from DataStore
    val savedCareerKey by DataStoreManager
        .getStringFlow(context, "saved_career_key", "")
        .collectAsState(initial = "")

    LaunchedEffect(Unit) {
        try {
            colleges = CollegeRepository.loadAll(context)
        } catch (_: Exception) {
            colleges = emptyList()
        }

        try {
            scholarships = ScholarshipRepository.loadAll(context)
        } catch (_: Exception) {
            scholarships = emptyList()
        }
    }

    val effectiveScores: Map<String, Int> =
        if (scores.isEmpty()) CAREER_INFO.keys.associateWith { 0 } else scores

    val sorted = effectiveScores.entries
        .sortedByDescending { it.value }
        .filter { CAREER_INFO.containsKey(it.key) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { sorted.size.coerceAtLeast(1) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Career recommendations") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gradient header
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Great job finishing the quiz!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        val topKey = sorted.firstOrNull()?.key
                        if (topKey != null) {
                            Text(
                                text = "Your top match: $topKey",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Swipe through cards to explore how different paths fit your strengths.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                            )
                        } else {
                            Text(
                                text = "We couldn't compute scores yet. You can still explore paths below.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }

            if (sorted.isEmpty()) {
                Text(
                    text = "No scores found. Try retaking the quiz.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { onNavigate("quiz") }) {
                    Text("Retake quiz")
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val (careerKey, score) = sorted[page]
                    val info = CAREER_INFO[careerKey]
                        ?: CareerInfo(
                            key = careerKey,
                            title = careerKey,
                            short = "A good potential path based on your answers.",
                            skills = emptyList(),
                            vibeTags = emptyList()
                        )

                    val maxScore = sorted.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
                    val percent = (score * 100f / maxScore).toInt().coerceIn(0, 100)

                    val suggestedColleges = collegesForCareer(careerKey, colleges)
                    val suggestedScholarships = scholarshipsForCareer(careerKey, scholarships)

                    val isSaved = savedCareerKey == careerKey

                    CareerSwipeCard(
                        info = info,
                        score = score,
                        percent = percent,
                        answers = answers,
                        colleges = suggestedColleges,
                        scholarships = suggestedScholarships,
                        isSaved = isSaved,
                        onSave = {
                            scope.launch {
                                // Save key so Home + Dashboard can show it
                                DataStoreManager.saveString(
                                    context,
                                    "saved_career_key",
                                    careerKey
                                )
                                // Gamification boost (one-time XP + badge)
                                viewModel.onCareerPathSaved()
                            }
                        },
                        onSeeColleges = { onNavigate("colleges") },
                        onSeeScholarships = { onNavigate("scholarships") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pageCount = sorted.size
                    repeat(pageCount) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (selected) 10.dp else 7.dp)
                                .background(
                                    color = if (selected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }
                Text(
                    text = "Swipe left / right to see other matches.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate("dashboard") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View dashboard")
                    }
                    Button(
                        onClick = { onNavigate("results") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("See score breakdown")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CareerSwipeCard(
    info: CareerInfo,
    score: Int,
    percent: Int,
    answers: QuizAnswers,
    colleges: List<College>,
    scholarships: List<Scholarship>,
    isSaved: Boolean,
    onSave: () -> Unit,
    onSeeColleges: () -> Unit,
    onSeeScholarships: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = info.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = info.short,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MatchPill(percent = percent, score = score)
            }

            HorizontalDivider()

            // Skills
            if (info.skills.isNotEmpty()) {
                Text(
                    text = "Key skills",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    info.skills.forEach { skill ->
                        PillChip(text = skill)
                    }
                }
            }

            // Why it fits
            val reasonChips = reasonChipsForCareer(info.key, answers)
            val vibeChips = info.vibeTags
            val allChips = reasonChips + vibeChips

            if (allChips.isNotEmpty()) {
                Text(
                    text = "This suits you because…",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allChips.forEach { text ->
                        PillChip(
                            text = text,
                            tone = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                }
            }

            // Colleges
            if (colleges.isNotEmpty()) {
                Text(
                    text = "Good colleges to explore",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colleges.take(3).forEach { c ->
                        MiniTagChip(
                            icon = Icons.Filled.School,
                            text = c.name
                        )
                    }
                }
            }

            // Scholarships
            if (scholarships.isNotEmpty()) {
                Text(
                    text = "Scholarships that might fit",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scholarships.take(3).forEach { s ->
                        MiniTagChip(
                            icon = Icons.Filled.Star,
                            text = s.name
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaved
                ) {
                    Text(if (isSaved) "Path saved" else "Save this path")
                }
                OutlinedButton(
                    onClick = onSeeColleges,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Explore colleges")
                }
            }

            OutlinedButton(
                onClick = onSeeScholarships,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text("View scholarships")
            }
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    tone: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Surface(
        shape = CircleShape,
        color = tone,
        tonalElevation = 1.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun MiniTagChip(
    icon: ImageVector,
    text: String
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun MatchPill(
    percent: Int,
    score: Int
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Match (${score} pts)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Reason chips & helpers (same as before) --------------------------------

private fun reasonChipsForCareer(
    key: String,
    a: QuizAnswers
): List<String> {
    val list = mutableListOf<String>()

    when (key) {
        "Engineering" -> {
            if (a.favoriteSubject.equals("math", true) || a.favoriteSubject.equals("science", true)) {
                list += "Strong in STEM subjects"
            }
            if (a.problemSolving.equals("analytical", true)) {
                list += "Enjoys analytical problem solving"
            }
            if (a.handsOnWork.equals("yes", true)) {
                list += "Likes practical, hands‑on work"
            }
        }
        "Design" -> {
            if (a.favoriteSubject.equals("arts", true)) {
                list += "Creative & arts oriented"
            }
            if (a.problemSolving.equals("creative", true)) {
                list += "Enjoys creative challenges"
            }
            if (a.creativity.equals("very", true)) {
                list += "Wants creativity in career"
            }
        }
        "Management", "Entrepreneurship" -> {
            if (a.jobPreference.equals("business", true)) {
                list += "Interested in business / entrepreneurship"
            }
            if (a.leadership.equals("very", true) || a.leadership.equals("somewhat", true)) {
                list += "Comfortable taking leadership roles"
            }
            if (a.communication.equals("public", true)) {
                list += "Enjoys presenting & speaking"
            }
        }
        "Science" -> {
            if (a.favoriteSubject.equals("science", true)) {
                list += "Science is a favourite subject"
            }
            if (a.problemSolving.equals("analytical", true)) {
                list += "Enjoys research & analysis"
            }
        }
    }

    return list.distinct()
}

/**
 * Uses tags / type fields from colleges.json.  [oai_citation:1‡colleges.json](file-service://file-H7XmmBrYdMVkK56Urtggea)
 */
private fun collegesForCareer(
    key: String,
    all: List<College>
): List<College> {
    return all.filter { college ->
        val tags: List<String> = college.tags ?: emptyList()
        val type = college.type

        when (key) {
            "Engineering" ->
                (type?.contains("Engineering", true) == true) ||
                        tags.any {
                            it.contains("Engineering", true) ||
                                    it.contains("Computer", true) ||
                                    it.contains("AI", true)
                        }

            "Design" ->
                tags.any { it.contains("Design", true) || it.contains("UI/UX", true) }

            "Management", "Entrepreneurship" ->
                (type?.contains("Management", true) == true) ||
                        tags.any {
                            it.contains("Management", true) ||
                                    it.contains("Business", true)
                        }

            "Science" ->
                tags.any { it.contains("Science", true) }

            else -> false
        }
    }.sortedBy { college ->
        college.rank ?: Int.MAX_VALUE
    }.take(4)
}

/**
 * Uses tags from scholarships.json.  [oai_citation:2‡scholarships.json](file-service://file-Rj5TSnBc7wA3vMxtF7A2j1)
 */
private fun scholarshipsForCareer(
    key: String,
    all: List<Scholarship>
): List<Scholarship> {
    return all.filter { s ->
        val tags = s.tags ?: emptyList()
        when (key) {
            "Engineering" ->
                tags.any {
                    it.equals("STEM", true) ||
                            it.equals("Tech", true) ||
                            it.equals("Science", true) ||
                            it.equals("Merit", true)
                }

            "Design" ->
                tags.any {
                    it.equals("Merit", true) ||
                            it.equals("Private", true)
                }

            "Management", "Entrepreneurship" ->
                tags.any {
                    it.equals("Need-based", true) ||
                            it.equals("Merit", true) ||
                            it.contains("Business", true)
                }

            "Science" ->
                tags.any {
                    it.equals("Science", true) ||
                            it.equals("Merit", true)
                }

            else -> false
        }
    }.take(4)
}