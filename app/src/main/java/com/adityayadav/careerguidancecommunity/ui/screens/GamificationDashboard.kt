package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import com.adityayadav.careerguidancecommunity.data.model.calculateLevel
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Gamification dashboard:
 * - Gradient hero with level ring & XP bar
 * - Shows saved career path from DataStore
 * - Stats row (points, badges, milestones)
 * - Badge grid
 * - Recent activity
 * - Level‑up dialog + theme‑aware confetti
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamificationDashboard(
    viewModel: QuizViewModel,
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val progress by viewModel.progress.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Saved career path
    val savedCareerKey by DataStoreManager
        .getStringFlow(ctx, "saved_career_key", "")
        .collectAsState(initial = "")
    val savedCareerTitle = remember(savedCareerKey) {
        mapCareerKeyToTitle(savedCareerKey)
    }

    // Level calculation
    val levelInfo = remember(progress.points) { calculateLevel(progress.points) }

    // Confetti controller
    val confettiController = remember { ConfettiController() }

    // Track previous badge count & previous level to detect level-ups
    var prevBadgeCount by remember { mutableStateOf(progress.badges.size) }
    var prevLevel by remember { mutableStateOf(levelInfo.level) }

    // Dialog state for "level up" modal
    var showLevelUp by remember { mutableStateOf(false) }

    // Detect badge increases for confetti burst
    LaunchedEffect(progress.badges.size) {
        if (progress.badges.size > prevBadgeCount) {
            confettiController.launchBurst()
        }
        prevBadgeCount = progress.badges.size
    }

    // Detect level up (level number increased)
    LaunchedEffect(levelInfo.level) {
        if (levelInfo.level > prevLevel) {
            confettiController.launchBurst()
            showLevelUp = true
            scope.launch {
                delay(2500)
                showLevelUp = false
            }
        }
        prevLevel = levelInfo.level
    }

    val milestonesCompleted = listOf(
        progress.completedQuiz,
        progress.viewedResults,
        progress.exploredColleges,
        progress.checkedScholarships
    ).count { it }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar with back
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Hero card with gradient + level ring
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val onHero = MaterialTheme.colorScheme.onPrimary

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Your journey",
                                style = MaterialTheme.typography.labelMedium,
                                color = onHero.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Level ${levelInfo.level}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = onHero
                            )
                            Text(
                                text = "${progress.points} XP total",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onHero.copy(alpha = 0.9f)
                            )

                            if (savedCareerTitle.isNotBlank()) {
                                Text(
                                    text = "Saved path: $savedCareerTitle",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onHero.copy(alpha = 0.95f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { levelInfo.progressFraction.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                trackColor = onHero.copy(alpha = 0.25f),
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${levelInfo.currentXp}/${levelInfo.nextLevelXp} XP to next level",
                                style = MaterialTheme.typography.labelMedium,
                                color = onHero.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        LevelRing(
                            progressFraction = levelInfo.progressFraction,
                            level = levelInfo.level,
                            ringSize = 96.dp
                        )
                    }
                }
            }

            // Quick stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    label = "Points",
                    value = progress.points.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Badges",
                    value = progress.badges.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Milestones",
                    value = milestonesCompleted.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Badges header + quick actions
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Badges", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onNavigate("colleges") }) {
                        Text("Explore colleges")
                    }
                    TextButton(onClick = { onNavigate("scholarships") }) {
                        Text("Find scholarships")
                    }
                }
            }

            // Badge grid
            BadgeGrid(badges = progress.badges)

            // Activity card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Recent activity", style = MaterialTheme.typography.titleSmall)
                    ActivityRow(progress)
                }
            }
        }

        // Confetti overlay
        ConfettiCanvas(controller = confettiController, modifier = Modifier.fillMaxSize())

        // Level-up modal (Dialog) with animated entrance/exit
        if (showLevelUp) {
            LevelUpDialog(
                level = levelInfo.level,
                points = progress.points,
                onDismiss = { showLevelUp = false }
            )
        }
    }
}

/* ---------- Level ring ---------- */

@Composable
private fun LevelRing(
    progressFraction: Float,
    level: Int,
    ringSize: Dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "levelRing"
    )

    val scheme = MaterialTheme.colorScheme
    val bgColor = scheme.surfaceVariant.copy(alpha = 0.8f)
    val gradient = Brush.sweepGradient(
        colors = listOf(
            scheme.primary,
            scheme.secondary,
            scheme.primary
        )
    )

    Box(
        modifier = Modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minSide = min(size.width, size.height)
            val strokeWidth = minSide * 0.18f
            val radius = (minSide - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - radius * 2f) / 2f,
                (size.height - radius * 2f) / 2f
            )

            // background ring
            drawArc(
                color = bgColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = topLeft,
                size = Size(radius * 2f, radius * 2f)
            )

            // progress ring with gradient
            drawArc(
                brush = gradient,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = Size(radius * 2f, radius * 2f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Lv $level",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${(progressFraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------- Stats & badges ---------- */

@Composable
private fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun BadgeGrid(badges: List<String>) {
    val display = if (badges.isEmpty()) listOf("No badges yet") else badges

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp, max = 220.dp),
        contentPadding = PaddingValues(6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(display) { name ->
            BadgeCard(name = name)
        }
    }
}

@Composable
private fun BadgeCard(name: String) {
    val (bg, icon, tint) = badgeVisual(name)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = bg,
                tonalElevation = 0.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = tint
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2
            )
            if (name != "No badges yet") {
                Text(
                    text = "Earned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun badgeVisual(
    name: String
): Triple<Color, androidx.compose.ui.graphics.vector.ImageVector, Color> {
    return when {
        name.equals("No badges yet", ignoreCase = true) ->
            Triple(
                Color(0xFFE0E0E0),
                Icons.Filled.Work,
                Color(0xFF757575)
            )

        name.contains("Quiz", ignoreCase = true) ->
            Triple(
                Color(0xFFE3F2FD),
                Icons.Filled.EmojiEvents,
                Color(0xFF1E88E5)
            )

        name.contains("College", ignoreCase = true) ->
            Triple(
                Color(0xFFF3E5F5),
                Icons.Filled.School,
                Color(0xFF6A1B9A)
            )

        name.contains("Scholar", ignoreCase = true) ->
            Triple(
                Color(0xFFFFF3E0),
                Icons.Filled.EmojiEvents,
                Color(0xFFFF8F00)
            )

        else ->
            Triple(
                Color(0xFFF2F2F2),
                Icons.Filled.Work,
                Color(0xFF616161)
            )
    }
}

/* ---------- Recent activity ---------- */

@Composable
private fun ActivityRow(progress: UserProgress) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (progress.completedQuiz) {
            Text("• Completed quiz", style = MaterialTheme.typography.bodySmall)
        }
        if (progress.viewedResults) {
            Text("• Viewed results", style = MaterialTheme.typography.bodySmall)
        }
        if (progress.exploredColleges) {
            Text("• Explored colleges", style = MaterialTheme.typography.bodySmall)
        }
        if (progress.checkedScholarships) {
            Text("• Checked scholarships", style = MaterialTheme.typography.bodySmall)
        }
        progress.badges.take(3).forEach {
            Text("• Earned badge: $it", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/* ---------- Level‑up dialog ---------- */

@Composable
private fun LevelUpDialog(
    level: Int,
    points: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(animationSpec = tween(450)) + fadeIn(animationSpec = tween(450)),
            exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Level Up! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "You reached Level $level",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "$points XP total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Nice!",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Confetti (theme‑aware) ---------- */

private data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val r: Float,
    val color: Color,
    var alpha: Float
)

private class ConfettiController {
    private val _bursts = mutableStateListOf<Long>()
    val bursts: List<Long> get() = _bursts

    fun launchBurst() {
        _bursts.add(System.currentTimeMillis())
    }

    fun clearOld(cutoff: Long) {
        _bursts.removeAll { it < cutoff }
    }
}

@Composable
private fun ConfettiCanvas(
    controller: ConfettiController,
    modifier: Modifier = Modifier
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }

    // Palette driven by the current theme so dark/light both look good
    val scheme = MaterialTheme.colorScheme
    val palette = remember(scheme) {
        listOf(
            scheme.primary,
            scheme.secondary,
            scheme.primaryContainer,
            scheme.secondaryContainer,
            scheme.onPrimary
        )
    }

    LaunchedEffect(controller.bursts.toList()) {
        val burstTime = controller.bursts.maxOrNull() ?: return@LaunchedEffect

        val newParticles = List(70) {
            val angle = Random.nextDouble(0.0, 2.0 * PI).toFloat()
            val speed = Random.nextDouble(180.0, 520.0).toFloat()
            Particle(
                x = 0f,
                y = 0f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                r = Random.nextDouble(3.0, 7.0).toFloat(),
                color = palette.random(),
                alpha = 1f
            )
        }

        val start = burstTime
        val gravity = 900f
        var lastFrameTime = start.toFloat()

        particles = newParticles

        while (System.currentTimeMillis() - start < 1600L && particles.isNotEmpty()) {
            val now = System.currentTimeMillis().toFloat()
            val dt = (now - lastFrameTime) / 1000f
            lastFrameTime = now

            particles = particles.mapNotNull { p ->
                val newVy = p.vy + gravity * dt
                val newX = p.x + p.vx * dt
                val newY = p.y + newVy * dt
                val newAlpha = p.alpha - dt / 1.2f
                if (newAlpha <= 0f) null
                else p.copy(x = newX, y = newY, vy = newVy, alpha = newAlpha)
            }

            delay(16L)
        }

        particles = emptyList()
        controller.clearOld(System.currentTimeMillis() - 3000L)
    }

    Canvas(modifier = modifier) {
        if (particles.isEmpty()) return@Canvas
        val origin = Offset(size.width / 2f, size.height * 0.08f)
        particles.forEach { p ->
            drawCircle(
                color = p.color.copy(alpha = p.alpha),
                radius = p.r,
                center = Offset(origin.x + p.x, origin.y + p.y)
            )
        }
    }
}

/* ---------- Shared career title mapper ---------- */

private fun mapCareerKeyToTitle(key: String): String = when (key) {
    "Engineering" -> "Engineering & Tech"
    "Design" -> "Design & UX"
    "Management" -> "Business & Management"
    "Science" -> "Science & Research"
    "Entrepreneurship" -> "Entrepreneurship"
    else -> key
}