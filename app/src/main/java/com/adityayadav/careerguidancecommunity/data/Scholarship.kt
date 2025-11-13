package com.adityayadav.careerguidancecommunity.data

import kotlinx.serialization.Serializable

@Serializable
data class Scholarship(
    val id: String,
    val name: String,
    val category: String,
    val amount: String? = null,
    val deadline: String? = null,
    val eligibility: String? = null,
    val applyLink: String? = null
)
