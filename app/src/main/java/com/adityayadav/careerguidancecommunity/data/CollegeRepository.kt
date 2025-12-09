package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.adityayadav.careerguidancecommunity.data.model.College
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object CollegeRepository {

    // cached list (in-memory). Access from single-threaded coroutine contexts or treat as volatile.
    private var cached: List<College>? = null

    /**
     * Load list of colleges from assets (colleges.json).
     * Uses cache when available.
     */
    suspend fun loadAll(context: Context): List<College> {
        cached?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("colleges.json")
                    .bufferedReader()
                    .use { it.readText() }

                val type = object : TypeToken<List<College>>() {}.type
                val list: List<College> = Gson().fromJson(json, type) ?: emptyList()

                cached = list
                list
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * Replace the in-memory cache with the provided list.
     * This is used when you fetch remote JSON and want to use it instead of local assets.
     */
    fun replaceCached(list: List<College>) {
        cached = list
    }

    /**
     * Get a college by id from the cache. Returns null if cache is empty or not found.
     * This is intentionally non-suspending — call loadAll(context) first to populate cache.
     */
    fun getById(id: String): College? = cached?.firstOrNull { it.id == id }
}
