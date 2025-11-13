package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GamificationDashboard(
    viewModel: QuizViewModel,
    onNavigate: (String) -> Unit
) {
    val progressState by viewModel.progress.collectAsState()

    // Level calculations
    val levelInfo = remember(progressState.points) { calculateLevel(progressState.points) }

    // Confetti controller
    val confettiController = remember { ConfettiController() }

    // Track new badge burst
    var prevBadgeCount by remember { mutableIntStateOf(progressState.badges.size) }
    LaunchedEffect(progressState.badges.size) {
        if (progressState.badges.size > prevBadgeCount) {
            confettiController.launchBurst()
        }
        prevBadgeCount = progressState.badges.size
    }

    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Your Achievements",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Points: ${progressState.points} • Level ${levelInfo.level}", style = MaterialTheme.typography.bodyMedium)
            }

            // Level ring
            LevelRing(progressFraction = levelInfo.progressFraction, level = levelInfo.level, size = 96.dp)
        }

        // Badges
        Text("Badges", style = MaterialTheme.typography.titleMedium)
        BadgeRow(badges = progressState.badges)

        // XP card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Progress to next level", style = MaterialTheme.typography.titleSmall)
                LinearProgressIndicator(
                    progress = { levelInfo.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    trackColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("XP: ${levelInfo.currentXp}", style = MaterialTheme.typography.bodySmall)
                    Text("Next: ${levelInfo.nextLevelXp} XP", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Activity timeline
        Text("Recent activity", style = MaterialTheme.typography.titleMedium)
        ActivityTimeline(progressState)

        // Quick actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onNavigate("colleges") }, modifier = Modifier.weight(1f)) { Text("Explore Colleges") }
            OutlinedButton(onClick = { onNavigate("scholarships") }, modifier = Modifier.weight(1f)) { Text("Scholarships") }
        }
    }

    // Confetti overlay
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        ConfettiCanvas(controller = confettiController)
    }
}

// ---------------- helpers & UI components ----------------

private data class LevelInfo(val level: Int, val progressFraction: Float, val nextLevelXp: Int, val currentXp: Int)

private fun calculateLevel(points: Int): LevelInfo {
    val xpPerLevel = 150
    val level = (points / xpPerLevel) + 1
    val currentXp = points % xpPerLevel
    val progressFraction = currentXp.toFloat() / xpPerLevel.toFloat()
    return LevelInfo(level, progressFraction.coerceIn(0f, 1f), xpPerLevel, currentXp)
}

@Composable
private fun LevelRing(progressFraction: Float, level: Int, size: Dp) {
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, animationSpec = tween(600), label = "LevelProgress")

    val bgColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = this.size.width * 0.12f
            val radius = (min(this.size.width, this.size.height) - strokeWidth) / 2f
            val topLeft = Offset((this.size.width - radius * 2) / 2f, (this.size.height - radius * 2) / 2f)

            // background ring
            drawArc(
                color = bgColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            // progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Lv $level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("${(progressFraction * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BadgeRow(badges: List<String>) {
    val display = badges.ifEmpty { listOf("No badges yet") }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(display) { b ->
            BadgeCard(title = b)
        }
    }
}

@Composable
private fun BadgeCard(title: String) {
    val (bgColor, icon, tint) = badgeVisual(title)
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(shape = CircleShape, tonalElevation = 2.dp, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(tint.copy(alpha = 0.12f))) {
                    Icon(imageVector = icon, contentDescription = title, tint = tint)
                }
            }
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}

@Composable
private fun badgeVisual(name: String): Triple<Color, ImageVector, Color> {
    return when {
        name.contains("Quiz", ignoreCase = true) -> Triple(Color(0xFFE9F5FF), Icons.Default.Star, Color(0xFF1E88E5))
        name.contains("College", ignoreCase = true) -> Triple(Color(0xFFF6F6FF), Icons.Default.Star, Color(0xFF6A1B9A))
        name.contains("Scholar", ignoreCase = true) || name.contains("Scholarship", ignoreCase = true) -> Triple(Color(0xFFFFF3E0), Icons.Default.Star, Color(0xFFFF8F00))
        name.contains("Parent", ignoreCase = true) -> Triple(Color(0xFFE8F5E9), Icons.Default.Star, Color(0xFF2E7D32))
        else -> Triple(Color(0xFFF2F2F2), Icons.Default.Star, Color(0xFF616161))
    }
}

@Composable
private fun ActivityTimeline(progress: UserProgress) {
    val items = remember(progress) {
        mutableListOf<String>().apply {
            if (progress.completedQuiz) add("Completed quiz")
            if (progress.viewedResults) add("Viewed results")
            if (progress.exploredColleges) add("Explored colleges")
            if (progress.checkedScholarships) add("Checked scholarships")
            addAll(progress.badges.map { "Earned badge: $it" })
        }
    }

    if (items.isEmpty()) {
        Text("No recent activity. Take the quiz to get started.", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = primaryColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(item, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// --------------------- Confetti ---------------------
private data class Particle(val x: Float, val y: Float, val vx: Float, val vy: Float, val r: Float, var alpha: Float, val color: Color)

private class ConfettiController {
    private val _bursts = mutableStateListOf<Long>()
    val bursts: List<Long> get() = _bursts

    fun launchBurst() {
        _bursts.add(System.currentTimeMillis())
    }

    fun clearOld(cutoffMs: Long) {
        _bursts.removeAll { it < cutoffMs }
    }
}

@Composable
private fun ConfettiCanvas(controller: ConfettiController) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    val particleColors = listOf(Color(0xFFfce18a), Color(0xFFff726d), Color(0xFFb48def), Color(0xFFf4306d))

    LaunchedEffect(controller.bursts.toList()) {
        val latestBurstTime = controller.bursts.maxOrNull() ?: return@LaunchedEffect
        val now = System.currentTimeMillis()

        val newParticles = List(50) {
            val angle = Random.nextDouble(PI * 1.6, PI * 2.4).toFloat()
            val speed = Random.nextDouble(400.0, 900.0).toFloat()
            Particle(
                x = 0f, y = 0f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                r = Random.nextDouble(4.0, 8.0).toFloat(),
                alpha = 1f,
                color = particleColors.random()
            )
        }
        particles = particles + newParticles

        val animationDuration = 2000L
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() < startTime + animationDuration) {
            val dt = 16f / 1000f
            particles = particles.mapNotNull { p ->
                val gravity = 1200f
                val newVy = p.vy + gravity * dt
                val newX = p.x + p.vx * dt
                val newY = p.y + newVy * dt
                val newAlpha = p.alpha - (dt / 1.8f)

                if (newAlpha <= 0f) null else p.copy(x = newX, y = newY, vy = newVy, alpha = newAlpha)
            }
            delay(16)
        }
        particles = emptyList()
        controller.clearOld(now - 3000)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val origin = Offset(size.width / 2f, size.height * 0.1f)
        particles.forEach { p ->
            drawCircle(
                color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                radius = p.r,
                center = Offset(origin.x + p.x, origin.y + p.y)
            )
        }
    }
}
