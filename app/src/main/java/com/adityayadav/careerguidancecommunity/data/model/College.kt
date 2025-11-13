package com.adityayadav.careerguidancecommunity.data.model

data class College(
    val id: String,
    val name: String,
    val city: String?,
    val state: String?,
    val type: String?,
    val rank: Int?,
    val intake: Int?,
    val avgFees: String?,
    val description: String?,
    val website: String?
)
