package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.Layout
import kotlin.math.max
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextDecoration

// ---------------------------- Data (CAREER_INFO + skill descriptions + colors) ----------------------------
private val CAREER_INFO: Map<String, CareerInfo> = mapOf(
    "Engineering" to CareerInfo(
        title = "Engineering",
        short = "Solve real-world technical problems using math & technology.",
        skills = listOf("Mathematics", "Programming", "Problem solving", "CAD/Systems thinking", "Electronics", "Embedded systems", "Data structures"),
        salary = "₹3.5 LPA – ₹25 LPA (depending on role & company)",
        roadmap = listOf("Complete engineering diploma/degree (B.Tech)", "Build projects (apps / hardware)", "Internships / open-source", "Specialize (AI, Embedded, Cloud)"),
        colleges = listOf("IITs / NITs", "State Engineering Colleges", "Private tech universities")
    ),
    "Design" to CareerInfo(
        title = "Design (UI/UX / Product / Visual)",
        short = "Create user-friendly and beautiful product experiences.",
        skills = listOf("Visual design", "User research", "Prototyping (Figma)", "Interaction design", "Wireframing", "Accessibility"),
        salary = "₹2.4 LPA – ₹20 LPA (product & startup roles vary)",
        roadmap = listOf("Learn design fundamentals", "Master Figma/Sketch/Adobe", "Build portfolio & case studies", "Apply for internships / freelance"),
        colleges = listOf("Design Institutes (NID, CEPT)", "Private design schools", "University design specializations")
    ),
    "Management" to CareerInfo(
        title = "Management / Business",
        short = "Lead teams, plan strategy and run operations or products.",
        skills = listOf("Communication", "Leadership", "Business strategy", "Data-driven decision making", "Stakeholder management"),
        salary = "₹4 LPA – ₹40 LPA (product / consulting / leadership roles)",
        roadmap = listOf("Bachelor's degree (any field)", "Gain work experience", "MBA / Management programs", "Move into product/operations/consulting"),
        colleges = listOf("IIMs / Top B-schools", "State universities (MBA)", "Good private MBA colleges")
    ),
    "Science" to CareerInfo(
        title = "Science & Research",
        short = "Investigate fundamental questions; build knowledge and innovations.",
        skills = listOf("Laboratory skills", "Critical thinking", "Statistics", "Scientific writing", "Data analysis"),
        salary = "₹3 LPA – ₹30 LPA (academia → industry research varies)",
        roadmap = listOf("B.Sc / B.Tech in relevant field", "M.Sc / PhD for research roles", "Publish papers, join labs", "Industry R&D or academia"),
        colleges = listOf("Central universities / IISc / Research institutes", "Top science colleges")
    ),
    "Entrepreneurship" to CareerInfo(
        title = "Entrepreneur / Startup",
        short = "Create and grow your own business or product.",
        skills = listOf("Product thinking", "Sales", "Fundraising", "Resilience", "Market research"),
        salary = "Varies greatly — equity + growth potential",
        roadmap = listOf("Build an MVP", "Validate with users", "Find co-founder / team", "Raise funds / grow revenue"),
        colleges = listOf("Incubators, startup accelerators, engineering colleges")
    )
)

private data class CareerInfo(
    val title: String,
    val short: String,
    val skills: List<String>,
    val salary: String,
    val roadmap: List<String>,
    val colleges: List<String>
)

// Short skill descriptions (used by chip tap)
private val SKILL_DESCRIPTIONS: Map<String, String> = mapOf(
    "Mathematics" to "Core math skills used in engineering, algorithms and modeling.",
    "Programming" to "Ability to write code, build applications and automate tasks.",
    "Problem solving" to "Breaking down problems logically and finding solutions.",
    "CAD/Systems thinking" to "Designing systems and using CAD tools for physical products.",
    "Electronics" to "Understanding circuits, microcontrollers and electronic systems.",
    "Embedded systems" to "Building software for hardware devices.",
    "Data structures" to "Efficient ways to store and access data.",
    "Visual design" to "Principles of layout, typography and color.",
    "User research" to "Understanding user needs through interviews and testing.",
    "Prototyping (Figma)" to "Rapidly building UI mockups to validate ideas.",
    "Interaction design" to "Designing how users interact with products.",
    "Wireframing" to "Low-fidelity sketches of UI layout and flows.",
    "Accessibility" to "Designing products usable by people with disabilities.",
    "Communication" to "Clear written and verbal communication skills.",
    "Leadership" to "Guiding teams, making decisions and motivating people.",
    "Business strategy" to "Planning how a company creates value and competes.",
    "Data-driven decision making" to "Using metrics and analysis to guide actions.",
    "Laboratory skills" to "Hands-on experimental techniques in science.",
    "Statistics" to "Understanding and analyzing data using statistical methods.",
    "Scientific writing" to "Communicating research clearly and accurately.",
    "Product thinking" to "Focusing on customer value and product-market fit.",
    "Sales" to "Selling products and building customer relationships.",
    "Fundraising" to "Raising capital from investors and fund sources.",
    "Resilience" to "Persisting through setbacks and learning from failure.",
    "Market research" to "Studying users and competitors to find opportunity."
)

// Per-career chip colors
private val CAREER_COLORS: Map<String, Color> = mapOf(
    "Engineering" to Color(0xFF6EC6FF),
    "Design" to Color(0xFFFFD86E),
    "Management" to Color(0xFFB39DDB),
    "Science" to Color(0xFF90CAF9),
    "Entrepreneurship" to Color(0xFFFFAB91)
)

// ---------------------------- Main Screen ----------------------------
@Composable
fun CareerRecommendationScreen(
    viewModel: QuizViewModel,
    onNavigate: (String) -> Unit
) {
    val scores by viewModel.scores.collectAsState(initial = emptyMap())

    val sorted = if (scores.isEmpty()) CAREER_INFO.keys.associateWith { 0 } else scores

    val topThree = sorted.entries.sortedByDescending { it.value }.take(3).map { it.key }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Career Recommendations", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Based on your quiz results — top careers you might enjoy", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Bar chart
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Score distribution", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    BarChart(scores = sorted, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // helper if no scores
        item {
            if (scores.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("No results yet", style = MaterialTheme.typography.titleMedium)
                        Text("Take the quiz to get personalized career recommendations.", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onNavigate("quiz") }) { Text("Take Quiz") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Top careers list
        items(topThree) { key ->
            val info = CAREER_INFO[key] ?: CareerInfo(key, "A good match", emptyList(), "Varies", emptyList(), emptyList())
            CareerCard(info = info, score = sorted[key] ?: 0, careerKey = key, onExploreColleges = { onNavigate("colleges") })
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Next steps", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text("• Try the colleges list and check scholarships.")
            Text("• Build a small project or portfolio related to the top career.")
            Text("• Consider short online courses (Coursera, Udemy, NPTEL) to improve key skills.")
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onNavigate("colleges") }, modifier = Modifier.weight(1f)) { Text("Explore Colleges") }
                OutlinedButton(onClick = { onNavigate("scholarships") }, modifier = Modifier.weight(1f)) { Text("Check Scholarships") }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ---------------------------- Career Card (with limited chips & popup) ----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CareerCard(info: CareerInfo, score: Int, careerKey: String, onExploreColleges: () -> Unit) {
    // expansion state for chips (show more)
    var expanded by rememberSaveable { mutableStateOf(false) }

    // dialog state for showing skill description
    var showSkillDialog by rememberSaveable { mutableStateOf(false) }
    var dialogSkillTitle by rememberSaveable { mutableStateOf("") }
    var dialogSkillDesc by rememberSaveable { mutableStateOf("") }

    val chipColor = CAREER_COLORS[careerKey] ?: Color(0xFFD0D0D0)

    Card(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = info.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Score: $score", fontSize = 12.sp)
                }
                Button(onClick = onExploreColleges) { Text("Explore Colleges") }
            }

            Text(info.short, fontSize = 14.sp)

            Text("Key skills", style = MaterialTheme.typography.titleSmall)

            // show first N skills + "+N more" chip
            val displayCount = 6
            val skills = info.skills
            val showList = if (expanded) skills else skills.take(displayCount)

            FlowLayout(horizontalGap = 8.dp, verticalGap = 8.dp, modifier = Modifier.fillMaxWidth()) {
                showList.forEach { skill ->
                    // each chip is clickable to show description
                    SkillChip(text = skill, bgColor = chipColor, onClick = {
                        dialogSkillTitle = skill
                        dialogSkillDesc = SKILL_DESCRIPTIONS[skill] ?: "Short info about $skill."
                        showSkillDialog = true
                    })
                }

                if (!expanded && skills.size > displayCount) {
                    val remaining = skills.size - displayCount
                    // FIXED: use named parameter so lambda binds to onClick (not isPrimary)
                    MoreChip(text = "+$remaining more", onClick = { expanded = true })
                }
                // show "Show less" chip when expanded
                if (expanded && skills.size > displayCount) {
                    MoreChip(text = "Show less", onClick = { expanded = false }, isPrimary = false)
                }
            }

            // show dialog when chip tapped
            if (showSkillDialog) {
                AlertDialog(
                    onDismissRequest = { showSkillDialog = false },
                    title = { Text(dialogSkillTitle) },
                    text = { Text(dialogSkillDesc) },
                    confirmButton = {
                        TextButton(onClick = { showSkillDialog = false }) { Text("OK") }
                    }
                )
            }

            Text("Salary (typical)", style = MaterialTheme.typography.titleSmall)
            Text(info.salary, fontSize = 14.sp)

            Text("Roadmap", style = MaterialTheme.typography.titleSmall)
            info.roadmap.forEachIndexed { idx, step ->
                Text("${idx + 1}. $step", fontSize = 14.sp)
            }

            if (info.colleges.isNotEmpty()) {
                Text("Suggested colleges", style = MaterialTheme.typography.titleSmall)
                info.colleges.forEach { c -> Text("• $c", fontSize = 14.sp) }
            }
        }
    }
}

// ---------------------------- Skill chips & helper chips ----------------------------
@Composable
private fun SkillChip(text: String, bgColor: Color, onClick: () -> Unit) {
    // darker text if bg is light (simple contrast)
    val luminance = bgColor.red * 0.2126f + bgColor.green * 0.7152f + bgColor.blue * 0.0722f
    val contentColor = if (luminance > 0.6f) Color.Black else Color.White

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(14.dp),
        color = bgColor.copy(alpha = 0.18f),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = bgColor, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 12.sp, color = contentColor)
        }
    }
}

@Composable
private fun MoreChip(text: String, onClick: () -> Unit, isPrimary: Boolean = true) {
    val bg = if (isPrimary) MaterialTheme.colorScheme.primary else Color.LightGray
    val fg = if (isPrimary) MaterialTheme.colorScheme.onPrimary else Color.Black

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(14.dp),
        color = bg,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), fontSize = 12.sp, color = fg, textDecoration = if (!isPrimary) TextDecoration.Underline else TextDecoration.None)
    }
}

// ---------------------------- BarChart (same as earlier) ----------------------------
@Composable
private fun BarChart(
    scores: Map<String, Int>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 140.dp,
    barWidth: Dp = 44.dp,
    barSpacing: Dp = 12.dp
) {
    if (scores.isEmpty()) {
        Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No score data to show", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val entries = scores.entries.sortedByDescending { it.value }.toList()
    val maxValue = (entries.maxOf { it.value }).coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        entries.forEach { (key, value) ->
            Spacer(Modifier.size(barSpacing / 2))

            val fraction = value.toFloat() / maxValue.toFloat()
            val animated by animateFloatAsState(targetValue = fraction, animationSpec = tween(durationMillis = 600))

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(text = value.toString(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

                Box(modifier = Modifier.size(width = barWidth, height = chartHeight), contentAlignment = Alignment.BottomCenter) {
                    Box(modifier = Modifier.size(width = barWidth, height = chartHeight).background(Color(0xFFEDEDED), shape = RoundedCornerShape(8.dp)))
                    Box(
                        modifier = Modifier
                            .size(width = barWidth, height = (chartHeight * animated))
                            .background(
                                Brush.verticalGradient(colors = listOf(Color(0xFF5E9CFF), Color(0xFF1E88E5)), tileMode = TileMode.Clamp),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )
                }

                Text(text = key, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(barWidth + 8.dp).padding(top = 6.dp))
            }

            Spacer(Modifier.size(barSpacing / 2))
        }
    }
}

// ---------------------------- FlowLayout implementation ----------------------------
@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val horizontalGapPx = horizontalGap.roundToPx()
        val verticalGapPx = verticalGap.roundToPx()

        val rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        val rowHeights = mutableListOf<Int>()

        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        var currentRowHeight = 0

        val maxWidth = constraints.maxWidth

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0))
            val itemWidth = placeable.width
            val itemHeight = placeable.height

            if (currentRow.isEmpty()) {
                currentRow.add(placeable)
                currentRowWidth = itemWidth
                currentRowHeight = itemHeight
            } else if (currentRowWidth + horizontalGapPx + itemWidth <= maxWidth) {
                currentRow.add(placeable)
                currentRowWidth += horizontalGapPx + itemWidth
                currentRowHeight = max(currentRowHeight, itemHeight)
            } else {
                rows.add(currentRow)
                rowHeights.add(currentRowHeight)
                currentRow = mutableListOf(placeable)
                currentRowWidth = itemWidth
                currentRowHeight = itemHeight
            }
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowHeights.add(currentRowHeight)
        }

        val totalHeight = rowHeights.sum() + verticalGapPx * max(0, rowHeights.size - 1)

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            for (rowIndex in rows.indices) {
                val row = rows[rowIndex]
                var x = 0
                val rowHeight = rowHeights[rowIndex]
                for (placeable in row) {
                    placeable.placeRelative(x, y + (rowHeight - placeable.height) / 2)
                    x += placeable.width + horizontalGapPx
                }
                y += rowHeight + verticalGapPx
            }
        }
    }
}
