package com.adityayadav.careerguidancecommunity.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    quizViewModel: QuizViewModel,
) {
    val context: Context = LocalContext.current
    val themeMode by DataStoreManager
        .getStringFlow(context, "theme_mode", "light")
        .collectAsState(initial = "light")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        // hook for any future preload
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero / summary card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Theme segmented controls
            val options = listOf(
                "light" to "Light",
                "dark" to "Dark",
                "system" to "System"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { (value, label) ->
                    val selected = themeMode.equals(value, ignoreCase = true)
                    val container =
                        if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    val contentColor =
                        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    val border =
                        if (selected) null
                        else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

                    Button(
                        onClick = {
                            scope.launch {
                                DataStoreManager.saveString(context, "theme_mode", value)
                                snackbarHostState.showSnackbar("Theme: $label")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = container,
                            contentColor = contentColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = border,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(label)
                    }
                }
            }

            Text(
                text = "Current: ${
                    themeMode.replaceFirstChar { it.uppercaseChar() }
                }",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Data & app section
            Text(
                text = "Data & app",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = {
                    scope.launch {
                        quizViewModel.resetProgress()
                        snackbarHostState.showSnackbar("Gamification progress reset")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset gamification (XP & badges)")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        quizViewModel.resetAllProgress()
                        snackbarHostState.showSnackbar("All quiz data reset")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset quiz, scores & XP")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        DataStoreManager.clearProfile(context)
                        snackbarHostState.showSnackbar("Profile cleared")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear profile")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Note: Theme switching here stores your preference in DataStore. " +
                        "To fully apply it app‑wide, read this value in your Theme setup and choose light/dark color schemes accordingly.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}