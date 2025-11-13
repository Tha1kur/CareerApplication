package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object CollegeRepository {
    private var cached: List<College>? = null

    suspend fun loadAll(context: Context): List<College> {
        cached?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("colleges.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<College>>() {}.type
                val list: List<College> = Gson().fromJson(json, type)
                cached = list
                list
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    fun replaceCached(list: List<College>) {
        cached = list
    }

    // get by id
    fun getById(id: String): College? = cached?.firstOrNull { it.id == id }
}
