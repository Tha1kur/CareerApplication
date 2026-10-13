package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adityayadav.careerguidancecommunity.data.DataStoreManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartQuiz: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val savedCareerKey by DataStoreManager
        .getStringFlow(context, "saved_career_key", "")
        .collectAsState(initial = "")

    val savedCareerTitle = remember(savedCareerKey) {
        mapCareerKeyToTitle(savedCareerKey)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Career Guidance Community",
                        maxLines = 1
                    )
                },
                actions = {
                    IconButton(onClick = { onNavigate("profile") }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    }
                    IconButton(onClick = { onNavigate("settings") }) {
                        Icon(
                            imageVector = Icons.Filled.Dashboard,
                            contentDescription = "Settings"
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero banner – gradient card that matches the dashboard vibe
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Start your journey",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "Find your best-fit career",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 2
                            )
                            Text(
                                text = "Take a quick quiz, explore colleges & scholarships, and track your progress like a game.",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.96f),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(onClick = onStartQuiz) {
                                    Text("Start quiz")
                                }
                                TextButton(onClick = { onNavigate("dashboard") }) {
                                    Text("View dashboard")
                                }
                            }

                            if (savedCareerTitle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Saved path:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = savedCareerTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        TextButton(onClick = { onNavigate("recommendations") }) {
                                            Text(
                                                text = "View",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Career Mentor Flagship Banner
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Personalized AI Career Mentor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = "Synthesizes your profile, strengths, and quiz assessments into tailored milestone roadmaps, exam targets, and project ideas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = { onNavigate("ai_mentor") }) {
                                Text("Open Roadmap")
                            }
                        }
                    }
                }

                // Quick actions row 1
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Colleges",
                        subtitle = "Explore colleges",
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigate("colleges") }
                    )
                    QuickActionCard(
                        title = "Scholarships",
                        subtitle = "Find funding",
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = { onNavigate("scholarships") }
                    )
                }

                // Quick actions row 2: AI Mentor & Application Tracker
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "AI Mentor",
                        subtitle = "Milestone plan",
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigate("ai_mentor") }
                    )
                    QuickActionCard(
                        title = "Tracker",
                        subtitle = "Grants pipeline",
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        onClick = { onNavigate("application_tracker") }
                    )
                }

                // Quick actions row 3: Community & Compare
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Community",
                        subtitle = "Mentorship AMA",
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = { onNavigate("community") }
                    )
                    QuickActionCard(
                        title = "Compare",
                        subtitle = "Side-by-side view",
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigate("compare") }
                    )
                }

                // Quick actions row 4: Saved Items & Dashboard
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Saved Items",
                        subtitle = "Colleges & Grants",
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        onClick = { onNavigate("saved") }
                    )
                    QuickActionCard(
                        title = "Dashboard",
                        subtitle = "Your progress",
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigate("dashboard") }
                    )
                }

                // Parental tips row
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Parental Tips",
                        subtitle = "Support for parents",
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = { onNavigate("parental") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recommended / Featured area
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Recommended next steps",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    RecommendationCard(
                        title = "Consult AI Career Mentor",
                        description = "Review your personalized milestones, recommended exams, and portfolio project ideas tailored to your strengths.",
                        onPrimary = { onNavigate("ai_mentor") }
                    )

                    RecommendationCard(
                        title = "Join Peer & Mentor Community",
                        description = "Ask questions and read real advice from verified mentors and alumni across IITs, NID, and top colleges.",
                        onPrimary = { onNavigate("community") }
                    )

                    RecommendationCard(
                        title = "Track Scholarship Applications",
                        description = "Organize deadlines, check document readiness, and move opportunities across your application pipeline.",
                        onPrimary = { onNavigate("application_tracker") }
                    )

                    RecommendationCard(
                        title = "Complete the quiz",
                        description = "Answer a few quick questions to get personalized career, college and scholarship suggestions.",
                        onPrimary = onStartQuiz
                    )

                    RecommendationCard(
                        title = "Review your matches",
                        description = "See recommended careers based on your answers and strengths.",
                        onPrimary = { onNavigate("results") }
                    )

                    RecommendationCard(
                        title = "Explore top colleges",
                        description = "Check out colleges that fit your interests and budget, and dive deeper into details.",
                        onPrimary = { onNavigate("colleges") },
                        onSecondary = { onNavigate("recommendations") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Made with ❤️  •  Career Guidance Community",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Quick action card row item.
 */
@Composable
private fun RowScope.QuickActionCard(
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (title) {
                    "Colleges" -> Icons.Filled.School
                    "Scholarships" -> Icons.Filled.Star
                    "AI Mentor" -> Icons.Filled.AutoAwesome
                    "Tracker" -> Icons.Filled.AssignmentTurnedIn
                    "Community" -> Icons.Filled.Forum
                    "Compare" -> Icons.AutoMirrored.Filled.CompareArrows
                    "Saved Items" -> Icons.Filled.Bookmark
                    "Dashboard" -> Icons.Filled.Dashboard
                    "Parental Tips" -> Icons.Filled.Book
                    else -> Icons.Filled.Work
                }
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    title: String,
    description: String,
    onPrimary: () -> Unit,
    onSecondary: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, MaterialTheme.shapes.medium),
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
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onPrimary) {
                    Text("Do it")
                }
                if (onSecondary != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = onSecondary) {
                        Text("Learn more")
                    }
                }
            }
        }
    }
}

/* Shared mapper with dashboard */

private fun mapCareerKeyToTitle(key: String): String = when (key) {
    "Engineering" -> "Engineering & Tech"
    "Design" -> "Design & UX"
    "Management" -> "Business & Management"
    "Science" -> "Science & Research"
    "Entrepreneurship" -> "Entrepreneurship"
    else -> key
}