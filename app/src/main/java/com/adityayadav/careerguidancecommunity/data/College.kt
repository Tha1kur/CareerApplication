package com.adityayadav.careerguidancecommunity.data

import kotlinx.serialization.Serializable

@Serializable
data class College(
    val id: String,
    val name: String,
    val city: String? = null,
    val state: String? = null,
    val type: String? = null,
    val rank: Int? = null,
    val intake: Int? = null,
    val avgFees: String? = null,
    val description: String? = null,
    val courses: List<String> = emptyList()
)
