package com.example.quotevault.Screens



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.UserPreferences


import com.example.quotevault.quotes.QuoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                    val result = repository.searchQuotes(query)
                    if (result.isSuccess) {
                        _quotes.value = result.getOrNull() ?: emptyList()
                    } else {
                        _error.value = result.exceptionOrNull()?.message
                    }
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
            _isLoading.value = true
            try {
                val result = repository.getQuoteOfTheDay()
                if (result.isSuccess) {
                    _dailyQuote.value = result.getOrNull()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getQuotes()
                if (result.isSuccess) {
                    _quotes.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.d("HomeViewModel", "Error loading quotes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadQuotesByCategory(category: QuoteCategory) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getQuotesByCategory(category)
                if (result.isSuccess) {
                    _quotes.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
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
                    val result = repository.removeFromFavorites(quoteId)
                    if (result.isSuccess) {
                        _favorites.value = _favorites.value - quoteId
                    } else {
                        _error.value = "Failed to remove from favorites"
                    }
                } else {
                    val result = repository.addToFavorites(quoteId)
                    if (result.isSuccess) {
                        _favorites.value = _favorites.value + quoteId
                    } else {
                        _error.value = "Failed to add to favorites"
                    }
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