package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

// ------------------------------
// MAIN SCREEN
// ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalScreen(
    onNavigate: (String) -> Unit,
    quizViewModel: QuizViewModel? = null
) {
    val ctx = LocalContext.current

    val tips = listOf(
        Pair("Start early & listen",
            "Have open conversations about what your child enjoys, their strengths and interests. Avoid forcing one single career path."),
        Pair("Focus on skills, not just marks",
            "Encourage projects, competitions, internships and communication practice. These matter more than marks alone."),
        Pair("Help explore options",
            "Use quizzes, visit colleges, talk to professionals and explore job videos together."),
        Pair("Mental health matters",
            "Reduce pressure. Help them balance study, hobbies, sleep and relaxation."),
        Pair("Financial planning",
            "Help your child understand fees, scholarships, government colleges, and alternative career routes.")
    )

    val resources = listOf(
        Resource("UNICEF: Career Guidance for Parents",
            "https://www.unicef.org/parents/guide-career-choices.pdf"),
        Resource("National Scholarship Portal",
            "https://scholarships.gov.in"),
        Resource("Career Paths Overview (PDF)",
            "https://example.com/parent-career-pathways.pdf")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parental Awareness") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Intro
            item {
                Text(
                    "Helpful tips & resources for parents to support career decisions",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
            }

            // Tips header
            item {
                Text(
                    "Parenting Tips",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
            }

            // Tips list
            items(tips) { (title, body) ->
                ExpandableTipCard(title = title, body = body)
            }

            // Resources header
            item {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Resources & PDFs",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
            }

            // Resource cards
            items(resources) { r ->
                ResourceCard(resource = r, onOpen = { url ->
                    openUrlSafely(ctx, url)

                    // optional gamification
                    quizViewModel?.let { vm ->
                        vm.addPoints(10)
                        vm.addBadge("Parent Engaged")
                        Toast.makeText(ctx, "+10 points awarded", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            // Contact section
            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Need additional support?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        sendEmail(
                            ctx,
                            "care@yourapp.example",
                            "Request: Parent Counseling",
                            "Hello,\nI would like to request a short counseling session.\nThank you."
                        )
                    }) {
                        Text("Email Counselor")
                    }

                    OutlinedButton(onClick = {
                        openUrlSafely(ctx, "https://wa.me/911234567890")
                    }) {
                        Text("WhatsApp Support")
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Back Button
            item {
                Button(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Back to Home") }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ------------------------------
// EXPANDABLE TIP CARD
// ------------------------------
@Composable
private fun ExpandableTipCard(title: String, body: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (expanded) "Hide" else "Read",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            if (expanded) {
                Text(body, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(body, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}

// ------------------------------
// RESOURCE DATA + CARD
// ------------------------------
private data class Resource(val title: String, val url: String)

@Composable
private fun ResourceCard(resource: Resource, onOpen: (String) -> Unit) {
    val ctx = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(14.dp)) {

            Text(resource.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            Text(resource.url, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                Button(onClick = { onOpen(resource.url) }) {
                    Text("Open")
                }

                OutlinedButton(onClick = {
                    copyToClipboard(ctx, resource.url)
                }) {
                    Text("Copy Link")
                }
            }
        }
    }
}

// ------------------------------
// UTILITIES
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

private fun sendEmail(ctx: android.content.Context, to: String, subject: String, body: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(ctx, "No email app found", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(ctx: android.content.Context, text: String) {
    val clipboard =
        ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("link", text))
    Toast.makeText(ctx, "Link copied", Toast.LENGTH_SHORT).show()
}
