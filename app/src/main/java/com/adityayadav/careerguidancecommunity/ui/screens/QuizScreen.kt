package com.adityayadav.careerguidancecommunity.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onComplete: () -> Unit,
    onBackToHome: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = (pagerState.currentPage + 1) / 4f,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Section ${pagerState.currentPage + 1} of 4",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> PageOne(viewModel)
                1 -> PageTwo(viewModel)
                2 -> PageThree(viewModel)
                3 -> PageFour(viewModel)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                Button(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                    Text("Back")
                }
            } else {
                Spacer(Modifier.width(8.dp))
            }

            if (pagerState.currentPage < 3) {
                Button(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }) {
                    Text("Next")
                }
            } else {
                Button(onClick = {
                    viewModel.submitQuiz()
                    onComplete()
                }) {
                    Text("Submit")
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onBackToHome, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back to Home")
        }
    }
}

@Composable
fun PageOne(viewModel: QuizViewModel) {
    // Q1..Q5: MCQ
    val scroll = rememberScrollState()

    // Use rememberSaveable so state survives configuration changes / page recompositions
    var selectedQ1 by rememberSaveable { mutableStateOf("") }
    var selectedQ2 by rememberSaveable { mutableStateOf("") }
    var selectedQ3 by rememberSaveable { mutableStateOf("") }
    var selectedQ4 by rememberSaveable { mutableStateOf("") }
    var selectedQ5 by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Q1
        Text("1. Which subject do you enjoy the most?", fontSize = 18.sp)
        val q1Options = listOf("Mathematics", "Science", "Arts & Creativity", "Social Studies", "Business & Commerce")
        q1Options.forEach { opt ->
            OptionCard(
                text = opt,
                selected = selectedQ1 == opt,
                onClick = {
                    selectedQ1 = opt
                    viewModel.updateFavoriteSubject(opt)
                }
            )
        }

        // Q2
        Text("2. What type of tasks do you enjoy?", fontSize = 18.sp)
        val q2Options = listOf("Solving problems", "Designing creative things", "Managing people", "Doing experiments", "Learning new technologies")
        q2Options.forEach { opt ->
            OptionCard(
                text = opt,
                selected = selectedQ2 == opt,
                onClick = {
                    selectedQ2 = opt
                    viewModel.updateJobPreference(opt)
                }
            )
        }

        // Q3
        Text("3. Which activity sounds exciting to you?", fontSize = 18.sp)
        val q3Options = listOf("Building machines/apps", "Drawing/designing", "Leading a team", "Doing scientific research", "Running a business")
        q3Options.forEach { opt ->
            OptionCard(
                text = opt,
                selected = selectedQ3 == opt,
                onClick = {
                    selectedQ3 = opt
                    viewModel.updateHandsOnWork(opt)
                }
            )
        }

        // Q4
        Text("4. Which career area attracts you most?", fontSize = 18.sp)
        val q4Options = listOf("Engineering", "Design", "Management", "Science", "Entrepreneurship")
        q4Options.forEach { opt ->
            OptionCard(
                text = opt,
                selected = selectedQ4 == opt,
                onClick = {
                    selectedQ4 = opt
                    viewModel.updateWorkEnvironment(opt)
                }
            )
        }

        // Q5
        Text("5. What motivates you more?", fontSize = 18.sp)
        val q5Options = listOf("Innovation", "Creativity", "Leadership", "Research", "Money")
        q5Options.forEach { opt ->
            OptionCard(
                text = opt,
                selected = selectedQ5 == opt,
                onClick = {
                    selectedQ5 = opt
                    viewModel.updateCreativity(opt)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PageTwo(viewModel: QuizViewModel) {
    // Q6..Q10: Ratings 1..5
    val scroll = rememberScrollState()

    var q6 by rememberSaveable { mutableStateOf(0) }
    var q7 by rememberSaveable { mutableStateOf(0) }
    var q8 by rememberSaveable { mutableStateOf(0) }
    var q9 by rememberSaveable { mutableStateOf(0) }
    var q10 by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RatingRow(label = "6. Logical & Analytical Ability (1–5)", selected = q6) {
            q6 = it; viewModel.updateProblemSolving(it.toString())
        }

        RatingRow(label = "7. Creativity Level (1–5)", selected = q7) {
            q7 = it; viewModel.updateCreativity(it.toString())
        }

        RatingRow(label = "8. Communication Skills (1–5)", selected = q8) {
            q8 = it; viewModel.updateCommunication(it.toString())
        }

        RatingRow(label = "9. Leadership Ability (1–5)", selected = q9) {
            q9 = it; viewModel.updateLeadership(it.toString())
        }

        RatingRow(label = "10. Problem Solving (1–5)", selected = q10) {
            q10 = it; viewModel.updateProblemSolving(it.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PageThree(viewModel: QuizViewModel) {
    // Q11..Q15: MCQ + Yes/No
    val scroll = rememberScrollState()

    var sel11 by rememberSaveable { mutableStateOf("") }
    var sel12 by rememberSaveable { mutableStateOf("") }
    var sel13 by rememberSaveable { mutableStateOf("") }
    var sel14 by rememberSaveable { mutableStateOf("") }
    var sel15 by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("11. Do you prefer working with people or machines?", fontSize = 18.sp)
        listOf("People", "Machines", "Both").forEach { opt ->
            OptionCard(opt, sel11 == opt) {
                sel11 = opt; viewModel.updateWorkEnvironment(opt)
            }
        }

        Text("12. Do you like hands-on work?", fontSize = 18.sp)
        listOf("Yes", "No").forEach { opt ->
            OptionCard(opt, sel12 == opt) {
                sel12 = opt; viewModel.updateHandsOnWork(opt)
            }
        }

        Text("13. Do you prefer structured or flexible work environments?", fontSize = 18.sp)
        listOf("Structured", "Flexible").forEach { opt ->
            OptionCard(opt, sel13 == opt) {
                sel13 = opt; viewModel.updateWorkEnvironment(opt)
            }
        }

        Text("14. Do you enjoy research and learning new topics?", fontSize = 18.sp)
        listOf("Yes", "No").forEach { opt ->
            OptionCard(opt, sel14 == opt) {
                sel14 = opt; viewModel.updateProblemSolving(opt)
            }
        }

        Text("15. How comfortable are you with deadlines?", fontSize = 18.sp)
        listOf("Very comfortable", "Somewhat comfortable", "Not comfortable").forEach { opt ->
            OptionCard(opt, sel15 == opt) {
                sel15 = opt; viewModel.updateJobPreference(opt)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PageFour(viewModel: QuizViewModel) {
    // Q16..Q20: Personality + ratings
    val scroll = rememberScrollState()

    var sel16 by rememberSaveable { mutableStateOf("") }
    var q17 by rememberSaveable { mutableStateOf(0) }
    var sel18 by rememberSaveable { mutableStateOf("") }
    var q19 by rememberSaveable { mutableStateOf(0) }
    var q20 by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("16. Are you introverted or extroverted?", fontSize = 18.sp)
        listOf("Introverted", "Extroverted", "Ambivert").forEach { opt ->
            OptionCard(opt, sel16 == opt) {
                sel16 = opt; viewModel.updateCommunication(opt)
            }
        }

        RatingRow(label = "17. How well do you handle stress? (1–5)", selected = q17) {
            q17 = it; viewModel.updateProblemSolving(it.toString())
        }

        Text("18. Do you like teamwork?", fontSize = 18.sp)
        listOf("Yes", "No").forEach { opt ->
            OptionCard(opt, sel18 == opt) {
                sel18 = opt; viewModel.updateLeadership(opt)
            }
        }

        RatingRow(label = "19. Planning & Organizing (1–5)", selected = q19) {
            q19 = it; viewModel.updateJobPreference(it.toString())
        }

        RatingRow(label = "20. Decision Making (1–5)", selected = q20) {
            q20 = it; viewModel.updateLeadership(it.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OptionCard(text: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) Color(0xFF4CAF50) else Color(0xFFEFEFEF)
    val contentColor = if (selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = text, fontSize = 16.sp, color = contentColor)
    }
}

@Composable
fun RatingRow(label: String, selected: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { num ->
                val bg = if (selected == num) Color(0xFF4CAF50) else Color.LightGray
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .clickable { onSelect(num) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(num.toString(), color = Color.Black)
                }
            }
        }
    }
}
