package com.adityayadav.careerguidancecommunity.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.ScholarshipRepository
import com.adityayadav.careerguidancecommunity.data.Scholarship
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
        viewModelScope.launch {
            loadAndFilter()
        }

        viewModelScope.launch {
            _searchQuery.debounce(350).distinctUntilChanged().combine(_categoryFlow) { s, c -> s to c }
                .collect { (s, c) -> applyFilter(s, c) }
        }
    }

    fun setRemoteJsonUrl(url: String?) { _remoteUrl.value = url }

    fun onSearchChanged(text: String) {
        _searchQuery.value = text
        _uiState.update { it.copy(searchQuery = text) }
        viewModelScope.launch { DataStoreManager.saveSearch(ctx, "scholarships_search", text) }
    }

    fun onCategorySelected(cat: String) {
        _categoryFlow.value = cat
        _uiState.update { it.copy(selectedCategory = cat) }
        viewModelScope.launch { DataStoreManager.saveFilter(ctx, "scholarships_filter", cat) }
    }

    fun loadAndFilter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val remote = _remoteUrl.value
                if (!remote.isNullOrBlank()) {
                    val remoteList = fetchRemote(remote)
                    if (remoteList.isNotEmpty()) ScholarshipRepository.replaceCached(remoteList)
                }
                applyFilter(_searchQuery.value, _categoryFlow.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed", isLoading = false) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchRemote(urlString: String) = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.connect()
            if (conn.responseCode != 200) return@withContext emptyList<Scholarship>()
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            Json { ignoreUnknownKeys = true }.decodeFromString<List<Scholarship>>(text)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun applyFilter(search: String?, category: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val all = ScholarshipRepository.loadAll(ctx)
                val filtered = all.filter { s ->
                    val matchesCat = (category == "All") || s.category.equals(category ?: "", ignoreCase = true)
                    val q = (search ?: "").trim().lowercase()
                    val matchesSearch = q.isEmpty() || s.name.lowercase().contains(q) || (s.eligibility?.lowercase()?.contains(q) ?: false)
                    matchesCat && matchesSearch
                }
                _uiState.update { it.copy(items = filtered, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Filter failed") }
            }
        }
    }
}
