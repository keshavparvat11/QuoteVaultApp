package com.example.quotevault.Screens



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.quotes.QuoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(QuoteCategory.MOTIVATION)
    val selectedCategory: StateFlow<QuoteCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    private val _dailyQuote = MutableStateFlow<Quote?>(null)
    val dailyQuote: StateFlow<Quote?> = _dailyQuote.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    init {
        loadDailyQuote()
        loadQuotes()
        loadFavorites()
    }

    fun selectCategory(category: QuoteCategory) {
        _selectedCategory.value = category
        loadQuotesByCategory(category)
    }

    fun searchQuotes(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val results = repository.searchQuotes(query)
                    _quotes.value = results
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            loadQuotes()
        }
    }

    fun loadDailyQuote() {
        viewModelScope.launch {
            try {
                _dailyQuote.value = repository.getQuoteOfTheDay()
            } catch (e: Exception) {
                // Use fallback from local
            }
        }
    }

    fun loadQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _quotes.value = repository.getQuotes()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadQuotesByCategory(category: QuoteCategory) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _quotes.value = repository.getQuotesByCategory(category)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getUserFavorites().collect { favoritesList ->
                _favorites.value = favoritesList.toSet()
            }
        }
    }

    fun toggleFavorite(quoteId: String) {
        viewModelScope.launch {
            val isFavorite = _favorites.value.contains(quoteId)
            try {
                if (isFavorite) {
                    repository.removeFromFavorites(quoteId)
                } else {
                    repository.addToFavorites(quoteId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to update favorites"
            }
        }
    }

    fun refresh() {
        loadQuotes()
        loadDailyQuote()
    }
}