package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.ScholarshipRepository
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.URL
import javax.net.ssl.HttpsURLConnection

data class ScholarshipsUiState(
    val items: List<Scholarship> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = ""
)

class ScholarshipViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx = application.applicationContext

    private val _searchQuery = MutableStateFlow("")
    private val _categoryFlow = MutableStateFlow("All")
    private val _remoteUrl = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(ScholarshipsUiState(isLoading = true))
    val uiState: StateFlow<ScholarshipsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadAndFilter() }

        // Live filtering on change
        viewModelScope.launch {
            _searchQuery
                .debounce(350)
                .combine(_categoryFlow) { s, c -> s to c }
                .collect { (s, c) -> applyFilter(s, c) }
        }
    }

    fun setRemoteJsonUrl(url: String?) {
        _remoteUrl.value = url
    }

    fun onSearchChanged(text: String) {
        _searchQuery.value = text
        _uiState.update { it.copy(searchQuery = text) }

        viewModelScope.launch {
            DataStoreManager.saveString(ctx, "scholarships_search", text)
        }
    }

    fun onCategorySelected(cat: String) {
        _categoryFlow.value = cat
        _uiState.update { it.copy(selectedCategory = cat) }

        viewModelScope.launch {
            DataStoreManager.saveString(ctx, "scholarships_filter", cat)
        }
    }

    fun loadAndFilter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val remote = _remoteUrl.value
                if (!remote.isNullOrBlank()) {
                    val remoteList = fetchRemote(remote)
                    if (remoteList.isNotEmpty()) {
                        ScholarshipRepository.replaceCached(remoteList)
                    }
                }

                applyFilter(_searchQuery.value, _categoryFlow.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchRemote(urlString: String): List<Scholarship> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.connect()

                if (conn.responseCode != 200) return@withContext emptyList()

                val json = conn.inputStream.bufferedReader().use { it.readText() }
                Json { ignoreUnknownKeys = true }.decodeFromString(json)
            } catch (e: Exception) {
                emptyList()
            }
        }

    private fun applyFilter(search: String, category: String) {
        viewModelScope.launch(Dispatchers.Default) {

            val all = ScholarshipRepository.loadAll(ctx)
            val q = search.lowercase()

            val filtered = all.filter { s ->
                val matchCategory = category == "All" ||
                        s.category.equals(category, ignoreCase = true)

                val matchSearch = q.isBlank() ||
                        s.name.lowercase().contains(q) ||
                        (s.eligibility?.lowercase()?.contains(q) ?: false)

                matchCategory && matchSearch
            }

            _uiState.update { it.copy(items = filtered, error = null) }
        }
    }
}
