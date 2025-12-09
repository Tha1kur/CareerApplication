package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import com.adityayadav.careerguidancecommunity.data.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

object QuizRepository {
    private var cached: List<QuizQuestion>? = null

    suspend fun loadAll(context: Context): List<QuizQuestion> {
        cached?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("aptitudequiz.json").bufferedReader().use { it.readText() }
                val list = Json { ignoreUnknownKeys = true }.decodeFromString<List<QuizQuestion>>(json)
                cached = list
                list
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}