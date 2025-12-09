package com.adityayadav.careerguidancecommunity.data.model

import kotlinx.serialization.Serializable

@Serializable
data class College(
    val id: String,
    val name: String,
    val city: String? = null,
    val state: String? = null,
    val type: String? = null,
    val rank: Int? = null,
    val intake: String? = null,
    val avgFees: String? = null,
    val description: String? = null,

    // ensure non-null list so UI never crashes
    val courses: List<String> = emptyList(),

    val tags: List<String> = emptyList(),
    val website: String? = null
)