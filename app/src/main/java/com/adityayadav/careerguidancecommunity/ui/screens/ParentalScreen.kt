package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalScreen(
    onNavigate: (String) -> Unit,
    quizViewModel: QuizViewModel? = null
) {
    val ctx = LocalContext.current

    val tips = listOf(
        "Start early & listen" to
                "Have open conversations about what your child enjoys, their strengths and interests. Avoid forcing one single career path.",
        "Focus on skills, not just marks" to
                "Encourage projects, competitions, internships and communication practice. These matter more than marks alone.",
        "Help explore options" to
                "Use quizzes, visit colleges, talk to professionals and explore job videos together.",
        "Mental health matters" to
                "Reduce pressure. Help them balance study, hobbies, sleep and relaxation.",
        "Financial planning" to
                "Start planning early for college fees, scholarships and loans so money stress reduces."
    )

    val resources = listOf(
        Resource(
            title = "NCERT Career Guidance PDF",
            url = "https://ncert.nic.in"
        ),
        Resource(
            title = "National Scholarship Portal",
            url = "https://scholarships.gov.in"
        ),
        Resource(
            title = "MyGov – Career & Skill Initiatives",
            url = "https://www.mygov.in"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("For Parents") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Support your child's career journey",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Use these tips & resources to guide them without pressure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // Parenting tips header
            item {
                Text(
                    text = "Parenting tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Tips list
            items(tips) { (title, body) ->
                ExpandableTipCard(title = title, body = body)
            }

            // Resources header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Resources & PDFs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Resource cards
            items(resources) { res ->
                ResourceCard(
                    resource = res,
                    onOpen = { url -> openUrlSafely(ctx, url) }
                )
            }

            // CTA + back
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can take the quiz with your child and then review the results together.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigate("quiz") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take quiz with your child")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ------------------------------
// Expandable tip card
// ------------------------------
@Composable
private fun ExpandableTipCard(title: String, body: String) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { setExpanded(!expanded) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (expanded) "Hide" else "Read",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (expanded) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

// ------------------------------
// Resource data + card
// ------------------------------
private data class Resource(val title: String, val url: String)

@Composable
private fun ResourceCard(
    resource: Resource,
    onOpen: (String) -> Unit
) {
    val ctx = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                text = resource.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpen(resource.url) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open")
                }
                OutlinedButton(
                    onClick = { copyToClipboard(ctx, resource.url) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy link")
                }
            }
        }
    }
}

// ------------------------------
// Utilities
// ------------------------------
private fun openUrlSafely(ctx: android.content.Context, url: String) {
    try {
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (e: Exception) {
        Toast.makeText(ctx, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(ctx: android.content.Context, text: String) {
    val clipboard =
        ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboard.setPrimaryClip(
        android.content.ClipData.newPlainText(
            "link",
            text
        )
    )
    Toast.makeText(ctx, "Link copied", Toast.LENGTH_SHORT).show()
}