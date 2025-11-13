package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onStartQuiz: () -> Unit, onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Welcome to Career Guidance Community")
        Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth()) {
            Text("Start Aptitude Quiz")
        }
        Button(onClick = { onNavigate("colleges") }, modifier = Modifier.fillMaxWidth()) {
            Text("Explore Colleges")
        }
        Button(onClick = { onNavigate("scholarships") }, modifier = Modifier.fillMaxWidth()) {
            Text("Check Scholarships")
        }
        Button(onClick = { onNavigate("dashboard") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gamification Dashboard")
        }
    }
}
