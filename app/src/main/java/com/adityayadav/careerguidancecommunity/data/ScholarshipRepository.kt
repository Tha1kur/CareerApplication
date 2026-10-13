package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

object ScholarshipRepository {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Volatile
    private var cached: List<Scholarship> = emptyList()

    suspend fun loadAll(context: Context): List<Scholarship> {
        if (cached.isNotEmpty()) return cached

        return withContext(Dispatchers.IO) {
            try {
                val json = context.assets
                    .open("scholarships.json")
                    .bufferedReader()
                    .use { it.readText() }

                val list: List<Scholarship> = jsonSerializer.decodeFromString(json)
                cached = list
                list
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    fun replaceCached(list: List<Scholarship>) {
        cached = list
    }

    suspend fun getById(context: Context, id: String): Scholarship? {
        val existing = cached.firstOrNull { it.id == id }
        if (existing != null) return existing

        val list = loadAll(context)
        return list.firstOrNull { it.id == id }
    }

    fun getCachedById(id: String): Scholarship? = cached.firstOrNull { it.id == id }

    suspend fun getByIds(context: Context, ids: Collection<String>): List<Scholarship> {
        val all = loadAll(context)
        return all.filter { it.id in ids }
    }
}
