package com.example.quotevault.favorites



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.model.Quote
import com.example.quotevault.quotes.QuoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: QuoteRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Quote>>(emptyList())
    val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = repository.getFavoriteQuotes()
                if (result.isSuccess) {
                    _favorites.value = result.getOrNull() ?: emptyList()
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

    fun removeFromFavorites(quoteId: String) {
        viewModelScope.launch {
            try {
                val result = repository.removeFromFavorites(quoteId)
                if (result.isSuccess) {
                    // Reload favorites
                    loadFavorites()
                } else {
                    _error.value = "Failed to remove from favorites"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}