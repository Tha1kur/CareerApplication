package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.College
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.URL
import javax.net.ssl.HttpsURLConnection

data class CollegesUiState(
    val items: List<College> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String = "All",
    val searchQuery: String = ""
)

@OptIn(FlowPreview::class)
class CollegesViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx = application.applicationContext
    private val json = Json { ignoreUnknownKeys = true }

    // DataStore Keys
    private val FILTER_KEY = "colleges_filter"
    private val SEARCH_KEY = "colleges_search"

    // Search & filter state flows
    private val _searchQueryFlow = MutableStateFlow("")
    private val _filterFlow = MutableStateFlow("All")
    private val _remoteUrl = MutableStateFlow<String?>(null)

    // Bookmarked colleges
    private val _savedCollegeIds = MutableStateFlow<Set<String>>(emptySet())
    val savedCollegeIds: StateFlow<Set<String>> = _savedCollegeIds.asStateFlow()

    // Compare selections (up to 3 colleges)
    private val _compareSelection = MutableStateFlow<Set<String>>(emptySet())
    val compareSelection: StateFlow<Set<String>> = _compareSelection.asStateFlow()

    // Final UI state
    private val _uiState = MutableStateFlow(CollegesUiState(isLoading = true))
    val uiState: StateFlow<CollegesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadAndFilter()
        }

        viewModelScope.launch {
            DataStoreManager.savedCollegesFlow(ctx).collect { set ->
                _savedCollegeIds.value = set
            }
        }

        // Refilter when search or filter changes
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300)
                .combine(_filterFlow) { search, filter -> search to filter }
                .collect { (search, filter) ->
                    applyLocalFilter(search, filter)
                }
        }
    }

    fun toggleBookmark(collegeId: String) {
        viewModelScope.launch {
            DataStoreManager.toggleSaveCollege(ctx, collegeId)
        }
    }

    fun toggleCompareSelection(collegeId: String) {
        _compareSelection.update { current ->
            if (current.contains(collegeId)) {
                current - collegeId
            } else {
                if (current.size < 3) current + collegeId else current
            }
        }
    }

    fun clearCompareSelection() {
        _compareSelection.value = emptySet()
    }

    fun onSearchChanged(text: String) {
        _searchQueryFlow.value = text
        _uiState.update { it.copy(searchQuery = text) }

        // Persist search
        viewModelScope.launch {
            DataStoreManager.saveString(ctx, SEARCH_KEY, text)
        }
    }

    fun onFilterSelected(filter: String) {
        _filterFlow.value = filter
        _uiState.update { it.copy(selectedFilter = filter) }

        // Persist filter
        viewModelScope.launch {
            DataStoreManager.saveString(ctx, FILTER_KEY, filter)
        }
    }

    fun setRemoteJsonUrl(url: String?) {
        _remoteUrl.value = url
    }

    fun loadAndFilter() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Try remote fetch
                _remoteUrl.value?.let { remoteUrl ->
                    if (remoteUrl.isNotBlank()) {
                        val remoteList = fetchRemote(remoteUrl)
                        if (remoteList.isNotEmpty()) {
                            CollegeRepository.replaceCached(remoteList)
                        }
                    }
                }

                // Load local JSON
                CollegeRepository.loadAll(ctx)

                // Apply filter & search
                applyLocalFilter(
                    search = _searchQueryFlow.value,
                    filter = _filterFlow.value
                )

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Load failed") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchRemote(urlString: String): List<College> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val conn = url.openConnection() as? HttpsURLConnection ?: return@withContext emptyList()
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.connect()

                if (conn.responseCode != 200) return@withContext emptyList()

                val raw = conn.inputStream.bufferedReader().readText()
                json.decodeFromString<List<College>>(raw)
            } catch (e: Exception) {
                emptyList()
            }
        }

    private fun applyLocalFilter(search: String, filter: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val all = CollegeRepository.loadAll(ctx)

                val q = search.lowercase()

                val filtered = all.filter { c ->

                    val matchesFilter = when (filter) {
                        "All" -> true
                        else -> c.type?.equals(filter, ignoreCase=true) ?: false
                    }

                    val matchesSearch =
                        q.isBlank() ||
                                c.name.lowercase().contains(q) ||
                                (c.city?.lowercase()?.contains(q) ?: false) ||
                                (c.state?.lowercase()?.contains(q) ?: false) ||
                                (c.tags?.any { it.lowercase().contains(q) } ?: false)

                    matchesFilter && matchesSearch
                }

                _uiState.update { it.copy(items = filtered, error = null) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
