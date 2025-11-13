package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement

@Composable
fun DashboardScreen(viewModel: QuizViewModel, onNavigate: (String) -> Unit) {
    // initial value ensures collectAsState compiles without lifecycle helpers
    val progress by viewModel.progress.collectAsState(initial = com.adityayadav.careerguidancecommunity.data.model.UserProgress())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Gamification Dashboard")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Points: ${progress.points}")
                Text("Completed Quiz: ${if (progress.completedQuiz) "Yes" else "No"}")
                Text("Viewed Results: ${if (progress.viewedResults) "Yes" else "No"}")
            }
        }

        Text("Badges")
        if (progress.badges.isEmpty()) {
            Text("No badges yet. Complete actions to earn badges.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(progress.badges) { b ->
                    Card(modifier = Modifier.padding(4.dp)) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Text(b)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate("home") }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}
