package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers

@Composable
fun ResultsScreen(viewModel: QuizViewModel, onNavigate: (String) -> Unit) {
    // provide an initial empty map to collect safely
    val scoresState by viewModel.scores.collectAsState(initial = emptyMap())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Results")
        if (scoresState.isEmpty()) {
            Text("No results yet. Complete the quiz first.")
            Button(onClick = { onNavigate("quiz") }) { Text("Take Quiz") }
            return@Column
        }

        // Sort scores descending and show all
        val sorted = scoresState.entries.sortedByDescending { it.value }
        sorted.forEach { (stream, score) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stream)
                    Text("$score pts")
                }
            }
        }

        Button(onClick = { onNavigate("colleges") }, modifier = Modifier.fillMaxWidth()) {
            Text("See Colleges")
        }
        Button(onClick = { onNavigate("dashboard") }, modifier = Modifier.fillMaxWidth()) {
            Text("Open Dashboard")
        }
    }
}
