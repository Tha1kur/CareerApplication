package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScholarshipRepository {

    private var cached: List<Scholarship> = emptyList()

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

    fun replaceCached(list: List<Scholarship>) {
        cached = list
    }

    fun getById(id: String): Scholarship? {
        return cached.firstOrNull { it.id == id }
    }
}
