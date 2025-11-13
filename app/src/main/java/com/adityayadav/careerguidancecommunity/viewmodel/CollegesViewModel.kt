package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.CollegeRepository
import com.adityayadav.careerguidancecommunity.data.College
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
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

class CollegesViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext

    // persistent keys already in DataStoreManager, we'll use "colleges_filter" and "colleges_search"
    private val FILTER_KEY = "colleges_filter"
    private val SEARCH_KEY = "colleges_search"

    // internal flows
    private val _searchQueryFlow = MutableStateFlow("")
    private val _filterFlow = MutableStateFlow("All")
    private val _remoteUrl = MutableStateFlow<String?>(null) // set this if you want to fetch remote JSON

    // ui state
    private val _uiState = MutableStateFlow(CollegesUiState(isLoading = true))
    val uiState: StateFlow<CollegesUiState> = _uiState.asStateFlow()

    init {
        // restore saved filter + search from DataStore (if present)
        viewModelScope.launch {
            // attempt to read saved values (DataStoreManager.profileFlow style)
            // We'll use DataStoreManager.profileFlow only if it stores generic keys; here we directly read preferences via context.dataStore
            // But to avoid adding new DataStore apis, we'll reuse DataStoreManager with new helper functions.
            // If DataStoreManager in your project doesn't expose generic read, you can adapt this - for now we'll try to read via DataStoreManager.profileFlow as fallback.
            // Simple approach: load persisted filter/search if you previously saved via DataStoreManager (we'll save via DataStoreManager below).
            // Try to load saved via DataStoreManager.profileFlow (it returns a Profile) — if you need separate keys, you can implement them in DataStoreManager.
            // For now, initialize from DataStoreManager.profileFlow if present:
            try {
                val profile = DataStoreManager.profileFlow(ctx).first()
                // if you saved these keys inside profile.goal or name, adapt accordingly.
                // We'll not overwrite, default remains "All".
            } catch (_: Exception) { /* ignore */ }

            // Kick off initial load
            loadAndFilter()
        }

        // combine search (debounced) + filter to re-run filter
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(350)
                .distinctUntilChanged()
                .combine(_filterFlow) { search, filter -> Pair(search, filter) }
                .collect { (search, filter) ->
                    // apply filtering on background thread
                    applyLocalFilter(search ?: "", filter ?: "All")
                }
        }
    }

    /**
     * Optionally set a remote URL to fetch JSON (public open data). If set, `loadAndFilter()` will try remote then fallback.
     */
    fun setRemoteJsonUrl(url: String?) {
        _remoteUrl.value = url
    }

    /**
     * Public: called by UI when search text changes
     */
    fun onSearchChanged(text: String) {
        _searchQueryFlow.value = text
        // persist search to DataStore (optional)
        viewModelScope.launch { DataStoreManager.saveSearch(ctx, "colleges_search", text) }
        // update ui state's searchQuery quickly (without waiting for debounce)
        _uiState.update { it.copy(searchQuery = text) }
    }

    /**
     * Public: called when chip filter changes
     */
    fun onFilterSelected(filter: String) {
        _filterFlow.value = filter
        _uiState.update { it.copy(selectedFilter = filter) }
        // persist filter
        viewModelScope.launch { DataStoreManager.saveFilter(ctx, "colleges_filter", filter) }
    }

    /**
     * Load remote (if URL is set) else load local assets; then apply current search+filter
     */
    fun loadAndFilter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1) attempt remote if provided
                val remote = _remoteUrl.value
                if (!remote.isNullOrBlank()) {
                    val remoteList = fetchCollegesRemote(remote)
                    if (remoteList.isNotEmpty()) {
                        CollegeRepository.replaceCached(remoteList) // helper we'll add to repository
                    }
                }

                // 2) load local assets (or cached remote)
                val all = withContext(Dispatchers.IO) { CollegeRepository.loadAll(ctx) }
                // apply current immediate filter + search
                applyLocalFilter(_searchQueryFlow.value, _filterFlow.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load data") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchCollegesRemote(urlString: String): List<College> = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 8_000
            conn.readTimeout = 8_000
            conn.doInput = true
            conn.connect()
            val code = conn.responseCode
            if (code != 200) throw IOException("Remote fetch failed: $code")
            val stream = conn.inputStream.bufferedReader().use { it.readText() }
            // parse with kotlinx.serialization
            val list = Json { ignoreUnknownKeys = true }.decodeFromString<List<College>>(stream)
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun applyLocalFilter(search: String?, filter: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val all = CollegeRepository.loadAll(ctx)
                val filtered = all.filter { c ->
                    val matchesFilter = when (filter ?: "All") {
                        "All" -> true
                        else -> c.type?.equals(filter ?: "", ignoreCase = true) ?: true
                    }
                    val q = (search ?: "").trim().lowercase()
                    val matchesSearch = q.isEmpty() || (
                            c.name.lowercase().contains(q) ||
                                    (c.city?.lowercase()?.contains(q) ?: false) ||
                                    c.courses.any { it.lowercase().contains(q) }
                            )
                    matchesFilter && matchesSearch
                }
                _uiState.update { it.copy(items = filtered, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Filter failed") }
            }
        }
    }
}
