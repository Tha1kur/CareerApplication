package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.adityayadav.careerguidancecommunity.data.model.College
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

object CollegeRepository {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // cached list (in-memory)
    @Volatile
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

                val list: List<College> = jsonSerializer.decodeFromString(json)
                cached = list
                list
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * Replace the in-memory cache with the provided list.
     */
    fun replaceCached(list: List<College>) {
        cached = list
    }

    /**
     * Get a college by id, lazily loading if cache is uninitialized.
     */
    suspend fun getById(context: Context, id: String): College? {
        val existing = cached?.firstOrNull { it.id == id }
        if (existing != null) return existing

        val list = loadAll(context)
        return list.firstOrNull { it.id == id }
    }

    /**
     * In-memory cache query (non-suspending).
     */
    fun getCachedById(id: String): College? = cached?.firstOrNull { it.id == id }

    /**
     * Get multiple colleges by collection of IDs.
     */
    suspend fun getByIds(context: Context, ids: Collection<String>): List<College> {
        val all = loadAll(context)
        return all.filter { it.id in ids }
    }
}
