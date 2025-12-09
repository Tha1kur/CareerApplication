package com.adityayadav.careerguidancecommunity.data.model
import kotlinx.serialization.Serializable

@Serializable
data class QuizOption(
    val value: String,
    val label: String
)

@Serializable
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<QuizOption>
)