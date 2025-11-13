package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScholarshipRepository {
    private var cached = emptyList<Scholarship>()

    fun loadAll(context: Context): List<Scholarship> {
        if (cached.isNotEmpty()) return cached

        val json = context.assets
            .open("scholarships.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Scholarship>>() {}.type
        cached = Gson().fromJson(json, type)
        return cached
    }

    fun filter(category: String): List<Scholarship> =
        cached.filter { it.category.equals(category, ignoreCase = true) }
    fun replaceCached(list: List<Scholarship>) {
        cached = list
    }

    fun getById(id: String): Scholarship? =
        cached.firstOrNull { it.id == id }
}
