package com.adityayadav.careerguidancecommunity.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Scholarship(
    val id: String,
    val name: String,
    val category: String,
    val amount: String? = null,
    val deadline: String? = null,
    val eligibility: String? = null,
    val applyLink: String? = null,
    val description: String? = null,
    val tags: List<String>? = null
)
